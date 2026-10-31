package com.example.service

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ShayariResult(
    val title: String,
    val lines: List<String>,
    val fullText: String,
    val category: String,
    val language: String,
    val tone: String,
    val explanation: String
)

class AiPoetryService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Strictly 2-line classic emotional couplets (Sher) - never repeated
    private val curatedShayaris = mapOf(
        "Heartbreak_Hindi" to listOf(
            ShayariResult(
                title = "टूटा आईना",
                lines = listOf(
                    "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,",
                    "हम तो आईना थे फिर भी तेरे शहर में बिखर गए।"
                ),
                fullText = "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,\nहम तो आईना थे फिर भी तेरे शहर में बिखर गए।",
                category = "Heartbreak",
                language = "Hindi",
                tone = "Dard-e-Dil",
                explanation = "Fragility of true love shattered by distance and silence."
            ),
            ShayariResult(
                title = "सब कुछ लुटा दिया",
                lines = listOf(
                    "हमने तो एक ही शख्स पर सब कुछ लुटा दिया,",
                    "उसने जरा सी बात पे हमको हमेशा के लिए भुला दिया।"
                ),
                fullText = "हमने तो एक ही शख्स पर सब कुछ लुटा दिया,\nउसने जरा सी बात पे हमको हमेशा के लिए भुला दिया।",
                category = "Heartbreak",
                language = "Hindi",
                tone = "Dard-e-Dil",
                explanation = "Complete emotional surrender forgotten effortlessly by the beloved."
            ),
            ShayariResult(
                title = "फासले",
                lines = listOf(
                    "पास रहकर भी जो कभी समझे नहीं हमें,",
                    "आज उनसे हम हजारों मील दूर हो गए।"
                ),
                fullText = "पास रहकर भी जो कभी समझे नहीं हमें,\nआज उनसे हम हजारों मील दूर हो गए।",
                category = "Heartbreak",
                language = "Hindi",
                tone = "Khamoshi",
                explanation = "The quiet pain of emotional distance turning into physical departure."
            )
        ),
        "Heartbreak_Hinglish" to listOf(
            ShayariResult(
                title = "Zakhm-e-Wafa",
                lines = listOf(
                    "Dard itna tha ki aankhon se chhalakne na diya,",
                    "Dil ko zakhmi hi sahi par tere aage jhukne na diya."
                ),
                fullText = "Dard itna tha ki aankhon se chhalakne na diya,\nDil ko zakhmi hi sahi par tere aage jhukne na diya.",
                category = "Heartbreak",
                language = "Hinglish",
                tone = "Dard-e-Dil",
                explanation = "Holding back tears to preserve dignity amidst deep heartbreak."
            ),
            ShayariResult(
                title = "Adhoora Afsana",
                lines = listOf(
                    "Humne to ek hi shakhs par sab kuch luta diya,",
                    "Usne zara si baat pe humko hamesha ke liye bhula diya."
                ),
                fullText = "Humne to ek hi shakhs par sab kuch luta diya,\nUsne zara si baat pe humko hamesha ke liye bhula diya.",
                category = "Heartbreak",
                language = "Hinglish",
                tone = "Dard-e-Dil",
                explanation = "Pain of being cast aside after complete dedication."
            )
        ),
        "Deep Romantic_Hindi" to listOf(
            ShayariResult(
                title = "रूहानी ताल्लुक",
                lines = listOf(
                    "तेरी खामोशियों में भी एक गहरा असर है,",
                    "मेरी हर एक दुआ को बस तेरी ही फिक्र है।"
                ),
                fullText = "तेरी खामोशियों में भी एक गहरा असर है,\nमेरी हर एक दुआ को बस तेरी ही फिक्र है।",
                category = "Deep Romantic",
                language = "Hindi",
                tone = "Bheegi Palkein",
                explanation = "Silent devotion where love connects soul to soul."
            ),
            ShayariResult(
                title = "सिर्फ तेरा जिक्र",
                lines = listOf(
                    "यूं तो महफिल में लोग हजारों मिले हमें,",
                    "मगर इस तन्हा दिल के कोने में सिर्फ तेरा ही जिक्र है।"
                ),
                fullText = "यूं तो महफिल में लोग हजारों मिले हमें,\nमगर इस तन्हा दिल के कोने में सिर्फ तेरा ही जिक्र है।",
                category = "Deep Romantic",
                language = "Hindi",
                tone = "Bheegi Palkein",
                explanation = "A solitary heart remembering only one presence in crowded worlds."
            )
        ),
        "Deep Romantic_Hinglish" to listOf(
            ShayariResult(
                title = "Rooh Ka Rishta",
                lines = listOf(
                    "Teri khamoshiyon mein bhi ek gehra asar hai,",
                    "Meri har ek dua ko bas teri hi fikar hai."
                ),
                fullText = "Teri khamoshiyon mein bhi ek gehra asar hai,\nMeri har ek dua ko bas teri hi fikar hai.",
                category = "Deep Romantic",
                language = "Hinglish",
                tone = "Soulful",
                explanation = "Soul connection that whispers through unspoken feelings."
            )
        ),
        "One-sided Love_Hindi" to listOf(
            ShayariResult(
                title = "एकतरफा इबादत",
                lines = listOf(
                    "तुझे खबर भी नहीं इस दर्द-ए-इश्क की शायद,",
                    "और हम तन्हाई की चादर में छुपकर रोते हैं।"
                ),
                fullText = "तुझे खबर भी नहीं इस दर्द-ए-इश्क की शायद,\nऔर हम तन्हाई की चादर में छुपकर रोते हैं।",
                category = "One-sided Love",
                language = "Hindi",
                tone = "Tanhai",
                explanation = "Unconditional adoration where tears remain secret."
            )
        ),
        "One-sided Love_Hinglish" to listOf(
            ShayariResult(
                title = "Khamosh Mohabbat",
                lines = listOf(
                    "Tujhe khabar bhi nahi is dard-e-ishq ki shayad,",
                    "Aur hum tanhai ki chaadar mein chhupkar rote hain."
                ),
                fullText = "Tujhe khabar bhi nahi is dard-e-ishq ki shayad,\nAur hum tanhai ki chaadar mein chhupkar rote hain.",
                category = "One-sided Love",
                language = "Hinglish",
                tone = "Tanhai",
                explanation = "Silent tears and pure adoration with no expectations."
            )
        ),
        "4-Line_Hindi" to listOf(
            ShayariResult(
                title = "दो कतरे अश्क",
                lines = listOf(
                    "कभी जो याद आओ तुम तो आंखें भीग जाती हैं,",
                    "मोहब्बत मुकम्मल न सही पर इबादत बन गई मेरी।"
                ),
                fullText = "कभी जो याद आओ तुम तो आंखें भीग जाती हैं,\nमोहब्बत मुकम्मल न सही पर इबादत बन गई मेरी।",
                category = "4-Line",
                language = "Hindi",
                tone = "Adhuri Mohabbat",
                explanation = "Poetic couplet of incomplete love transforming into worship."
            )
        ),
        "4-Line_Hinglish" to listOf(
            ShayariResult(
                title = "Do Qatre Ashq",
                lines = listOf(
                    "Kabhi jo yaad aao tum to aankhein bheeg jaati hain,",
                    "Mohabbat mukammal na sahi par ibaadat ban gayi meri."
                ),
                fullText = "Kabhi jo yaad aao tum to aankhein bheeg jaati hain,\nMohabbat mukammal na sahi par ibaadat ban gayi meri.",
                category = "4-Line",
                language = "Hinglish",
                tone = "Adhuri Mohabbat",
                explanation = "Love that remains sacred despite separation."
            )
        )
    )

    suspend fun generateShayari(
        category: String,
        language: String, // "Hindi" or "Hinglish"
        tone: String,
        userTopic: String?
    ): ShayariResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = buildString {
                    append("You are a legendary Urdu & Hindi Shayar. ")
                    append("Compose EXACTLY 2 meaningful, emotionally deep, heart-touching lines (2-Line Sher). ")
                    append("Theme: $category. Tone: $tone. ")
                    if (!userTopic.isNullOrBlank()) {
                        append("Topic/Context: $userTopic. ")
                    }
                    if (language.equals("Hindi", ignoreCase = true)) {
                        append("Language: Pure Hindi (Devanagari script). ")
                    } else {
                        append("Language: Hinglish (Romanized Hindi/Urdu). ")
                    }
                    append("STRICT RULES: ")
                    append("1. Exactly 2 lines. ")
                    append("2. Both lines must be completely different - NEVER repeat the first line or any sentence. ")
                    append("3. No preamble, no introduction, no conversational filler. ")
                    append("Output strictly valid JSON with this format: ")
                    append("""{"title": "Title", "line1": "First deep line", "line2": "Second deep line (different from line 1)", "explanation": "Essence"}""")
                }

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("responseMimeType", "application/json")
                        put("temperature", 0.75)
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonBody.toString().toRequestBody(mediaType)
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyString = response.body?.string()
                        if (!bodyString.isNullOrBlank()) {
                            val respJson = JSONObject(bodyString)
                            val candidates = respJson.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val content = candidates.getJSONObject(0).optJSONObject("content")
                                val parts = content?.optJSONArray("parts")
                                val text = parts?.getJSONObject(0)?.optString("text")
                                if (!text.isNullOrBlank()) {
                                    val cleaned = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                                    val parsed = JSONObject(cleaned)
                                    val title = parsed.optString("title", "दो लाइन शायरी")
                                    val l1 = parsed.optString("line1").trim()
                                    val l2 = parsed.optString("line2").trim()
                                    val exp = parsed.optString("explanation", "गहरी भावनाएं")

                                    // Verify quality control: exactly 2 lines, no repetition
                                    if (l1.isNotBlank() && l2.isNotBlank() && !l1.equals(l2, ignoreCase = true)) {
                                        val lines = listOf(l1, l2)
                                        return@withContext ShayariResult(
                                            title = title,
                                            lines = lines,
                                            fullText = lines.joinToString("\n"),
                                            category = category,
                                            language = language,
                                            tone = tone,
                                            explanation = exp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("AiPoetryService", "API generation error, using pristine 2-line fallback", e)
            }
        }

        // Return curated non-repetitive 2-line couplet
        val key = "${category}_$language"
        val fallbackList = curatedShayaris[key]
            ?: curatedShayaris["Heartbreak_$language"]
            ?: curatedShayaris["Heartbreak_Hindi"]!!
        fallbackList.random()
    }
}
