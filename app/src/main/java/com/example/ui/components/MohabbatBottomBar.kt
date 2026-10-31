package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.Screen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleVibrant
import com.example.ui.theme.RoseRomantic
import com.example.ui.theme.RoseSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun MohabbatBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        Screen.Home,
        Screen.ShayariGenerator,
        Screen.VoiceStudio,
        Screen.VideoStudio,
        Screen.MyProjects
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color(0xF0090312),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    Color(0x40A855F7),
                    Color(0x10A855F7)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEach { screen ->
                val isSelected = currentScreen.route == screen.route
                val testTag = "nav_item_${screen.route}"

                Box(
                    modifier = Modifier
                        .testTag(testTag)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = RoseRomantic)
                        ) { onNavigate(screen) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0x33EC4899) else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) RoseSoft else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = PoppinsFontFamily,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        )
                    }
                }
            }
        }
    }
}
