package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ProjectEntity
import com.example.ui.MohabbatViewModel
import com.example.ui.Screen
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
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

@Composable
fun HomeScreen(
    viewModel: MohabbatViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                // Background visual asset
                AsyncImage(
                    model = R.drawable.img_hero_bg_1793426551852,
                    contentDescription = "Romantic night atmosphere",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark velvet overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x66090312),
                                    Color(0x99090312),
                                    DarkBackground
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon_1793426552376),
                            contentDescription = "MOHABBAT AI Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, RoseRomantic, RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MOHABBAT AI",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Feel Every Emotion",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = RoseSoft,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary CTA: Create New Sad Reel
                    GradientButton(
                        text = "Create New Sad Reel (9:16)",
                        icon = Icons.Default.MovieCreation,
                        onClick = { viewModel.navigateTo(Screen.VideoStudio) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "home_create_reel_btn"
                    )
                }
            }
        }

        // Daily Emotion Shayari Highlight
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sher-e-Khaas (शायरी ऑफ द डे)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Dard-e-Dil",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = RoseSoft,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33EC4899))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val dailyShayari = "कांच के टुकड़े भी चमकते हैं रोशनी पाकर,\nहम तो आईना थे फिर भी तेरे शहर में बिखर गए।"

                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = dailyShayari,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontSize = 16.sp,
                                lineHeight = 26.sp,
                                color = TextPrimary
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.voiceEngine.previewVoice(dailyShayari) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speak Verse",
                                    tint = PurpleVibrant
                                )
                            }
                            IconButton(
                                onClick = { viewModel.copyToClipboard(dailyShayari) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Verse",
                                    tint = RoseSoft
                                )
                            }
                        }
                    }
                }
            }
        }

        // Studio Suites Quick Access
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "AI Creation Studios",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudioCard(
                        title = "AI Shayari",
                        subtitle = "Hindi & Hinglish",
                        icon = Icons.Default.AutoAwesome,
                        gradientColors = listOf(Color(0xFF7E22CE), Color(0xFF4C1D95)),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.ShayariGenerator) }
                    )
                    StudioCard(
                        title = "AI Voice Studio",
                        subtitle = "Emotional Narrator",
                        icon = Icons.Default.RecordVoiceOver,
                        gradientColors = listOf(Color(0xFFBE185D), Color(0xFF831843)),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.VoiceStudio) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudioCard(
                        title = "Video Studio",
                        subtitle = "9:16 Reel Maker",
                        icon = Icons.Default.MovieCreation,
                        gradientColors = listOf(Color(0xFF4338CA), Color(0xFF312E81)),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.VideoStudio) }
                    )
                    StudioCard(
                        title = "Music Studio",
                        subtitle = "Sad Piano & Strings",
                        icon = Icons.Default.MusicNote,
                        gradientColors = listOf(Color(0xFFB45309), Color(0xFF78350F)),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.MusicStudio) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StudioCard(
                        title = "Instagram Connect",
                        subtitle = "1-Tap Reel Publisher",
                        icon = Icons.Default.Share,
                        gradientColors = listOf(Color(0xFFC026D3), Color(0xFF701A75)),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.InstagramConnect) }
                    )
                    StudioCard(
                        title = "My Projects",
                        subtitle = "${projects.size} Saved Creations",
                        icon = Icons.Default.FolderSpecial,
                        gradientColors = listOf(Color(0xFF0F766E), Color(0xFF134E4A)),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.MyProjects) }
                    )
                }
            }
        }

        // Recent Projects Section
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Creations",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PurpleVibrant,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.MyProjects) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (projects.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceCard)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = RoseSoft,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No projects yet. Tap 'Create New Sad Reel' to begin!",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(projects.take(5)) { project ->
                            RecentProjectCard(
                                project = project,
                                onClick = {
                                    if (project.type == "REEL") {
                                        viewModel.navigateTo(Screen.VideoStudio)
                                    } else if (project.type == "AUDIO") {
                                        viewModel.sendToVoiceStudio(project.content)
                                    } else {
                                        viewModel.copyToClipboard(project.content)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White)
            ) { onClick() },
        color = Color.Transparent,
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xCCFFFFFF),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(180.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = DarkSurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33A855F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.type,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = RoseRomantic,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = when (project.type) {
                        "REEL" -> Icons.Default.MovieCreation
                        "AUDIO" -> Icons.Default.RecordVoiceOver
                        else -> Icons.Default.AutoAwesome
                    },
                    contentDescription = null,
                    tint = PurpleVibrant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = project.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                maxLines = 1
            )

            Text(
                text = project.content,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                maxLines = 2
            )
        }
    }
}
