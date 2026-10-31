package com.example

import com.example.data.model.ProjectEntity
import com.example.service.AiPoetryService
import com.example.service.EmotionDetectionEngine
import com.example.service.PoetryEmotion
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MohabbatAppTest {
    @Test
    fun testShayariCuratedFallbackGeneration() = runBlocking {
        val service = AiPoetryService()
        val result = service.generateShayari(
            category = "Heartbreak",
            language = "Hindi",
            tone = "Dard-e-Dil",
            userTopic = null
        )

        assertNotNull(result)
        assertTrue(result.lines.isNotEmpty())
        assertTrue(result.fullText.isNotBlank())
        assertEquals("Heartbreak", result.category)
        assertEquals("Hindi", result.language)
    }

    @Test
    fun testProjectEntityCreation() {
        val project = ProjectEntity(
            title = "Dard-e-Mohabbat Reel",
            type = "REEL",
            content = "Lines of poetry",
            language = "Hindi",
            category = "Heartbreak",
            tone = "Dard-e-Dil",
            musicTrack = "Aansoon (Sad Piano & Rain)",
            backgroundId = "rain"
        )

        assertEquals("Dard-e-Mohabbat Reel", project.title)
        assertEquals("REEL", project.type)
        assertEquals("rain", project.backgroundId)
        assertTrue(project.createdAt > 0)
    }

    @Test
    fun testAllEightPoetryEmotions() {
        val emotions = PoetryEmotion.entries
        assertEquals(8, emotions.size)
        assertTrue(emotions.contains(PoetryEmotion.DEEP_HEARTBREAK))
        assertTrue(emotions.contains(PoetryEmotion.UNREQUITED_LOVE))
        assertTrue(emotions.contains(PoetryEmotion.BETRAYAL))
        assertTrue(emotions.contains(PoetryEmotion.LONELINESS))
        assertTrue(emotions.contains(PoetryEmotion.SEPARATION))
        assertTrue(emotions.contains(PoetryEmotion.ROMANTIC_LOVE))
        assertTrue(emotions.contains(PoetryEmotion.MEMORIES))
        assertTrue(emotions.contains(PoetryEmotion.PAINFUL_LOVE))
    }

    @Test
    fun testEmotionDetectionEngineAnalysis() {
        val engine = EmotionDetectionEngine()
        val heartbreakText = "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,\nहम तो आईना थे फिर भी तेरे शहर में बिखर गए।"
        val analysis = engine.analyzeShayari(heartbreakText)

        assertNotNull(analysis)
        assertEquals(PoetryEmotion.DEEP_HEARTBREAK, analysis.dominantEmotion)
        assertEquals(2, analysis.lineBreakdown.size)
        assertTrue(analysis.qualityReport.isValid)
        assertEquals(2, analysis.qualityReport.lineCount)
        assertTrue(analysis.ssmlScript.contains("<speak>"))
        assertTrue(analysis.ssmlScript.contains("</speak>"))
        assertTrue(analysis.ssmlScript.contains("<break time="))
    }

    @Test
    fun testStrictTwoLineSherQualityControl() {
        val engine = EmotionDetectionEngine()

        // Valid 2 distinct lines
        val validSher = "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,\nहम तो आईना थे फिर भी तेरे शहर में बिखर गए।"
        val report = engine.verifyQuality(validSher)
        assertTrue(report.isValid)
        assertEquals(2, report.lineCount)
        assertEquals(false, report.hasRepetition)

        // Invalid: Repeated line
        val repeatedSher = "कांच के टुकड़े भी चमकते हैं,\nकांच के टुकड़े भी चमकते हैं"
        val reportRepeated = engine.verifyQuality(repeatedSher)
        assertTrue(!reportRepeated.isValid)

        // Invalid: Single line
        val singleLine = "केवल एक पंक्ति"
        val reportSingle = engine.verifyQuality(singleLine)
        assertTrue(!reportSingle.isValid)
        assertEquals(1, reportSingle.lineCount)
    }

    @Test
    fun testAllCuratedShayarisAreStrictlyTwoLines() = runBlocking {
        val service = AiPoetryService()
        val categories = listOf("Deep Romantic", "Heartbreak", "One-sided Love", "4-Line")
        val languages = listOf("Hindi", "Hinglish")

        for (cat in categories) {
            for (lang in languages) {
                val res = service.generateShayari(cat, lang, "Dard-e-Dil", null)
                assertEquals("Category: $cat Lang: $lang must be 2 lines", 2, res.lines.size)
                assertTrue("Line 1 and Line 2 must not be equal", res.lines[0] != res.lines[1])
            }
        }
    }
}
