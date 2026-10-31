package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.GlassStroke
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RoseRomantic
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.random.Random

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderBrush: Brush = Brush.linearGradient(
        listOf(
            Color(0x66A855F7),
            Color(0x33EC4899),
            Color(0x117E22CE)
        )
    ),
    backgroundBrush: Brush = Brush.verticalGradient(
        listOf(
            Color(0xDD180B2E),
            Color(0xEE0E051C)
        )
    ),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundBrush)
            .border(
                border = BorderStroke(1.dp, borderBrush),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "gradient_button",
    enabled: Boolean = true
) {
    val gradient = Brush.horizontalGradient(
        listOf(
            PurplePrimary,
            RoseRomantic,
            PurpleVibrant
        )
    )

    Surface(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White)
            ) { onClick() },
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (enabled) gradient else Brush.linearGradient(listOf(Color.DarkGray, Color.Gray))),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SecondaryGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "secondary_button"
) {
    Surface(
        modifier = modifier
            .testTag(testTag)
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = PurpleVibrant)
            ) { onClick() },
        color = Color(0x33A855F7),
        border = BorderStroke(1.dp, Color(0x66C084FC)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoseSoft,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = PoppinsFontFamily,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun AudioWaveformVisualizer(
    isAnimating: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 24
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33100522))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val factor = kotlin.math.sin((phase * 2 * Math.PI + i * 0.4)).toFloat()
            val heightFraction = if (isAnimating) {
                (0.2f + 0.8f * kotlin.math.abs(factor)).coerceIn(0.15f, 1f)
            } else {
                0.2f
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(fraction = heightFraction)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                PurpleVibrant,
                                RoseRomantic
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun ReelVideoCanvas(
    backgroundType: String, // "rain", "galaxy", "velvet", "neon"
    poetryLines: List<String>,
    currentLineIndex: Int,
    isPlaying: Boolean,
    musicName: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reelParticles")
    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainOffset"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = modifier
            .aspectRatio(9f / 16f)
            .clip(RoundedCornerShape(24.dp))
            .border(BorderStroke(2.dp, Brush.linearGradient(listOf(PurpleVibrant, RoseRomantic, Color.Transparent))), RoundedCornerShape(24.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Visual Background Layer
        when (backgroundType) {
            "rain" -> {
                AsyncImage(
                    model = R.drawable.img_reel_bg_rain_1793426552252,
                    contentDescription = "Rain window",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            "galaxy" -> {
                AsyncImage(
                    model = R.drawable.img_reel_bg_sunset_1793426568724,
                    contentDescription = "Rooftop galaxy",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                AsyncImage(
                    model = R.drawable.img_hero_bg_1793426551852,
                    contentDescription = "Velvet bokeh",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Dark Vignette & Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x990A0314),
                            Color(0x44000000),
                            Color(0xCC080210)
                        )
                    )
                )
        )

        // Animated particles / rain layer on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Rain streaks
            if (backgroundType == "rain" && isPlaying) {
                val drops = 35
                for (i in 0 until drops) {
                    val x = (i * 37) % width.toInt()
                    val startY = ((rainOffset * height + i * 45) % height)
                    drawLine(
                        color = Color(0x40E2E8F0),
                        start = Offset(x.toFloat(), startY),
                        end = Offset(x.toFloat() - 4, startY + 28),
                        strokeWidth = 1.5f
                    )
                }
            }

            // Glowing dust motes / stars
            val stars = 20
            for (i in 0 until stars) {
                val sx = (i * 59) % width.toInt()
                val sy = (i * 91) % height.toInt()
                val r = (2f + (i % 3)) * (if (isPlaying) glowPulse else 1f)
                drawCircle(
                    color = if (i % 2 == 0) Color(0x66C084FC) else Color(0x55F472B6),
                    radius = r,
                    center = Offset(sx.toFloat(), sy.toFloat())
                )
            }
        }

        // Reel Top Brand Badge
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x88000000))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) RoseRomantic else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MOHABBAT REEL 9:16",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            // Sound indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x88000000))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = PurpleVibrant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPlaying) "Playing" else "Paused",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        }

        // Center Animated Poetry / Lyrical Subtitle
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            poetryLines.forEachIndexed { index, line ->
                val isHighlighted = index == currentLineIndex
                val alpha = if (isHighlighted) 1f else 0.45f
                val scale = if (isHighlighted) 1.05f else 0.95f

                Text(
                    text = line,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = if (isHighlighted) 21.sp else 17.sp,
                        lineHeight = if (isHighlighted) 30.sp else 26.sp,
                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                        color = if (isHighlighted) Color.White else Color(0xCCFFFFFF),
                        textAlign = TextAlign.Center,
                        shadow = Shadow(
                            color = if (isHighlighted) PurpleVibrant else Color.Black,
                            blurRadius = if (isHighlighted) 16f else 6f,
                            offset = Offset(0f, 2f)
                        )
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                )
            }
        }

        // Bottom Music Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x99100522))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = RoseRomantic,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = musicName,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
