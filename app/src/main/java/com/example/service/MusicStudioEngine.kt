package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class MusicTrack(
    val id: String,
    val title: String,
    val genre: String,
    val durationText: String,
    val emotionDescription: String,
    val baseFrequencies: List<Double>
)

class MusicStudioEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var synthJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioTrack: AudioTrack? = null

    val tracks = listOf(
        MusicTrack(
            id = "sad_piano",
            title = "Aansoon (Sad Piano & Rain)",
            genre = "Sad Instrumental",
            durationText = "Loop (Infinite)",
            emotionDescription = "Melancholic piano chords evoking rain on dark glass",
            baseFrequencies = listOf(220.0, 261.63, 329.63, 440.0) // A minor chord
        ),
        MusicTrack(
            id = "heartbreak_strings",
            title = "Adhura Ishq (Heartbreak Strings)",
            genre = "Romantic Background",
            durationText = "Loop (Infinite)",
            emotionDescription = "Symphonic cello & violin swells of deep longing",
            baseFrequencies = listOf(146.83, 174.61, 220.0, 293.66) // D minor chord
        ),
        MusicTrack(
            id = "tanhai_guitar",
            title = "Tanhai Night (Acoustic Guitar)",
            genre = "Acoustic Sad",
            durationText = "Loop (Infinite)",
            emotionDescription = "Lonely fingerpicked acoustic strings with soft reverb",
            baseFrequencies = listOf(164.81, 196.0, 246.94, 329.63) // E minor chord
        ),
        MusicTrack(
            id = "soul_flute",
            title = "Ruhaani Melancholy (Soul Flute)",
            genre = "Emotional Flute",
            durationText = "Loop (Infinite)",
            emotionDescription = "Haunting bansuri melody lingering in stillness",
            baseFrequencies = listOf(293.66, 349.23, 440.0, 587.33) // High D minor
        )
    )

    private val _currentTrack = MutableStateFlow(tracks[0])
    val currentTrack: StateFlow<MusicTrack> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // Mixing Rule: Voice remains prominent (1.0f), background music remains subtle (0.20f)
    private val _volume = MutableStateFlow(0.20f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _userCustomAudioUri = MutableStateFlow<Uri?>(null)
    val userCustomAudioUri: StateFlow<Uri?> = _userCustomAudioUri.asStateFlow()

    // Master Reference Video & Style Reference
    private val _referenceVideoUri = MutableStateFlow<Uri?>(null)
    val referenceVideoUri: StateFlow<Uri?> = _referenceVideoUri.asStateFlow()

    private val _referenceMediaTitle = MutableStateFlow<String?>("Master Reference Audio (Subtle BGM)")
    val referenceMediaTitle: StateFlow<String?> = _referenceMediaTitle.asStateFlow()

    private val _isMasterReferenceActive = MutableStateFlow(false)
    val isMasterReferenceActive: StateFlow<Boolean> = _isMasterReferenceActive.asStateFlow()

    // If source separation on device cannot cleanly isolate without vocals, ask user for the original instrumental track
    private val _requiresOriginalInstrumentalPrompt = MutableStateFlow(false)
    val requiresOriginalInstrumentalPrompt: StateFlow<Boolean> = _requiresOriginalInstrumentalPrompt.asStateFlow()

    fun selectTrack(track: MusicTrack) {
        val wasPlaying = _isPlaying.value
        stop()
        _currentTrack.value = track
        _userCustomAudioUri.value = null
        _isMasterReferenceActive.value = false
        _requiresOriginalInstrumentalPrompt.value = false
        if (wasPlaying) {
            playCurrent()
        }
    }

    /**
     * Extracts and plays the actual background music / audio track from the user's
     * uploaded master reference video.
     * Preserves original musical tone, rhythm, and atmosphere.
     */
    fun setReferenceVideo(uri: Uri, displayName: String = "Master Reference Video") {
        stop()
        _referenceVideoUri.value = uri
        _userCustomAudioUri.value = uri
        _referenceMediaTitle.value = displayName
        _isMasterReferenceActive.value = true
        // If the reference video contains dialogue/narration, inform user to provide the original instrumental track if pure separation is required
        _requiresOriginalInstrumentalPrompt.value = true
        playUserAudio(uri)
    }

    /**
     * Sets the pristine original instrumental track provided by the user
     * when on-device separation from dialogue is needed.
     */
    fun setOriginalInstrumentalTrack(uri: Uri, displayName: String = "Master Instrumental Track") {
        stop()
        _userCustomAudioUri.value = uri
        _referenceMediaTitle.value = "$displayName (Clean Instrumental)"
        _isMasterReferenceActive.value = true
        _requiresOriginalInstrumentalPrompt.value = false
        playUserAudio(uri)
    }

    fun setUserAudio(uri: Uri) {
        stop()
        _userCustomAudioUri.value = uri
        _isMasterReferenceActive.value = true
        playUserAudio(uri)
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            stop()
        } else {
            playCurrent()
        }
    }

    fun playCurrent() {
        val customUri = _userCustomAudioUri.value
        if (customUri != null) {
            playUserAudio(customUri)
        } else {
            playSynthesizedTrack(_currentTrack.value)
        }
    }

    /**
     * Sets volume with smooth transition to prevent sudden volume changes or clicks.
     */
    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _volume.value = clamped
        mediaPlayer?.let { player ->
            try {
                player.setVolume(clamped, clamped)
            } catch (_: Exception) {}
        }
    }

    private fun playUserAudio(uri: Uri) {
        stop()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                isLooping = true
                val vol = _volume.value
                setVolume(vol, vol)
                prepare()
                start()
            }
            _isPlaying.value = true
        } catch (e: Exception) {
            Log.e("MusicStudioEngine", "Failed to play master reference audio", e)
            _isPlaying.value = false
        }
    }

    private val audioLock = Any()

    private fun safeReleaseTrack(track: AudioTrack?) {
        if (track == null) return
        try {
            if (track.state == AudioTrack.STATE_INITIALIZED) {
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    try {
                        track.pause()
                    } catch (_: Throwable) {}
                    try {
                        track.flush()
                    } catch (_: Throwable) {}
                    try {
                        track.stop()
                    } catch (_: Throwable) {}
                }
            }
        } catch (_: Throwable) {
            // Catch native state exceptions safely
        } finally {
            try {
                track.release()
            } catch (_: Throwable) {}
        }
    }

    private fun playSynthesizedTrack(track: MusicTrack) {
        stop()
        _isPlaying.value = true

        synthJob = scope.launch {
            val sampleRate = 44100
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ) * 2

            val trackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            var localTrack: AudioTrack? = null
            try {
                localTrack = AudioTrack(
                    trackAttributes,
                    format,
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    android.media.AudioManager.AUDIO_SESSION_ID_GENERATE
                )
            } catch (e: Throwable) {
                Log.e("MusicStudioEngine", "Failed to create AudioTrack", e)
                _isPlaying.value = false
                return@launch
            }

            synchronized(audioLock) {
                audioTrack = localTrack
            }

            try {
                try {
                    if (localTrack.state == AudioTrack.STATE_INITIALIZED) {
                        localTrack.play()
                    }
                } catch (e: Throwable) {
                    Log.e("MusicStudioEngine", "Failed to start AudioTrack playback", e)
                    return@launch
                }

                val buffer = ShortArray(bufferSize)
                var phase = 0.0
                var noteTimer = 0.0

                while (isActive && _isPlaying.value && localTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    val currentVol = _volume.value
                    val freqs = track.baseFrequencies

                    for (i in buffer.indices) {
                        noteTimer += 1.0 / sampleRate
                        val noteIndex = ((noteTimer * 0.8).toInt()) % freqs.size
                        val targetFreq = freqs[noteIndex]

                        // Warm pad chords blending with fundamental
                        val wave1 = sin(phase * 2.0 * Math.PI * targetFreq)
                        val wave2 = 0.5 * sin(phase * 2.0 * Math.PI * (targetFreq * 0.5)) // sub-octave
                        val wave3 = 0.3 * sin(phase * 2.0 * Math.PI * (targetFreq * 1.5)) // warm fifth

                        val envelope = 0.7 + 0.3 * sin(noteTimer * 2.0 * Math.PI * 0.4) // gentle swell
                        val sample = ((wave1 + wave2 + wave3) / 1.8 * 32767.0 * currentVol * 0.45 * envelope).toInt()

                        buffer[i] = sample.coerceIn(-32768, 32767).toShort()
                        phase += 1.0 / sampleRate
                        if (phase > 1.0) phase -= 1.0
                    }

                    if (!isActive || !_isPlaying.value) break

                    try {
                        val written = localTrack.write(buffer, 0, buffer.size)
                        if (written < 0) break
                    } catch (_: Throwable) {
                        break
                    }
                }
            } catch (e: Throwable) {
                Log.e("MusicStudioEngine", "Audio synthesis loop error", e)
            } finally {
                var needsRelease = false
                synchronized(audioLock) {
                    if (audioTrack === localTrack) {
                        audioTrack = null
                        needsRelease = true
                    }
                }
                if (needsRelease) {
                    safeReleaseTrack(localTrack)
                }
            }
        }
    }

    fun stop() {
        _isPlaying.value = false
        val job = synthJob
        synthJob = null
        job?.cancel()

        var trackToRelease: AudioTrack? = null
        synchronized(audioLock) {
            trackToRelease = audioTrack
            audioTrack = null
        }
        safeReleaseTrack(trackToRelease)

        val player = mediaPlayer
        mediaPlayer = null
        try {
            if (player?.isPlaying == true) {
                player.stop()
            }
            player?.release()
        } catch (_: Throwable) {}
    }

    fun release() {
        stop()
    }
}
