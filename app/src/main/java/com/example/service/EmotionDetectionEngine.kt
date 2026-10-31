package com.example.service

data class LineEmotionAnalysis(
    val lineNumber: Int,
    val lineText: String,
    val dominantEmotion: PoetryEmotion,
    val emotionalKeywords: List<String>,
    val pitchMultiplier: Float,
    val speedMultiplier: Float,
    val pauseAfterMs: Long,
    val deliveryStyle: String
)

enum class PoetryEmotion(
    val id: String,
    val title: String,
    val hindiTitle: String,
    val description: String,
    val basePitchMod: Float,
    val baseSpeedMod: Float,
    val defaultPauseMs: Long,
    val colorHex: Long
) {
    DEEP_HEARTBREAK(
        id = "deep_heartbreak",
        title = "Deep Heartbreak",
        hindiTitle = "गहरा दर्द-ए-दिल",
        description = "Slow, heavy and emotionally affected. Long poignant pauses at concluding verses.",
        basePitchMod = 0.74f,
        baseSpeedMod = 0.74f,
        defaultPauseMs = 1100L,
        colorHex = 0xFFE11D48
    ),
    UNREQUITED_LOVE(
        id = "unrequited_love",
        title = "Unrequited Love",
        hindiTitle = "एकतरफा मोहब्बत",
        description = "Helpless yearning, gentle ache of silent devotion and unseen tears.",
        basePitchMod = 0.78f,
        baseSpeedMod = 0.76f,
        defaultPauseMs = 950L,
        colorHex = 0xFFC084FC
    ),
    BETRAYAL(
        id = "betrayal",
        title = "Betrayal",
        hindiTitle = "बेवफाई और शिकवा",
        description = "Serious, controlled and painful. Heavy deliberate words with restrained sorrow.",
        basePitchMod = 0.72f,
        baseSpeedMod = 0.72f,
        defaultPauseMs = 1200L,
        colorHex = 0xFF991B1B
    ),
    LONELINESS(
        id = "loneliness",
        title = "Loneliness",
        hindiTitle = "खामोश तन्हाई",
        description = "Quiet, soft and deeply reflective. Whispered stillness in nocturnal solitude.",
        basePitchMod = 0.76f,
        baseSpeedMod = 0.72f,
        defaultPauseMs = 1300L,
        colorHex = 0xFF6366F1
    ),
    SEPARATION(
        id = "separation",
        title = "Separation",
        hindiTitle = "हिज्र और जुदाई",
        description = "Grief of distance, counting fading memories with trembling voice inflections.",
        basePitchMod = 0.75f,
        baseSpeedMod = 0.75f,
        defaultPauseMs = 1000L,
        colorHex = 0xFFD946EF
    ),
    ROMANTIC_LOVE(
        id = "romantic_love",
        title = "Romantic Love",
        hindiTitle = "नरम इश्क",
        description = "Gentle, warm, and affectionate. Intimate closeness and tender reverence.",
        basePitchMod = 0.82f,
        baseSpeedMod = 0.80f,
        defaultPauseMs = 700L,
        colorHex = 0xFFEC4899
    ),
    MEMORIES(
        id = "memories",
        title = "Memories",
        hindiTitle = "बीती यादें",
        description = "Nostalgic and slightly melancholic. Reflecting on past warmth and lost promises.",
        basePitchMod = 0.79f,
        baseSpeedMod = 0.77f,
        defaultPauseMs = 900L,
        colorHex = 0xFFF59E0B
    ),
    PAINFUL_LOVE(
        id = "painful_love",
        title = "Painful Love",
        hindiTitle = "दर्द-ए-इश्क",
        description = "The bittersweet blend of loving someone so intensely that it hurts.",
        basePitchMod = 0.76f,
        baseSpeedMod = 0.75f,
        defaultPauseMs = 1050L,
        colorHex = 0xFFBE185D
    )
}

data class QualityControlReport(
    val isValid: Boolean,
    val lineCount: Int,
    val lines: List<String>,
    val hasRepetition: Boolean,
    val feedbackMessage: String
)

data class EmotionAnalysisResult(
    val dominantEmotion: PoetryEmotion,
    val emotionalIntensity: Float, // 0.0 to 1.0
    val summaryInsight: String,
    val lineBreakdown: List<LineEmotionAnalysis>,
    val ssmlScript: String,
    val qualityReport: QualityControlReport
)

class EmotionDetectionEngine {

