package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AudioGenState
import com.example.service.PoetryEmotion
import com.example.service.VoiceCharacter
import com.example.ui.MohabbatViewModel
import com.example.ui.Screen
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RoseRomantic
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceStudioScreen(
    viewModel: MohabbatViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val text by viewModel.voiceStudioText.collectAsState()
    val currentVoice by viewModel.voiceEngine.currentVoice.collectAsState()
    val selectedEmotion by viewModel.voiceEngine.selectedEmotion.collectAsState()
    val analysis by viewModel.voiceEngine.currentAnalysis.collectAsState()
    val pitch by viewModel.voiceEngine.pitch.collectAsState()
    val speed by viewModel.voiceEngine.speed.collectAsState()
    val intensity by viewModel.voiceEngine.emotionalIntensity.collectAsState()
    val pauseDurationMs by viewModel.voiceEngine.pauseDurationMs.collectAsState()

    val isPreviewSpeaking by viewModel.voiceEngine.isPreviewSpeaking.collectAsState()
    val currentSpokenLine by viewModel.voiceEngine.currentSpokenLine.collectAsState()
    val narrationStatus by viewModel.voiceEngine.narrationStatus.collectAsState()

    val genState by viewModel.voiceEngine.audioGenService.genState.collectAsState()
    val isPlayerPlaying by viewModel.voiceEngine.audioPlayer.isPlaying.collectAsState()
    val playerProgress by viewModel.voiceEngine.audioPlayer.progressFraction.collectAsState()
    val currentPosMs by viewModel.voiceEngine.audioPlayer.currentPositionMs.collectAsState()
    val durationMs by viewModel.voiceEngine.audioPlayer.durationMs.collectAsState()
    val currentAudioFile by viewModel.voiceEngine.audioPlayer.currentAudioFile.collectAsState()

    var showControlsDetail by remember { mutableStateOf(false) }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setMasterReferenceVideo(uri, "Master Reference Video (Extracted Audio)")
        }
    }

    val instrumentalPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setMasterInstrumentalTrack(uri, "Original Instrumental Track")
        }
    }

    // Run semantic emotion analysis whenever text or forced emotion changes
    LaunchedEffect(text, selectedEmotion) {
        viewModel.voiceEngine.analyzeText(text)
    }

    val sampleShayaris = listOf(
        "Heartbreak" to "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,\nहम तो आईना थे फिर भी तेरे शहर में बिखर गए।",
        "Betrayal" to "तेरी बेवफाई का गिला हम किससे करें,\nतूने तो गैरों की खातिर हमें छोड़ दिया।",
        "Loneliness" to "तुझे खबर भी नहीं इस दर्द-ए-इश्क की शायद,\nऔर हम तन्हाई की चादर में छुपकर रोते हैं।",
        "Romantic" to "तेरी खामोशियों में भी एक गहरा असर है,\nमेरी हर एक दुआ को बस तेरी ही फिक्र है।"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
    ) {
        // Screen Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = RoseRomantic,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "AI Emotional Voice Studio",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "Professional Hindi poetry narration expressing human emotion, authentic pacing & natural pauses",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }

        // 1. Voice Personality Showcase Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF2E0854),
                                Color(0xFF1D0636),
                                Color(0xFF0F031E)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(RoseRomantic, PurpleVibrant, Color.Transparent)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentVoice.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = PlayfairDisplayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                )
                                Text(
                                    text = currentVoice.hindiTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = RoseSoft,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x44EC4899))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DEEP INDIAN MALE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentVoice.styleDescription,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Voice Character Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.voiceEngine.characterVoices.forEach { voice ->
                            val isSelected = voice.id == currentVoice.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) PurplePrimary else Color(0x33000000))
                                    .border(
                                        1.dp,
                                        if (isSelected) RoseSoft else Color(0x22FFFFFF),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.voiceEngine.selectVoice(voice) }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = voice.name.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Emotion Detection Engine Card
        item {
            val dominant = analysis?.dominantEmotion ?: PoetryEmotion.DEEP_HEARTBREAK
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = PurpleVibrant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emotion Detection Engine",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(dominant.colorHex).copy(alpha = 0.35f))
                                .border(1.dp, Color(dominant.colorHex), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = dominant.hindiTitle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = analysis?.summaryInsight ?: dominant.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 8 Emotion Selector Pills
                    Text(
                        text = "Override Emotion or Auto-Detect:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Auto detect pill
                        val isAuto = (selectedEmotion == null)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAuto) PurplePrimary else DarkSurfaceCard)
                                .border(1.dp, if (isAuto) RoseRomantic else Color(0x33A855F7), RoundedCornerShape(8.dp))
                                .clickable { viewModel.voiceEngine.setEmotion(null) }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Auto-Detect ✨",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isAuto) Color.White else TextSecondary,
                                    fontWeight = if (isAuto) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        PoetryEmotion.entries.forEach { emo ->
                            val isSelected = (selectedEmotion == emo)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(emo.colorHex) else DarkSurfaceCard)
                                    .border(1.dp, if (isSelected) Color.White else Color(0x33A855F7), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.voiceEngine.setEmotion(emo) }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = emo.hindiTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Script Input with Quick Emotion Presets
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shayari Script (Hindi)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )

                // Quick presets
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    sampleShayaris.forEach { (label, sampleText) ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = RoseSoft,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x22EC4899))
                                .clickable { viewModel.updateVoiceText(sampleText) }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { viewModel.updateVoiceText(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .testTag("voice_script_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = Color(0x40A855F7),
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quality Control Validation Banner
            val qualityReport = analysis?.qualityReport
            val isQualityValid = qualityReport?.isValid == true

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isQualityValid) Color(0x2210B981) else Color(0x22F59E0B))
                    .border(
                        1.dp,
                        if (isQualityValid) Color(0xFF10B981) else Color(0xFFF59E0B),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isQualityValid) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isQualityValid) Color(0xFF10B981) else Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isQualityValid) "गुणवत्ता जांच उत्तीर्ण: 2-लाइन शेर (Sher Invariant Verified)" else "गुणवत्ता जांच: ठीक 2 भावनात्मक पंक्तियां आवश्यक हैं",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = qualityReport?.feedbackMessage ?: "2 पंक्तियां • बिना दोहराव • आत्मीय और गहरा ठहराव",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isQualityValid) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Master Audio & Style Reference Section
            val refMediaTitle by viewModel.musicEngine.referenceMediaTitle.collectAsState()
            val isMasterActive by viewModel.musicEngine.isMasterReferenceActive.collectAsState()
            val requiresInstrumentalPrompt by viewModel.musicEngine.requiresOriginalInstrumentalPrompt.collectAsState()
            val bgmVolume by viewModel.musicEngine.volume.collectAsState()

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VideoFile,
                                contentDescription = null,
                                tint = RoseRomantic,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Master Audio & Reference Style",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isMasterActive) Color(0x3310B981) else Color(0x33A855F7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isMasterActive) "Master Source Active" else "Default Subtle BGM",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isMasterActive) Color(0xFF10B981) else RoseSoft,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current BGM: ${refMediaTitle ?: "Sad Instrumental"}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = "Mixing Invariant: Voice 100% Prominent • Background Music Subtle (${(bgmVolume * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )

                    if (requiresInstrumentalPrompt) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x336366F1))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Notice: Separation from video narration is unavailable on device. Attach the original instrumental track below for pure background score.",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC7D2FE), fontSize = 10.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecondaryGlassButton(
                            text = "Reference Video",
                            icon = Icons.Default.VideoFile,
                            onClick = { videoPickerLauncher.launch("video/*") },
                            modifier = Modifier.weight(1f)
                        )

                        SecondaryGlassButton(
                            text = "Clean Instrumental",
                            icon = Icons.Default.Audiotrack,
                            onClick = { instrumentalPickerLauncher.launch("audio/*") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. Smart Voice Controls Accordion
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showControlsDetail = !showControlsDetail },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = RoseSoft, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Smart Voice & Prosody Controls",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Text(
                            text = if (showControlsDetail) "Hide ▲" else "Adjust ▼",
                            style = MaterialTheme.typography.labelSmall.copy(color = PurpleVibrant)
                        )
                    }

                    if (showControlsDetail) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Pitch Slider
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Voice Pitch (गंभीरता)", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text("${String.format("%.2f", pitch)}x", style = MaterialTheme.typography.labelSmall.copy(color = PurpleVibrant))
                        }
                        Slider(
                            value = pitch,
                            onValueChange = { viewModel.voiceEngine.setPitch(it) },
                            valueRange = 0.6f..1.2f,
                            colors = SliderDefaults.colors(thumbColor = RoseRomantic, activeTrackColor = PurplePrimary)
                        )

                        // Speed Slider
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Speaking Pace (धीमी और नियंत्रित)", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text("${String.format("%.2f", speed)}x", style = MaterialTheme.typography.labelSmall.copy(color = PurpleVibrant))
                        }
                        Slider(
                            value = speed,
                            onValueChange = { viewModel.voiceEngine.setSpeed(it) },
                            valueRange = 0.6f..1.2f,
                            colors = SliderDefaults.colors(thumbColor = RoseRomantic, activeTrackColor = PurplePrimary)
                        )

                        // Emotional Intensity Slider
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Emotional Intensity (गहराई)", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text("${(intensity * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(color = RoseSoft))
                        }
                        Slider(
                            value = intensity,
                            onValueChange = { viewModel.voiceEngine.setEmotionalIntensity(it) },
                            valueRange = 0.5f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = RoseRomantic, activeTrackColor = PurplePrimary)
                        )

                        // Pause Duration Slider
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pause Duration (मिसरों के बीच ठहराव)", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                            Text("${pauseDurationMs}ms", style = MaterialTheme.typography.labelSmall.copy(color = PurpleVibrant))
                        }
                        Slider(
                            value = pauseDurationMs.toFloat(),
                            onValueChange = { viewModel.voiceEngine.setPauseDuration(it.toLong()) },
                            valueRange = 400f..1800f,
                            colors = SliderDefaults.colors(thumbColor = RoseRomantic, activeTrackColor = PurplePrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Line-by-Line Meaning & Emotion Breakdown
        if (!analysis?.lineBreakdown.isNullOrEmpty()) {
            item {
                Text(
                    text = "Line-by-Line Emotion Trajectory:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    analysis!!.lineBreakdown.forEach { lineAnalysis ->
                        val isCurrentlySpoken = isPreviewSpeaking && currentSpokenLine.contains(lineAnalysis.lineText.take(10))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrentlySpoken) Color(0x44EC4899) else DarkSurfaceCard)
                                .border(
                                    1.dp,
                                    if (isCurrentlySpoken) RoseRomantic else Color(0x22FFFFFF),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lineAnalysis.lineText,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = PlayfairDisplayFontFamily,
                                            fontWeight = if (isCurrentlySpoken) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrentlySpoken) Color.White else TextSecondary
                                        )
                                    )
                                    Text(
                                        text = lineAnalysis.deliveryStyle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = RoseSoft,
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(lineAnalysis.dominantEmotion.colorHex).copy(alpha = 0.3f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = lineAnalysis.dominantEmotion.hindiTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 6. Audio Generation Actions & Status
        item {
            // Error Card (if API key missing or network failure)
            if (genState is AudioGenState.Error) {
                val err = genState as AudioGenState.Error
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x33EF4444))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (err.isApiKeyMissing) "API Key Notice" else "Synthesis Status",
                                style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = err.errorMessage,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFCA5A5), fontSize = 11.sp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Generation Progress
            if (genState is AudioGenState.Generating) {
                val gen = genState as AudioGenState.Generating
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceCard)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = RoseRomantic, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = gen.progressText,
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Generation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Voice Preview Button (Instant human preview)
                if (isPreviewSpeaking) {
                    SecondaryGlassButton(
                        text = "Stop Preview",
                        icon = Icons.Default.Stop,
                        onClick = { viewModel.stopVoice() },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    SecondaryGlassButton(
                        text = "Voice Preview 🎙️",
                        icon = Icons.Default.PlayArrow,
                        onClick = { viewModel.previewVoiceNarration() },
                        modifier = Modifier.weight(1f),
                        testTag = "voice_preview_btn"
                    )
                }

                // Studio MP3 Generate Button
                GradientButton(
                    text = "Generate MP3 ✨",
                    icon = Icons.Default.AutoAwesome,
                    onClick = { viewModel.generateStudioNarration() },
                    modifier = Modifier.weight(1f),
                    testTag = "generate_mp3_btn"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // 7. Interactive Audio Player (Timeline, Play/Pause, Replay, Download)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentAudioFile != null) "Studio MP3 Narration Player" else "Emotional Voice Audio Player",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = if (isPlayerPlaying) "Playing: ${currentVoice.name}" else narrationStatus,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isPlayerPlaying) RoseSoft else TextMuted
                                )
                            )
                        }

                        // Replay Button
                        IconButton(
                            onClick = { viewModel.voiceEngine.audioPlayer.replay() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = "Replay", tint = PurpleVibrant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Waveform visualizer
                    AudioWaveformVisualizer(
                        isAnimating = isPlayerPlaying || isPreviewSpeaking,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Audio Timeline Scrubber
                    val elapsedSec = (currentPosMs / 1000)
                    val totalSec = (durationMs / 1000).coerceAtLeast(1)
                    val elapsedStr = String.format("%02d:%02d", elapsedSec / 60, elapsedSec % 60)
                    val totalStr = String.format("%02d:%02d", totalSec / 60, totalSec % 60)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = elapsedStr, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text(text = totalStr, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    }

                    Slider(
                        value = playerProgress,
                        onValueChange = { viewModel.voiceEngine.audioPlayer.seekToFraction(it) },
                        colors = SliderDefaults.colors(
                            thumbColor = RoseRomantic,
                            activeTrackColor = PurplePrimary,
                            inactiveTrackColor = Color(0x33A855F7)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Player Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause Main Button
                        IconButton(
                            onClick = { viewModel.voiceEngine.audioPlayer.togglePlayPause() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary)
                        ) {
                            Icon(
                                imageVector = if (isPlayerPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlayerPlaying) "Pause" else "Play",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Download MP3 button
                            SecondaryGlassButton(
                                text = "Download MP3",
                                icon = Icons.Default.Download,
                                onClick = {
                                    viewModel.voiceEngine.audioPlayer.downloadCurrentMp3("Mohabbat_Narration")
                                }
                            )

                            // Regenerate Voice button
                            IconButton(
                                onClick = { viewModel.generateStudioNarration() },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkSurfaceCard)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = PurpleVibrant)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 8. Video Synchronization & Save CTA
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                GradientButton(
                    text = "Synchronize with 9:16 Reel Studio 🎬",
                    icon = Icons.Default.MovieCreation,
                    onClick = { viewModel.syncNarrationToReel() },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "sync_reel_cta"
                )

                Spacer(modifier = Modifier.height(10.dp))

                SecondaryGlassButton(
                    text = "Save Voice Narration Project",
                    icon = Icons.Default.BookmarkBorder,
                    onClick = { viewModel.saveVoiceProject() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
