package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.MusicTrack
import com.example.ui.MohabbatViewModel
import com.example.ui.Screen
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
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

@Composable
fun MusicStudioScreen(
    viewModel: MohabbatViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTrack by viewModel.musicEngine.currentTrack.collectAsState()
    val isPlaying by viewModel.musicEngine.isPlaying.collectAsState()
    val volume by viewModel.musicEngine.volume.collectAsState()
    val userCustomAudio by viewModel.musicEngine.userCustomAudioUri.collectAsState()
    val isMasterActive by viewModel.musicEngine.isMasterReferenceActive.collectAsState()
    val masterTitle by viewModel.musicEngine.referenceMediaTitle.collectAsState()
    val requiresInstrumentalNotice by viewModel.musicEngine.requiresOriginalInstrumentalPrompt.collectAsState()

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setMasterReferenceVideo(uri, "Master Reference Video (Extracted Audio)")
        }
    }

    val instrumentalPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setMasterInstrumentalTrack(uri, "Original Instrumental Track")
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.musicEngine.setUserAudio(uri)
            Toast.makeText(context, "Custom audio loaded & playing 🎵", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
    ) {
        // Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = RoseRomantic,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Music Studio",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "Sad instrumental melodies, emotional piano chords, and custom audio upload",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }

        // Active Player Card
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
                                text = if (userCustomAudio != null) "User Uploaded Track" else currentTrack.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = if (userCustomAudio != null) "Custom Audio File" else currentTrack.genre,
                                style = MaterialTheme.typography.labelSmall.copy(color = RoseSoft)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.musicEngine.togglePlayPause() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AudioWaveformVisualizer(
                        isAnimating = isPlaying,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Volume slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = PurpleVibrant,
                            modifier = Modifier.size(18.dp)
                        )
                        Slider(
                            value = volume,
                            onValueChange = { viewModel.musicEngine.setVolume(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = RoseRomantic,
                                activeTrackColor = PurplePrimary,
                                inactiveTrackColor = Color(0x33A855F7)
                            ),
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )
                        Text(
                            text = "${(volume * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Master Reference Audio & Style Reference
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VideoFile, contentDescription = null, tint = RoseRomantic, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Master Audio & Reference Track",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        if (isMasterActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x3310B981))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Master Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Current: ${masterTitle ?: "Sad Instrumental"}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    )

                    Text(
                        text = "Atmosphere & original tone preserved • Subtle mixing: ${(volume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )

                    if (requiresInstrumentalNotice) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x336366F1))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Separation Guide: If your reference video has overlapping narration, attach the original clean instrumental below to avoid speech bleed.",
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

                    Spacer(modifier = Modifier.height(8.dp))

                    SecondaryGlassButton(
                        text = "Upload Custom Audio (MP3 / WAV)",
                        icon = Icons.Default.FileUpload,
                        onClick = { audioPickerLauncher.launch("audio/*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Curated Sad & Romantic Melodies List
        item {
            Text(
                text = "Emotional Instrumental Library",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(viewModel.musicEngine.tracks) { track ->
            val isSelected = currentTrack.id == track.id && userCustomAudio == null

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.musicEngine.selectTrack(track) },
                color = if (isSelected) Color(0x33A855F7) else DarkSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) RoseRomantic else Color(0x33A855F7)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PurplePrimary else Color(0x33100522)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else PurpleVibrant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            )
                            Text(
                                text = track.emotionDescription,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                                maxLines = 1
                            )
                        }
                    }

                    if (isSelected && isPlaying) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoseRomantic)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PLAYING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Bridge to Reel Studio
        item {
            Spacer(modifier = Modifier.height(20.dp))
            GradientButton(
                text = "Use in 9:16 Reel Studio 🎬",
                icon = Icons.Default.MovieCreation,
                onClick = { viewModel.navigateTo(Screen.VideoStudio) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
