package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MohabbatViewModel
import com.example.ui.Screen
import com.example.ui.components.MohabbatBottomBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InstagramConnectScreen
import com.example.ui.screens.MusicStudioScreen
import com.example.ui.screens.MyProjectsScreen
import com.example.ui.screens.ShayariGeneratorScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.screens.VoiceStudioScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MohabbatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MohabbatTheme {
                val viewModel: MohabbatViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()

                // Handle Back Press
                BackHandler(enabled = currentScreen != Screen.Home) {
                    viewModel.popBack()
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground),
                    bottomBar = {
                        MohabbatBottomBar(
                            currentScreen = currentScreen,
                            onNavigate = { screen -> viewModel.navigateTo(screen) }
                        )
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                (slideInHorizontally { width -> width / 4 } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> -width / 4 } + fadeOut())
                            },
                            label = "ScreenTransition"
                        ) { screen ->
                            when (screen) {
                                Screen.Home -> HomeScreen(viewModel = viewModel)
                                Screen.ShayariGenerator -> ShayariGeneratorScreen(viewModel = viewModel)
                                Screen.VoiceStudio -> VoiceStudioScreen(viewModel = viewModel)
                                Screen.VideoStudio -> VideoStudioScreen(viewModel = viewModel)
                                Screen.MusicStudio -> MusicStudioScreen(viewModel = viewModel)
                                Screen.MyProjects -> MyProjectsScreen(viewModel = viewModel)
                                Screen.InstagramConnect -> InstagramConnectScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