    private val lexicon = mapOf(
        PoetryEmotion.BETRAYAL to listOf("बेवफा", "bewafa", "धोखा", "dhokha", "गैर", "gair", "झूठ", "jhooth", "रुसवा", "ruswa", "छोड़", "chhod", "मतलब", "matlabi", "वादा", "vaada"),
        PoetryEmotion.DEEP_HEARTBREAK to listOf("टूट", "toot", "चूर", "choor", "ज़ख्म", "zakhm", "बिखर", "bikhar", "आंसू", "aansu", "रोए", "roye", "मातम", "matam", "कफन", "kafan", "तबाह", "tabah", "बरबाद", "barbaad"),
        PoetryEmotion.LONELINESS to listOf("तन्हा", "tanha", "तन्हाई", "tanhai", "अकेले", "akele", "सन्नाटा", "sannata", "खामोश", "khamosh", "रात", "raat", "अंधेरा", "andhera", "सूना", "soona"),
        PoetryEmotion.SEPARATION to listOf("जुदाई", "judai", "हिज्र", "hijr", "दूर", "door", "फासले", "faasle", "अलविदा", "alvida", "बिछड़", "bichhad", "रुखसत", "rukhsat"),
        PoetryEmotion.UNREQUITED_LOVE to listOf("एकतरफा", "ektarfa", "चाहत", "chahat", "इबादत", "ibadat", "इंतज़ार", "intezaar", "नसीब", "naseeb", "किस्मत", "qismat", "ख्वाब", "khwaab"),
        PoetryEmotion.ROMANTIC_LOVE to listOf("मोहब्बत", "mohabbat", "इश्क", "ishq", "प्यार", "pyaar", "सनम", "sanam", "दीवाना", "deewana", "मुस्कान", "muskaan", "रूह", "rooh", "चांद", "chaand", "धड़कन", "dhadkan"),
        PoetryEmotion.MEMORIES to listOf("याद", "yaad", "यादें", "yaadein", "बीते", "beete", "पल", "pal", "तस्वीर", "tasveer", "गुजरे", "gujre", "जमाना", "zamana"),
        PoetryEmotion.PAINFUL_LOVE to listOf("दर्द", "dard", "तड़प", "tadap", "अश्क", "ashq", "बेबसी", "bebasi", "मुकम्मल", "mukammal", "सजा", "saza", "जहर", "zehar")
    )

    /**
     * Quality Control Validator:
     * - Exactly two lines.
     * - Both lines emotionally deep.
     * - Never repeat the first line or any sentence.
     * - No unnecessary introduction or conclusion.
     */
    fun verifyQuality(rawText: String): QualityControlReport {
        val rawLines = rawText.lines()
            .map { it.trim().trim('"', '\'', '`', '—', '-') }
            .filter { line ->
                val cleaned = line.trim()
                cleaned.isNotBlank() &&
                !cleaned.startsWith("//") &&
                !cleaned.startsWith("#") &&
                !cleaned.startsWith("Title:", ignoreCase = true) &&
                !cleaned.startsWith("Shayari:", ignoreCase = true) &&
                !cleaned.startsWith("Here is", ignoreCase = true)
            }

        // Distinct lines check to avoid repeated lines
        val distinctLines = rawLines.distinct()

        if (distinctLines.isEmpty()) {
            return QualityControlReport(
                isValid = false,
                lineCount = 0,
                lines = emptyList(),
                hasRepetition = false,
                feedbackMessage = "शायरी दर्ज करें (कृपया 2 पंक्तियां लिखें)"
            )
        }

        // Strict 2-line selection
        val lines = if (distinctLines.size >= 2) {
            listOf(distinctLines[0], distinctLines[1])
        } else {
            distinctLines
        }

        val hasRepetition = lines.size >= 2 && (
            lines[0].equals(lines[1], ignoreCase = true) ||
            lines[0].contains(lines[1], ignoreCase = true) ||
            lines[1].contains(lines[0], ignoreCase = true)
        )

        val isValid = lines.size == 2 && !hasRepetition

        val feedback = when {
            lines.size < 2 -> "गुणवत्ता जांच: ठीक 2 पंक्तियां (शेर) आवश्यक हैं (वर्तमान: ${lines.size})"
            hasRepetition -> "गुणवत्ता जांच अस्वीकृत: पहली और दूसरी पंक्ति में दोहराव नहीं होना चाहिए"
            else -> "गुणवत्ता जांच उत्तीर्ण: विशुद्ध 2-लाइन शेर (बिना दोहराव, प्राकृतिक ठहराव)"
        }

        return QualityControlReport(
            isValid = isValid,
            lineCount = lines.size,
            lines = lines,
            hasRepetition = hasRepetition,
            feedbackMessage = feedback
        )
    }

