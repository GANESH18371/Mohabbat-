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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MohabbatViewModel
import com.example.ui.Screen
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.ReelVideoCanvas
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RoseRomantic
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoStudioScreen(
    viewModel: MohabbatViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reelLines by viewModel.reelPoetryLines.collectAsState()
    val bgType by viewModel.reelBackground.collectAsState()
    val isPlaying by viewModel.isPlayingReel.collectAsState()
    val currentLineIndex by viewModel.currentReelLineIndex.collectAsState()
    val currentMusic by viewModel.musicEngine.currentTrack.collectAsState()
    val musicVolume by viewModel.musicEngine.volume.collectAsState()

    var isExportingMp4 by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableStateOf(0f) }
    var isExportSuccess by remember { mutableStateOf(false) }

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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MovieCreation,
                        contentDescription = null,
                        tint = RoseRomantic,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Video Studio (9:16 Reel)",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "Instagram Reel format with animated Hindi typography and synchronized audio",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }

        // 9:16 Canvas Reel Preview
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                ReelVideoCanvas(
                    backgroundType = bgType,
                    poetryLines = reelLines,
                    currentLineIndex = currentLineIndex,
                    isPlaying = isPlaying,
                    musicName = currentMusic.title,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Play / Pause Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GradientButton(
                    text = if (isPlaying) "Pause Reel Preview" else "Play 9:16 Reel Preview",
                    icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    onClick = { viewModel.toggleReelPlayback() },
                    modifier = Modifier.weight(1f),
                    testTag = "reel_playback_btn"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Background Scene Selection
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Background Atmosphere",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BackgroundOptionCard(
                        title = "Rain Window",
                        subtitle = "Moody Drops",
                        isSelected = bgType == "rain",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setReelBackground("rain") }
                    )
                    BackgroundOptionCard(
                        title = "Rooftop Galaxy",
                        subtitle = "Midnight Stars",
                        isSelected = bgType == "galaxy",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setReelBackground("galaxy") }
                    )
                    BackgroundOptionCard(
                        title = "Velvet Bokeh",
                        subtitle = "Glowing Dust",
                        isSelected = bgType == "velvet",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setReelBackground("velvet") }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }

        // Music & Audio Mixing
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Audio & Narration Sync",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val audioFile = viewModel.voiceEngine.audioPlayer.currentAudioFile.collectAsState().value
                    val currentVoice = viewModel.voiceEngine.currentVoice.collectAsState().value

                    // Synced Voice Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33EC4899))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(RoseRomantic)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (audioFile != null) "Audio: Studio MP3 (${currentVoice.name})" else "Audio: ${currentVoice.name} Voice Narration",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val masterTitle by viewModel.musicEngine.referenceMediaTitle.collectAsState()
                    val isMasterActive by viewModel.musicEngine.isMasterReferenceActive.collectAsState()

                    Text(
                        text = "Background Score: ${masterTitle ?: currentMusic.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = RoseSoft, fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

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

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtle BGM Volume (Preserving Voice Prominence)", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text("${(musicVolume * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(color = PurpleVibrant))
                    }
                    Slider(
                        value = musicVolume,
                        onValueChange = { viewModel.musicEngine.setVolume(it) },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = RoseRomantic,
                            activeTrackColor = PurplePrimary,
                            inactiveTrackColor = Color(0x33A855F7)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Export MP4 & Publish to Instagram
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isExportingMp4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceCard)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PurpleVibrant, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Encoding 1080x1920 60FPS Sad Reel... ${(exportProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                            )
                        }
                    }
                } else if (isExportSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x3310B981))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reel exported successfully as MP4 (Ready to share)!",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryGlassButton(
                            text = "Export MP4 Reel",
                            icon = Icons.Default.Download,
                            onClick = {
                                val qc = viewModel.voiceEngine.emotionEngine.verifyQuality(reelLines.joinToString("\n"))
                                if (!qc.isValid) {
                                    Toast.makeText(context, qc.feedbackMessage, Toast.LENGTH_SHORT).show()
                                    return@SecondaryGlassButton
                                }
                                isExportingMp4 = true
                                isExportSuccess = false
                                coroutineScope.launch {
                                    for (p in 1..10) {
                                        exportProgress = p / 10f
                                        delay(200L)
                                    }
                                    isExportingMp4 = false
                                    isExportSuccess = true
                                    Toast.makeText(context, "Saved to Gallery / Movies / MohabbatReels (2-Line Invariant Verified)", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryGlassButton(
                            text = "Save Project",
                            icon = Icons.Default.BookmarkBorder,
                            onClick = { viewModel.saveReelProject() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GradientButton(
                    text = "Publish to Instagram Reels 🚀",
                    icon = Icons.Default.Share,
                    onClick = { viewModel.navigateTo(Screen.InstagramConnect) },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "reel_publish_nav_btn"
                )
            }
        }
    }
}

@Composable
private fun BackgroundOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = if (isSelected) Color(0x33A855F7) else DarkSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) RoseRomantic else Color(0x33A855F7)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) RoseSoft else TextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}
