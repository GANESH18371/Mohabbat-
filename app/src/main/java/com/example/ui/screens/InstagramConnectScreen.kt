package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.MohabbatViewModel
import com.example.ui.Screen
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
fun InstagramConnectScreen(
    viewModel: MohabbatViewModel,
    modifier: Modifier = Modifier
) {
    val isConnected by viewModel.isInstagramConnected.collectAsState()
    val username by viewModel.instagramUsername.collectAsState()
    val isPublishing by viewModel.isPublishingReel.collectAsState()
    val publishProgress by viewModel.publishProgress.collectAsState()
    val publishStatusText by viewModel.publishStatusText.collectAsState()
    val reelLines by viewModel.reelPoetryLines.collectAsState()

    var caption by remember {
        mutableStateOf("Dard itna tha ki aankhon se chhalakne na diya... 💔🥀\n\n#MohabbatAI #SadReels #HeartbreakShayari #UrduPoetry #ReelsIndia #FeelEveryEmotion")
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
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = RoseRomantic,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instagram Connect",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "Official Instagram integration, authorization & direct 9:16 Reel publisher",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }

        // Account Profile Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon_1793426552376),
                            contentDescription = "Profile Avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, RoseRomantic, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = username,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = PurpleVibrant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Mohabbat AI Official Poetry",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }

                        // Connection Status Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isConnected) Color(0x3310B981) else Color(0x33EF4444))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isConnected) "CONNECTED" else "DISCONNECTED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isConnected) Color(0xFF34D399) else Color(0xFFF87171),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33100522))
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "128", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            Text(text = "Reels", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "48.2K", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            Text(text = "Followers", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "4.2M", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White))
                            Text(text = "Total Views", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = PurpleVibrant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reels Publishing Authorization", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        }
                        Switch(
                            checked = isConnected,
                            onCheckedChange = { /* toggle mock state */ },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RoseRomantic
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Reel Publishing Studio Card
        item {
            Text(
                text = "Publish Reel to Instagram",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Selected Reel Summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33000000))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.MovieCreation, contentDescription = null, tint = RoseSoft, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Selected: Sad Romantic Reel (9:16)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Text(
                                text = "${reelLines.size} poetry lines with synchronized audio & background",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                        Text(
                            text = "Edit",
                            style = MaterialTheme.typography.labelSmall.copy(color = PurpleVibrant, fontWeight = FontWeight.Bold),
                            modifier = Modifier.clickable { viewModel.navigateTo(Screen.VideoStudio) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Caption & Hashtags
                    Text(
                        text = "Reel Caption & Viral Tags",
                        style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary, fontWeight = FontWeight.Medium)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color(0x40A855F7),
                            focusedContainerColor = DarkSurfaceCard,
                            unfocusedContainerColor = DarkSurfaceCard,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Publish Progress / Button
                    if (isPublishing) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = publishStatusText,
                                style = MaterialTheme.typography.bodyMedium.copy(color = RoseSoft, fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { publishProgress },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = RoseRomantic,
                                trackColor = Color(0x33A855F7)
                            )
                        }
                    } else {
                        GradientButton(
                            text = "Publish Reel to Instagram 🚀",
                            icon = Icons.Default.Share,
                            onClick = { viewModel.publishReelToInstagram(caption) },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "publish_instagram_reel_btn"
                        )
                    }
                }
            }
        }
    }
}