    fun analyzeShayari(rawText: String, forcedEmotion: PoetryEmotion? = null): EmotionAnalysisResult {
        val quality = verifyQuality(rawText)
        val lines = if (quality.lines.isNotEmpty()) quality.lines else listOf("कांच के टुकड़े भी चमकते हैं रोशनी पाकर,", "हम तो आईना थे फिर भी तेरे शहर में बिखर गए।")

        val lowerOverall = lines.joinToString(" ").lowercase()

        // Score all emotions
        val scores = mutableMapOf<PoetryEmotion, Int>()
        for ((emotion, words) in lexicon) {
            val count = words.count { lowerOverall.contains(it) }
            scores[emotion] = count
        }

        val detectedDominant = forcedEmotion ?: scores.maxByOrNull { it.value }?.key ?: PoetryEmotion.DEEP_HEARTBREAK
        val totalHits = scores.values.sum()
        val intensity = ((totalHits.toFloat() * 0.25f) + 0.6f).coerceIn(0.6f, 1.0f)

        // Process line by line for exactly 2 lines
        val lineBreakdowns = mutableListOf<LineEmotionAnalysis>()
        val ssmlBuilder = StringBuilder("<speak>")

        lines.forEachIndexed { index, line ->
            val lowerLine = line.lowercase()
            val matchedKeywords = mutableListOf<String>()

            // Find matching emotional keywords in line
            lexicon.values.flatten().forEach { kw ->
                if (lowerLine.contains(kw) && !matchedKeywords.contains(kw)) {
                    matchedKeywords.add(kw)
                }
            }

            // Dynamics for 2-line Sher:
            // Line 1: Setup - warm, introspective, medium-slow
            // Line 2: Climax Punchline - deep resonance, sorrowful ache, spoken once with deliberate emotion
            val isFinalPunchline = (index == lines.lastIndex && lines.size > 1)
            val lineEmotion = if (isFinalPunchline) detectedDominant else PoetryEmotion.MEMORIES

            val pitchMod = if (isFinalPunchline) {
                (lineEmotion.basePitchMod * 0.96f).coerceIn(0.68f, 1.1f)
            } else {
                (lineEmotion.basePitchMod * 1.02f).coerceIn(0.70f, 1.15f)
            }

            // Medium-slow speaking speed without dragging
            val speedMod = if (isFinalPunchline) {
                (lineEmotion.baseSpeedMod * 0.94f).coerceIn(0.74f, 1.05f)
            } else {
                (lineEmotion.baseSpeedMod * 0.98f).coerceIn(0.76f, 1.08f)
            }

            // Natural breathing pause between line 1 and line 2 (450ms - 500ms, strictly avoiding exaggerated silence)
            val pauseAfter = if (isFinalPunchline) 0L else 480L

            val deliveryStyle = if (isFinalPunchline) {
                "गहरा दर्द और अंतिम प्रभाव (Deep Climax Resonance)"
            } else {
                "आत्मीय शुरुआत (Intimate Setup)"
            }

            lineBreakdowns.add(
                LineEmotionAnalysis(
                    lineNumber = index + 1,
                    lineText = line,
                    dominantEmotion = lineEmotion,
                    emotionalKeywords = matchedKeywords,
                    pitchMultiplier = pitchMod,
                    speedMultiplier = speedMod,
                    pauseAfterMs = pauseAfter,
                    deliveryStyle = deliveryStyle
                )
            )

            // Construct SSML with subtle 200ms comma breaks (natural breathing, no robotic silence)
            val humanizedLine = line.replace(",", " <break time=\"200ms\"/> ")
            ssmlBuilder.append("<prosody pitch=\"${(pitchMod * 100).toInt()}%\" rate=\"${(speedMod * 100).toInt()}%\">")
            ssmlBuilder.append(humanizedLine)
            ssmlBuilder.append("</prosody>")
            if (index < lines.lastIndex && pauseAfter > 0) {
                ssmlBuilder.append("<break time=\"${pauseAfter}ms\"/>")
            }
        }

        ssmlBuilder.append("</speak>")

        val insight = when (detectedDominant) {
            PoetryEmotion.DEEP_HEARTBREAK -> "शायरी में टूटे दिल की गहरी उदासी और सब कुछ खो देने की बेबसी है।"
            PoetryEmotion.UNREQUITED_LOVE -> "एकतरफा चाहत, जहां जज्बात अनकहे और आंसू छुपाए गए हैं।"
            PoetryEmotion.BETRAYAL -> "विश्वास टूटने की गंभीर टीस और आत्मीय दर्द का असर है।"
            PoetryEmotion.LONELINESS -> "एकांत की गहरी खामोशी और रात के सन्नाटे का भाव।"
            PoetryEmotion.SEPARATION -> "अपनों से बिछड़ने का दर्द और दूरियों की बेबसी।"
            PoetryEmotion.ROMANTIC_LOVE -> "रूहानी ताल्लुक और बेपनाह मोहब्बत की मिठास।"
            PoetryEmotion.MEMORIES -> "पुरानी यादों की महक और गुजरे पलों का अहसास।"
            PoetryEmotion.PAINFUL_LOVE -> "दर्द और मोहब्बत की कशमकश, जहां जुदाई भी इबादत है।"
        }

        return EmotionAnalysisResult(
            dominantEmotion = detectedDominant,
            emotionalIntensity = intensity,
            summaryInsight = insight,
            lineBreakdown = lineBreakdowns,
            ssmlScript = ssmlBuilder.toString(),
            qualityReport = quality
        )
    }
}
