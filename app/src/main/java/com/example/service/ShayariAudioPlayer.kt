package com.example.service

import android.content.ContentValues
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

class ShayariAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _progressFraction = MutableStateFlow(0f)
    val progressFraction: StateFlow<Float> = _progressFraction.asStateFlow()

    private val _currentAudioFile = MutableStateFlow<File?>(null)
    val currentAudioFile: StateFlow<File?> = _currentAudioFile.asStateFlow()

    fun loadAudioFile(file: File, autoPlay: Boolean = true) {
        stop()
        _currentAudioFile.value = file

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    val dur = mp.duration.coerceAtLeast(1000)
                    _durationMs.value = dur
                    if (autoPlay) {
                        mp.start()
                        _isPlaying.value = true
                        startProgressTracker()
                    }
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPositionMs.value = _durationMs.value
                    _progressFraction.value = 1f
                    stopProgressTracker()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("ShayariAudioPlayer", "Failed to load audio file", e)
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        val mp = mediaPlayer ?: return
        if (!mp.isPlaying) {
            if (_progressFraction.value >= 0.99f) {
                mp.seekTo(0)
            }
            mp.start()
            _isPlaying.value = true
            startProgressTracker()
        }
    }

    fun pause() {
        val mp = mediaPlayer ?: return
        if (mp.isPlaying) {
            mp.pause()
            _isPlaying.value = false
            stopProgressTracker()
        }
    }

    fun replay() {
        val mp = mediaPlayer ?: return
        mp.seekTo(0)
        _currentPositionMs.value = 0
        _progressFraction.value = 0f
        mp.start()
        _isPlaying.value = true
        startProgressTracker()
    }

    fun seekToFraction(fraction: Float) {
        val mp = mediaPlayer ?: return
        val clamped = fraction.coerceIn(0f, 1f)
        val targetMs = (clamped * _durationMs.value).toInt()
        mp.seekTo(targetMs)
        _currentPositionMs.value = targetMs
        _progressFraction.value = clamped
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _isPlaying.value) {
                val mp = mediaPlayer
                if (mp != null && mp.isPlaying) {
                    val pos = mp.currentPosition
                    val dur = mp.duration.coerceAtLeast(1)
                    _currentPositionMs.value = pos
                    _durationMs.value = dur
                    _progressFraction.value = (pos.toFloat() / dur.toFloat()).coerceIn(0f, 1f)
                }
                delay(100L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun downloadCurrentMp3(fileName: String = "Mohabbat_Narration") {
        val file = _currentAudioFile.value
        if (file == null || !file.exists()) {
            Toast.makeText(context, "No audio file available to download", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, "${fileName}_${System.currentTimeMillis()}.mp3")
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                    put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/MohabbatAI")
                }
                val uri: Uri? = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { os ->
                        FileInputStream(file).use { fis ->
                            fis.copyTo(os)
                        }
                    }
                    Toast.makeText(context, "MP3 Saved to Music / MohabbatAI 🎧", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Saved to App Files: ${file.name}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Saved to Library: ${file.name}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ShayariAudioPlayer", "Download error", e)
            Toast.makeText(context, "Audio ready at: ${file.name}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stop() {
        stopProgressTracker()
        _isPlaying.value = false
        val player = mediaPlayer
        mediaPlayer = null
        try {
            if (player?.isPlaying == true) {
                player.stop()
            }
            player?.release()
        } catch (_: Throwable) {}
        _progressFraction.value = 0f
        _currentPositionMs.value = 0
    }

    fun release() {
        stop()
    }
}
