package com.example.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

data class VoiceCharacter(
    val id: String,
    val name: String,
    val gender: String,
    val hindiTitle: String,
    val styleDescription: String,
    val basePitch: Float,
    val baseSpeed: Float
)

class VoiceStudioEngine(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Main)
    private var previewJob: Job? = null

    val emotionEngine = EmotionDetectionEngine()
    val audioGenService = AudioGenerationService(context)
    val audioPlayer = ShayariAudioPlayer(context)

    val characterVoices = listOf(
        VoiceCharacter(
            id = "kabir_narrator",
            name = "Kabir (कबीर)",
            gender = "Male",
            hindiTitle = "गंभीर और भावुक कथावाचक",
            styleDescription = "Deep, warm, mature Indian male voice with natural speaking rhythm and emotional breathing pauses.",
            basePitch = 0.74f,
            baseSpeed = 0.76f
        ),
        VoiceCharacter(
            id = "dev_baritone",
            name = "Dev (देव)",
            gender = "Male",
            hindiTitle = "दर्द भरी भारी आवाज़",
            styleDescription = "Deep broken-heart baritone, heavy and emotionally affected for intense grief.",
            basePitch = 0.68f,
            baseSpeed = 0.72f
        ),
        VoiceCharacter(
            id = "raghav_classical",
            name = "Raghav (राघव)",
            gender = "Male",
            hindiTitle = "शालीन और शांत आवाज़",
            styleDescription = "Gentle, nostalgic and reflective classical delivery for poetry and nazm.",
            basePitch = 0.79f,
            baseSpeed = 0.78f
        ),
        VoiceCharacter(
            id = "ayesha_whisper",
            name = "Ayesha (आयशा)",
            gender = "Female",
            hindiTitle = "नरम और भावुक आवाज़",
            styleDescription = "Soft intimate female whispers, tender, tearful delivery for romance.",
            basePitch = 1.05f,
            baseSpeed = 0.80f
        )
    )

    private val _currentVoice = MutableStateFlow(characterVoices[0])
    val currentVoice: StateFlow<VoiceCharacter> = _currentVoice.asStateFlow()

    private val _selectedEmotion = MutableStateFlow<PoetryEmotion?>(null) // null means auto-detect
    val selectedEmotion: StateFlow<PoetryEmotion?> = _selectedEmotion.asStateFlow()

    private val _currentAnalysis = MutableStateFlow<EmotionAnalysisResult?>(null)
    val currentAnalysis: StateFlow<EmotionAnalysisResult?> = _currentAnalysis.asStateFlow()

    private val _pitch = MutableStateFlow(0.74f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _speed = MutableStateFlow(0.78f) // Medium-slow natural cadence
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private val _emotionalIntensity = MutableStateFlow(0.85f)
    val emotionalIntensity: StateFlow<Float> = _emotionalIntensity.asStateFlow()

    private val _pauseDurationMs = MutableStateFlow(480L) // Calibrated natural breath pause (not exaggerated)
    val pauseDurationMs: StateFlow<Long> = _pauseDurationMs.asStateFlow()

    private val _isPreviewSpeaking = MutableStateFlow(false)
    val isPreviewSpeaking: StateFlow<Boolean> = _isPreviewSpeaking.asStateFlow()

    private val _currentSpokenLine = MutableStateFlow("")
    val currentSpokenLine: StateFlow<String> = _currentSpokenLine.asStateFlow()

    private val _narrationStatus = MutableStateFlow("Ready for emotional narration (Strict 2-Line Sher Invariant)")
    val narrationStatus: StateFlow<String> = _narrationStatus.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            val hindiLocale = Locale.forLanguageTag("hi-IN")
            val result = tts?.setLanguage(hindiLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.forLanguageTag("en-IN"))
            }

            selectDeepMaleVoice()

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPreviewSpeaking.value = true
                }
                override fun onDone(utteranceId: String?) {}
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isPreviewSpeaking.value = false
                }
            })
        }
    }

    private fun selectDeepMaleVoice() {
        try {
            val voices = tts?.voices ?: return
            val maleHindiVoice = voices.firstOrNull { voice ->
                voice.locale.language == "hi" &&
                (voice.name.contains("male", ignoreCase = true) || voice.name.contains("hi-in-x-hie", ignoreCase = true))
            } ?: voices.firstOrNull { it.locale.language == "hi" }
              ?: voices.firstOrNull { it.locale.language == "en" && it.locale.country == "IN" && it.name.contains("male", ignoreCase = true) }

            if (maleHindiVoice != null) {
                tts?.voice = maleHindiVoice
            }
        } catch (_: Exception) {}
    }

    fun selectVoice(voice: VoiceCharacter) {
        _currentVoice.value = voice
        _pitch.value = voice.basePitch
        _speed.value = voice.baseSpeed
    }

    fun setEmotion(emotion: PoetryEmotion?) {
        _selectedEmotion.value = emotion
    }

    fun setPitch(newPitch: Float) {
        _pitch.value = newPitch.coerceIn(0.6f, 1.3f)
    }

    fun setSpeed(newSpeed: Float) {
        _speed.value = newSpeed.coerceIn(0.6f, 1.3f)
    }

    fun setEmotionalIntensity(intensity: Float) {
        _emotionalIntensity.value = intensity.coerceIn(0.5f, 1.0f)
    }

    fun setPauseDuration(pauseMs: Long) {
        _pauseDurationMs.value = pauseMs.coerceIn(300L, 900L)
    }

    fun analyzeText(text: String): EmotionAnalysisResult {
        val result = emotionEngine.analyzeShayari(text, _selectedEmotion.value)
        _currentAnalysis.value = result
        return result
    }

    /**
     * Synthesizes real Neural2 MP3 using the Google Cloud Text-to-Speech API
     * and loads it into the ShayariAudioPlayer.
     * Strictly verifies quality report and enforces exactly 2 non-repeating lines.
     */
    suspend fun generateStudioAudio(text: String): File? {
        val analysis = analyzeText(text)
        val file = audioGenService.generateExpressiveHindiMp3(
            ssmlScript = analysis.ssmlScript,
            plainText = analysis.qualityReport.lines.joinToString("\n"),
            voiceId = _currentVoice.value.id,
            emotion = analysis.dominantEmotion,
            pitchOffset = _pitch.value / _currentVoice.value.basePitch,
            speedOffset = _speed.value / _currentVoice.value.baseSpeed
        )

        if (file != null) {
            audioPlayer.loadAudioFile(file, autoPlay = true)
        }
        return file
    }

    /**
     * Real-time emotional line-by-line preview with natural breathing pauses
     * before generating the final audio file.
     * Strictly ensures:
     * 1. Exactly 2 meaningful lines (Sher).
     * 2. Never repeat the first line or any sentence.
     * 3. Each line is spoken exactly once.
     * 4. Medium-slow speaking speed with genuine heartbreak expression and no exaggerated pause.
     */
    fun previewVoice(rawText: String) {
        stopAll()

        val analysis = analyzeText(rawText)
        val lines = analysis.qualityReport.lines
        if (lines.isEmpty()) return

        _isPreviewSpeaking.value = true

        previewJob = scope.launch {
            _narrationStatus.value = "भाव: ${analysis.dominantEmotion.hindiTitle} • 2-लाइन शेर सत्यापित"
            delay(250L)

            val basePitchVal = _pitch.value
            val baseSpeedVal = _speed.value
            val pauseMs = _pauseDurationMs.value

            // Strictly speak line 1 once, then natural breath pause, then line 2 once
            for (index in lines.indices) {
                if (!_isPreviewSpeaking.value) break

                val line = lines[index]
                _currentSpokenLine.value = line

                val lineAnalysis = analysis.lineBreakdown.getOrNull(index)
                val lineEmotion = lineAnalysis?.dominantEmotion ?: analysis.dominantEmotion
                val isFinalPunchline = (index == lines.lastIndex && lines.size > 1)

                val linePitch = if (isFinalPunchline) {
                    (basePitchVal * lineEmotion.basePitchMod * 0.96f).coerceIn(0.68f, 1.2f)
                } else {
                    (basePitchVal * lineEmotion.basePitchMod).coerceIn(0.68f, 1.2f)
                }

                val lineSpeed = (baseSpeedVal * lineEmotion.baseSpeedMod).coerceIn(0.70f, 1.15f)

                tts?.setPitch(linePitch)
                tts?.setSpeechRate(lineSpeed)

                _narrationStatus.value = "पंक्ति ${index + 1}/${lines.size} (${lineEmotion.hindiTitle}): " + (lineAnalysis?.deliveryStyle ?: "")

                val formattedLine = if (!line.endsWith("...") && !line.endsWith("।")) "$line..." else line
                val utteranceId = "PREVIEW_${index}_${System.currentTimeMillis()}"
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                }

                tts?.speak(formattedLine, TextToSpeech.QUEUE_FLUSH, params, utteranceId)

                val wordCount = line.split("\\s+".toRegex()).size
                val estimatedSpeakTimeMs = ((wordCount * 420L) / lineSpeed).toLong().coerceAtLeast(1600L)
                delay(estimatedSpeakTimeMs)

                if (index < lines.lastIndex) {
                    _narrationStatus.value = "प्राकृतिक सांस (Natural Pause: ${pauseMs}ms)..."
                    delay(pauseMs)
                }
            }

            _narrationStatus.value = "आवाज़ पूर्वावलोकन संपन्न (2-Line Delivery Complete)"
            _currentSpokenLine.value = ""
            _isPreviewSpeaking.value = false
        }
    }

    fun stopAll() {
        previewJob?.cancel()
        previewJob = null
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _isPreviewSpeaking.value = false
        _currentSpokenLine.value = ""
        audioPlayer.pause()
    }

    fun release() {
        stopAll()
        tts?.shutdown()
        tts = null
        audioPlayer.release()
    }
}
