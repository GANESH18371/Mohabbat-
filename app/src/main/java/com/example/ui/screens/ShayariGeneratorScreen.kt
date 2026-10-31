package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MohabbatViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.components.SecondaryGlassButton
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RoseRomantic
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShayariGeneratorScreen(
    viewModel: MohabbatViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()
    val userTopic by viewModel.userTopic.collectAsState()
    val isGenerating by viewModel.isGeneratingShayari.collectAsState()
    val currentShayari by viewModel.currentShayari.collectAsState()

    val topicChips = listOf("baarish aur yaadein", "uska intezaar", "adhuri kahani", "khamosh aankhein", "pehli mulaqat")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)
    ) {
        // Screen Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = RoseRomantic,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Shayari Generator",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "Craft deeply emotional Urdu & Hindi sher-o-shayari with Gemini AI",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }

        // Category Selector
        item {
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterPill(
                        text = category,
                        isSelected = isSelected,
                        onClick = { viewModel.setCategory(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Language & Tone Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Language
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        viewModel.languages.forEach { lang ->
                            FilterPill(
                                text = lang,
                                isSelected = lang == selectedLanguage,
                                onClick = { viewModel.setLanguage(lang) }
                            )
                        }
                    }
                }

                // Tone
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Emotional Tone",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        viewModel.tones.forEach { tone ->
                            FilterPill(
                                text = tone,
                                isSelected = tone == selectedTone,
                                onClick = { viewModel.setTone(tone) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Topic / Prompt Input
        item {
            Text(
                text = "Custom Topic / Keywords (Optional)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = userTopic,
                onValueChange = { viewModel.setUserTopic(it) },
                placeholder = { Text("e.g. baarish aur uski yaad, adhura vaada...", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shayari_topic_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = Color(0x40A855F7),
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Suggestions Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                topicChips.forEach { chip ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22A855F7))
                            .clickable { viewModel.setUserTopic(chip) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "+ $chip",
                            style = MaterialTheme.typography.labelSmall.copy(color = PurpleVibrant)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Generate Button
            GradientButton(
                text = if (isGenerating) "Weaving Poetry with AI..." else "Generate Deep Shayari ✨",
                icon = if (isGenerating) null else Icons.Default.AutoAwesome,
                enabled = !isGenerating,
                onClick = { viewModel.generateShayari() },
                modifier = Modifier.fillMaxWidth(),
                testTag = "generate_shayari_btn"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Results Section
        item {
            if (isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = RoseRomantic, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Connecting with deep emotional frequencies...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
            } else if (currentShayari != null) {
                val shayari = currentShayari!!
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = shayari.title,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontFamily = PlayfairDisplayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = RoseSoft
                                    )
                                )
                                Row {
                                    IconButton(
                                        onClick = { viewModel.copyToClipboard(shayari.fullText, shayari.title) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = PurpleVibrant
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.saveShayariProject(shayari) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BookmarkBorder,
                                            contentDescription = "Save",
                                            tint = RoseRomantic
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Four Lines Display
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x33000000))
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                shayari.lines.forEach { line ->
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontFamily = PlayfairDisplayFontFamily,
                                            fontSize = 17.sp,
                                            lineHeight = 28.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Essence: ${shayari.explanation}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Bridges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SecondaryGlassButton(
                                    text = "Voice Narration 🎙️",
                                    icon = Icons.Default.RecordVoiceOver,
                                    onClick = { viewModel.sendToVoiceStudio(shayari.fullText) },
                                    modifier = Modifier.weight(1f)
                                )
                                SecondaryGlassButton(
                                    text = "Make 9:16 Reel 🎬",
                                    icon = Icons.Default.MovieCreation,
                                    onClick = { viewModel.createReelFromShayari(shayari) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PurplePrimary else DarkSurfaceCard)
            .border(
                1.dp,
                if (isSelected) RoseSoft else Color(0x33A855F7),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextSecondary
            )
        )
    }
}
