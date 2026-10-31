package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.MohabbatViewModel
import com.example.ui.Screen
import com.example.ui.components.GradientButton
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
import java.util.Date

@Composable
fun MyProjectsScreen(
    viewModel: MohabbatViewModel,
    modifier: Modifier = Modifier
) {
    val allProjects by viewModel.allProjects.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }

    val filteredProjects = allProjects.filter { project ->
        val matchesType = when (selectedFilter) {
            "REEL" -> project.type == "REEL"
            "AUDIO" -> project.type == "AUDIO"
            "SHAYARI" -> project.type == "SHAYARI"
            else -> true
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            project.title.contains(searchQuery, ignoreCase = true) ||
            project.content.contains(searchQuery, ignoreCase = true)
        }
        matchesType && matchesSearch
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
                        imageVector = Icons.Default.FolderSpecial,
                        contentDescription = null,
                        tint = RoseRomantic,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Projects",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "Saved shayaris, audio narrations, and 9:16 video reel projects",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search your creations...", color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PurpleVibrant)
                },
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All", "REEL" to "Reels", "AUDIO" to "Audios", "SHAYARI" to "Shayaris").forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PurplePrimary else DarkSurfaceCard)
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Empty State
        if (filteredProjects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurfaceCard)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = PurpleVibrant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved creations found",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create sad shayaris, voice recordings, or reels to see them here.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredProjects) { project ->
                ProjectItemCard(
                    project = project,
                    onCopy = { viewModel.copyToClipboard(project.content, project.title) },
                    onAction = {
                        when (project.type) {
                            "REEL" -> viewModel.navigateTo(Screen.VideoStudio)
                            "AUDIO" -> viewModel.sendToVoiceStudio(project.content)
                            else -> viewModel.sendToVoiceStudio(project.content)
                        }
                    },
                    onDelete = { projectToDelete = project }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = {
                Text(
                    text = "Delete Project?",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete '${projectToDelete?.title}'?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { viewModel.deleteProject(it) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseRomantic)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceCard
        )
    }
}

@Composable
private fun ProjectItemCard(
    project: ProjectEntity,
    onCopy: () -> Unit,
    onAction: () -> Unit,
    onDelete: () -> Unit
) {
    val formattedDate = DateFormat.format("dd MMM yyyy, hh:mm a", Date(project.createdAt)).toString()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        color = DarkSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33A855F7)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (project.type) {
                                    "REEL" -> Color(0x33EC4899)
                                    "AUDIO" -> Color(0x33A855F7)
                                    else -> Color(0x33F59E0B)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = project.type,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (project.type) {
                                    "REEL" -> RoseSoft
                                    "AUDIO" -> PurpleVibrant
                                    else -> Color(0xFFFBBF24)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = project.category,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = project.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = PlayfairDisplayFontFamily,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = PurpleVibrant)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseRomantic)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33A855F7))
                        .clickable { onAction() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (project.type) {
                                "REEL" -> Icons.Default.MovieCreation
                                "AUDIO" -> Icons.Default.PlayArrow
                                else -> Icons.Default.RecordVoiceOver
                            },
                            contentDescription = null,
                            tint = RoseSoft,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (project.type) {
                                "REEL" -> "Open in Studio"
                                "AUDIO" -> "Play Audio"
                                else -> "Speak Narration"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}
