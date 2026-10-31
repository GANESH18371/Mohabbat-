package com.example.service

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

sealed interface AudioGenState {
    data object Idle : AudioGenState
    data class Generating(val progressText: String) : AudioGenState
    data class Success(val mp3File: File, val durationEstimateSeconds: Int) : AudioGenState
    data class Error(val errorMessage: String, val isApiKeyMissing: Boolean = false) : AudioGenState
}

class AudioGenerationService(private val context: Context) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    private val _genState = MutableStateFlow<AudioGenState>(AudioGenState.Idle)
    val genState: StateFlow<AudioGenState> = _genState.asStateFlow()

    private val _lastGeneratedFile = MutableStateFlow<File?>(null)
    val lastGeneratedFile: StateFlow<File?> = _lastGeneratedFile.asStateFlow()

    suspend fun generateExpressiveHindiMp3(
        ssmlScript: String,
        plainText: String,
        voiceId: String,
        emotion: PoetryEmotion,
        pitchOffset: Float,
        speedOffset: Float
    ): File? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val errorMsg = "API Key not configured. To generate studio-grade Neural2 MP3 audio, configure your API key in the AI Studio Secrets panel. You can still preview the emotional voice using the Voice Preview button below."
            _genState.value = AudioGenState.Error(errorMsg, isApiKeyMissing = true)
            return@withContext null
        }

        _genState.value = AudioGenState.Generating("Connecting to Neural Hindi Speech Engine...")

        try {
            // Select Google Cloud Neural2 / Wavenet Voice for Deep Indian Male
            val googleVoiceName = when (voiceId) {
                "dev_baritone" -> "hi-IN-Wavenet-B" // Deep Raspy Baritone
                "ayesha_whisper" -> "hi-IN-Neural2-A" // Soft Female
                "meera_soul" -> "hi-IN-Wavenet-A" // Classical Female
                else -> "hi-IN-Neural2-B" // Deep Indian Male (Mature & Warm)
            }

            val genderStr = if (voiceId.contains("ayesha") || voiceId.contains("meera")) "FEMALE" else "MALE"

            _genState.value = AudioGenState.Generating("Applying emotional inflection & prosody for ${emotion.hindiTitle}...")

            // Construct JSON payload for Google Cloud TTS REST API
            val jsonPayload = JSONObject().apply {
                put("input", JSONObject().apply {
                    put("ssml", ssmlScript)
                })
                put("voice", JSONObject().apply {
                    put("languageCode", "hi-IN")
                    put("name", googleVoiceName)
                    put("ssmlGender", genderStr)
                })
                put("audioConfig", JSONObject().apply {
                    put("audioEncoding", "MP3")
                    put("speakingRate", (emotion.baseSpeedMod * speedOffset).toDouble().coerceIn(0.6, 1.4))
                    put("pitch", ((emotion.basePitchMod * pitchOffset - 1.0f) * 12.0).toDouble().coerceIn(-10.0, 10.0))
                    put("effectsProfileId", org.json.JSONArray().apply {
                        put("headphone-class-device")
                        put("studio-subwoofer")
                    })
                })
            }

            _genState.value = AudioGenState.Generating("Synthesizing expressive Hindi MP3 stream...")

            val requestBody = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "https://texttospeech.googleapis.com/v1/text:synthesize?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    Log.e("AudioGenService", "TTS API error: ${response.code} $errorBody")
                    val message = if (response.code == 403 || response.code == 400) {
                        "Cloud TTS API authorization failed (${response.code}). Check that Text-to-Speech API is enabled on your Cloud Project in AI Studio Secrets."
                    } else {
                        "Audio synthesis failed (HTTP ${response.code}). Please try again."
                    }
                    _genState.value = AudioGenState.Error(message, isApiKeyMissing = false)
                    return@withContext null
                }

                val responseStr = response.body?.string() ?: throw IllegalStateException("Empty audio response")
                val respJson = JSONObject(responseStr)
                val audioContentBase64 = respJson.optString("audioContent")

                if (audioContentBase64.isNullOrBlank()) {
                    _genState.value = AudioGenState.Error("Server returned empty audio stream.")
                    return@withContext null
                }

                _genState.value = AudioGenState.Generating("Saving high-fidelity MP3 to studio library...")

                val audioBytes = Base64.decode(audioContentBase64, Base64.DEFAULT)
                val outputDir = File(context.filesDir, "mohabbat_audio").apply { mkdirs() }
                val outputFile = File(outputDir, "mohabbat_narrator_${System.currentTimeMillis()}.mp3")

                FileOutputStream(outputFile).use { fos ->
                    fos.write(audioBytes)
                    fos.flush()
                }

                _lastGeneratedFile.value = outputFile
                val wordCount = plainText.split("\\s+".toRegex()).size
                val estSeconds = (wordCount * 0.6f + 3f).toInt()

                _genState.value = AudioGenState.Success(outputFile, estSeconds)
                return@withContext outputFile
            }
        } catch (e: Exception) {
            Log.e("AudioGenService", "Generation failed", e)
            _genState.value = AudioGenState.Error(e.localizedMessage ?: "Audio generation failed.")
            return@withContext null
        }
    }

    fun resetState() {
        _genState.value = AudioGenState.Idle
    }
}
