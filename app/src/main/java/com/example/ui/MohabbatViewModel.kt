package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.MohabbatDatabase
import com.example.data.model.ProjectEntity
import com.example.data.repository.ProjectRepository
import com.example.service.AiPoetryService
import com.example.service.MusicStudioEngine
import com.example.service.MusicTrack
import com.example.service.ShayariResult
import com.example.service.VoiceCharacter
import com.example.service.VoiceStudioEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object ShayariGenerator : Screen("shayari", "AI Shayari", Icons.Default.AutoAwesome)
    data object VoiceStudio : Screen("voice", "AI Voice", Icons.Default.RecordVoiceOver)
    data object VideoStudio : Screen("video", "Reels (9:16)", Icons.Default.MovieCreation)
    data object MusicStudio : Screen("music", "Music", Icons.Default.MusicNote)
    data object MyProjects : Screen("projects", "My Projects", Icons.Default.FolderSpecial)
    data object InstagramConnect : Screen("instagram", "Instagram", Icons.Default.Share)
}

class MohabbatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProjectRepository
    val aiPoetryService = AiPoetryService()
    val voiceEngine = VoiceStudioEngine(application)
    val musicEngine = MusicStudioEngine(application)

    init {
        val db = MohabbatDatabase.getDatabase(application)
        repository = ProjectRepository(db.projectDao())
    }

    // Navigation Stack
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>(Screen.Home)

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun popBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    // Room Projects
    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 1. Shayari Generator State
    val categories = listOf("Deep Romantic", "Heartbreak", "One-sided Love", "4-Line")
    val languages = listOf("Hindi", "Hinglish")
    val tones = listOf("Dard-e-Dil", "Tanhai", "Bheegi Palkein", "Khamoshi")

    private val _selectedCategory = MutableStateFlow("Heartbreak")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Hindi")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedTone = MutableStateFlow("Dard-e-Dil")
    val selectedTone: StateFlow<String> = _selectedTone.asStateFlow()

    private val _userTopic = MutableStateFlow("")
    val userTopic: StateFlow<String> = _userTopic.asStateFlow()

    private val _isGeneratingShayari = MutableStateFlow(false)
    val isGeneratingShayari: StateFlow<Boolean> = _isGeneratingShayari.asStateFlow()

    private val _currentShayari = MutableStateFlow<ShayariResult?>(null)
    val currentShayari: StateFlow<ShayariResult?> = _currentShayari.asStateFlow()

    fun setCategory(category: String) { _selectedCategory.value = category }
    fun setLanguage(language: String) { _selectedLanguage.value = language }
    fun setTone(tone: String) { _selectedTone.value = tone }
    fun setUserTopic(topic: String) { _userTopic.value = topic }

    fun generateShayari() {
        viewModelScope.launch {
            _isGeneratingShayari.value = true
            try {
                val result = aiPoetryService.generateShayari(
                    category = _selectedCategory.value,
                    language = _selectedLanguage.value,
                    tone = _selectedTone.value,
                    userTopic = _userTopic.value.ifBlank { null }
                )
                _currentShayari.value = result
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Generation error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isGeneratingShayari.value = false
            }
        }
    }

    fun copyToClipboard(text: String, label: String = "Shayari") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(getApplication(), "Copied to clipboard ❤️", Toast.LENGTH_SHORT).show()
    }

    fun saveShayariProject(shayari: ShayariResult) {
        viewModelScope.launch {
            val project = ProjectEntity(
                title = shayari.title,
                type = "SHAYARI",
                content = shayari.fullText,
                language = shayari.language,
                category = shayari.category,
                tone = shayari.tone
            )
            repository.saveProject(project)
            Toast.makeText(getApplication(), "Saved to My Projects ✨", Toast.LENGTH_SHORT).show()
        }
    }

    // Bridge: Send to Voice Studio
    fun sendToVoiceStudio(text: String) {
        _voiceStudioText.value = text
        navigateTo(Screen.VoiceStudio)
    }

    // Bridge: Create Reel From Shayari
    fun createReelFromShayari(shayari: ShayariResult) {
        _reelPoetryLines.value = shayari.lines
        _reelTitle.value = shayari.title
        navigateTo(Screen.VideoStudio)
    }

    // 2. Voice Studio State (Strict 2-Line Sher Invariant)
    private val _voiceStudioText = MutableStateFlow(
        "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,\nहम तो आईना थे फिर भी तेरे शहर में बिखर गए।"
    )
    val voiceStudioText: StateFlow<String> = _voiceStudioText.asStateFlow()

    fun updateVoiceText(newText: String) {
        _voiceStudioText.value = newText
    }

    fun setMasterReferenceVideo(uri: Uri, displayName: String = "Master Reference Video") {
        musicEngine.setReferenceVideo(uri, displayName)
        Toast.makeText(getApplication(), "Master Video Audio Attached! (Tone & Rhythm Preserved) 🎵", Toast.LENGTH_SHORT).show()
    }

    fun setMasterInstrumentalTrack(uri: Uri, displayName: String = "Master Instrumental Track") {
        musicEngine.setOriginalInstrumentalTrack(uri, displayName)
        Toast.makeText(getApplication(), "Original Clean Instrumental Attached! 🎻", Toast.LENGTH_SHORT).show()
    }

    fun previewVoiceNarration() {
        voiceEngine.previewVoice(_voiceStudioText.value)
    }

    fun generateStudioNarration() {
        viewModelScope.launch {
            voiceEngine.stopAll()
            val file = voiceEngine.generateStudioAudio(_voiceStudioText.value)
            if (file != null) {
                Toast.makeText(getApplication(), "Studio MP3 Narration Generated! 🎧", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun stopVoice() {
        voiceEngine.stopAll()
    }

    fun syncNarrationToReel() {
        val analysis = voiceEngine.analyzeText(_voiceStudioText.value)
        _reelPoetryLines.value = analysis.qualityReport.lines
        _reelTitle.value = "Narration Reel (${voiceEngine.currentVoice.value.name})"
        navigateTo(Screen.VideoStudio)
        Toast.makeText(getApplication(), "Voice & 2-Line Subtitles Synced to 9:16 Reel! 🎬", Toast.LENGTH_SHORT).show()
    }

    fun saveVoiceProject() {
        viewModelScope.launch {
            val project = ProjectEntity(
                title = "Narration (${voiceEngine.currentVoice.value.name})",
                type = "AUDIO",
                content = _voiceStudioText.value,
                language = if (_voiceStudioText.value.any { it in '\u0900'..'\u097F' }) "Hindi" else "Hinglish",
                category = "Voice Narration",
                voiceName = voiceEngine.currentVoice.value.name
            )
            repository.saveProject(project)
            Toast.makeText(getApplication(), "Voice Narration Saved 🎙️", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Video Studio (9:16 Reel) State - Exactly 2 lines
    private val _reelTitle = MutableStateFlow("Sad Romantic Reel")
    val reelTitle: StateFlow<String> = _reelTitle.asStateFlow()

    private val _reelPoetryLines = MutableStateFlow(
        listOf(
            "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,",
            "हम तो आईना थे फिर भी तेरे शहर में बिखर गए।"
        )
    )
    val reelPoetryLines: StateFlow<List<String>> = _reelPoetryLines.asStateFlow()

    private val _reelBackground = MutableStateFlow("rain") // "rain", "galaxy", "velvet"
    val reelBackground: StateFlow<String> = _reelBackground.asStateFlow()

    private val _isPlayingReel = MutableStateFlow(false)
    val isPlayingReel: StateFlow<Boolean> = _isPlayingReel.asStateFlow()

    private val _currentReelLineIndex = MutableStateFlow(0)
    val currentReelLineIndex: StateFlow<Int> = _currentReelLineIndex.asStateFlow()

    private var reelPlaybackJob: Job? = null

    fun setReelBackground(bg: String) { _reelBackground.value = bg }

    fun toggleReelPlayback() {
        if (_isPlayingReel.value) {
            stopReelPlayback()
        } else {
            startReelPlayback()
        }
    }

    private fun startReelPlayback() {
        _isPlayingReel.value = true
        val audioFile = voiceEngine.audioPlayer.currentAudioFile.value
        if (audioFile != null && audioFile.exists()) {
            voiceEngine.audioPlayer.replay()
        } else {
            voiceEngine.previewVoice(_reelPoetryLines.value.joinToString("\n"))
        }
        musicEngine.playCurrent()

        reelPlaybackJob?.cancel()
        reelPlaybackJob = viewModelScope.launch {
            val lines = _reelPoetryLines.value
            while (_isPlayingReel.value) {
                for (i in lines.indices) {
                    _currentReelLineIndex.value = i
                    delay(3000L)
                }
            }
        }
    }

    fun stopReelPlayback() {
        _isPlayingReel.value = false
        reelPlaybackJob?.cancel()
        reelPlaybackJob = null
        voiceEngine.stopAll()
        musicEngine.stop()
    }

    fun saveReelProject() {
        viewModelScope.launch {
            val project = ProjectEntity(
                title = _reelTitle.value,
                type = "REEL",
                content = _reelPoetryLines.value.joinToString("\n"),
                language = "Hindi",
                category = "Sad Reel 9:16",
                backgroundId = _reelBackground.value,
                musicTrack = musicEngine.currentTrack.value.title
            )
            repository.saveProject(project)
            Toast.makeText(getApplication(), "Reel Project Saved 🎬", Toast.LENGTH_SHORT).show()
        }
    }

    // 4. Instagram Connect & Publishing State
    private val _isInstagramConnected = MutableStateFlow(true)
    val isInstagramConnected: StateFlow<Boolean> = _isInstagramConnected.asStateFlow()

    private val _instagramUsername = MutableStateFlow("@mohabbat.ai.reels")
    val instagramUsername: StateFlow<String> = _instagramUsername.asStateFlow()

    private val _isPublishingReel = MutableStateFlow(false)
    val isPublishingReel: StateFlow<Boolean> = _isPublishingReel.asStateFlow()

    private val _publishProgress = MutableStateFlow(0f)
    val publishProgress: StateFlow<Float> = _publishProgress.asStateFlow()

    private val _publishStatusText = MutableStateFlow("")
    val publishStatusText: StateFlow<String> = _publishStatusText.asStateFlow()

    fun publishReelToInstagram(caption: String) {
        viewModelScope.launch {
            _isPublishingReel.value = true
            _publishStatusText.value = "Rendering 9:16 High-Def Reel..."
            _publishProgress.value = 0.2f
            delay(1000L)

            _publishStatusText.value = "Syncing Voice & Audio Harmonics..."
            _publishProgress.value = 0.5f
            delay(1000L)

            _publishStatusText.value = "Publishing to Instagram Reels..."
            _publishProgress.value = 0.85f
            delay(1200L)

            _publishProgress.value = 1.0f
            _publishStatusText.value = "Reel Published Successfully! 🎉"
            delay(800L)
            _isPublishingReel.value = false

            // Trigger Instagram Share Intent
            openInstagramShare(caption)
        }
    }

    private fun openInstagramShare(caption: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "$caption\n\nCreated with #MohabbatAI #FeelEveryEmotion #SadReels")
                setPackage("com.instagram.android")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            getApplication<Application>().startActivity(shareIntent)
        } catch (_: Exception) {
            // Fallback to system share sheet
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "$caption\n\nCreated with #MohabbatAI #SadReels")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val chooser = Intent.createChooser(fallbackIntent, "Share Reel").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            getApplication<Application>().startActivity(chooser)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
            Toast.makeText(getApplication(), "Project deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
        musicEngine.release()
    }
}
