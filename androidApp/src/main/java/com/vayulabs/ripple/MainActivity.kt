package com.vayulabs.ripple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vayulabs.ripple.audio.SoundscapeAudioEngine
import com.vayulabs.ripple.data.UserPreferences
import com.vayulabs.ripple.haptics.HapticEngine
import com.vayulabs.ripple.navigation.Screen
import com.vayulabs.ripple.presentation.breathe.BreathingScreen
import com.vayulabs.ripple.presentation.components.AppleMusicBottomBar
import com.vayulabs.ripple.presentation.components.AppleMusicMiniPlayer
import com.vayulabs.ripple.presentation.components.FluidAmbientBackground
import com.vayulabs.ripple.presentation.components.IbmEasing
import com.vayulabs.ripple.presentation.grounding.Grounding54321Screen
import com.vayulabs.ripple.presentation.home.HomeScreen
import com.vayulabs.ripple.presentation.journal.ThoughtDissolverScreen
import com.vayulabs.ripple.presentation.quotes.QuotesScreen
import com.vayulabs.ripple.presentation.sensory.*
import com.vayulabs.ripple.presentation.sounds.SoundMixerScreen
import com.vayulabs.ripple.presentation.splash.SplashScreen
import com.vayulabs.ripple.presentation.welcome.WelcomeScreen
import com.vayulabs.ripple.theme.*
import com.vayulabs.ripple.shared.repository.QuotesRepository
import com.vayulabs.ripple.shared.statemachine.BreathingStateMachine

class MainActivity : ComponentActivity() {
    private val audioEngine = SoundscapeAudioEngine()

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        UserPreferences.init(applicationContext)
        val hapticEngine = HapticEngine(applicationContext)
        val quotesRepository = QuotesRepository()

        setContent {
            val isDark = ThemeState.isDarkMode.value

            RippleTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val breathingStateMachine = remember { BreathingStateMachine(scope) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Top level routes that show the floating bottom pill
                val bottomBarRoutes = setOf(
                    Screen.Home.route,
                    Screen.Breathe.route,
                    Screen.SensoryHub.route,
                    Screen.Sounds.route,
                    Screen.Quotes.route
                )

                val showBottomBar = currentRoute in bottomBarRoutes

                // Initial start destination: Welcome on first launch, otherwise Home
                val startDest = remember {
                    if (UserPreferences.isFirstLaunch.value || UserPreferences.currentUserName.value.isNullOrBlank()) {
                        Screen.Welcome.route
                    } else {
                        Screen.Home.route
                    }
                }

                // Handle direct intent route from Quick Settings Tile or notifications
                val extraRoute = remember { intent?.getStringExtra("EXTRA_START_ROUTE") }
                var isSplashShowing by remember { mutableStateOf(extraRoute.isNullOrBlank()) }
                LaunchedEffect(extraRoute) {
                    if (!extraRoute.isNullOrBlank()) {
                        navController.navigate(extraRoute)
                    }
                }

                AnimatedContent(
                    targetState = isSplashShowing,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400, easing = LinearOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(350, easing = FastOutLinearInEasing))
                    },
                    label = "SplashTransition"
                ) { showSplash ->
                    if (showSplash) {
                        SplashScreen(
                            hapticEngine = hapticEngine,
                            onSplashFinished = { isSplashShowing = false }
                        )
                    } else {
                        FluidAmbientBackground {
                            Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = startDest,
                            modifier = Modifier.fillMaxSize(),
                            // Dynamic, vibrant Directional Scaling & Non-Overlapping Transitions
                            // As per UX Design transition principles: outgoing exits swiftly so words never ghost,
                            // incoming scales into position with a subtle staggered delay
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(400, delayMillis = 90, easing = LinearOutSlowInEasing)
                                ) + scaleIn(
                                    initialScale = 0.92f,
                                    animationSpec = tween(450, delayMillis = 70, easing = IbmEasing)
                                )
                            },
                            exitTransition = {
                                fadeOut(
                                    animationSpec = tween(180, easing = FastOutLinearInEasing)
                                ) + scaleOut(
                                    targetScale = 0.95f,
                                    animationSpec = tween(180, easing = FastOutLinearInEasing)
                                )
                            },
                            popEnterTransition = {
                                fadeIn(
                                    animationSpec = tween(400, delayMillis = 90, easing = LinearOutSlowInEasing)
                                ) + scaleIn(
                                    initialScale = 1.05f,
                                    animationSpec = tween(450, delayMillis = 70, easing = IbmEasing)
                                )
                            },
                            popExitTransition = {
                                fadeOut(
                                    animationSpec = tween(180, easing = FastOutLinearInEasing)
                                ) + scaleOut(
                                    targetScale = 0.92f,
                                    animationSpec = tween(180, easing = FastOutLinearInEasing)
                                )
                            }
                        ) {
                            composable(Screen.Welcome.route) {
                                WelcomeScreen(
                                    onContinue = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Welcome.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.Home.route) {
                                HomeScreen(
                                    quotesRepository = quotesRepository,
                                    onNavigate = { route -> navController.navigate(route) }
                                )
                            }

                            composable(Screen.Breathe.route) {
                                BreathingScreen(
                                    stateMachine = breathingStateMachine,
                                    hapticEngine = hapticEngine,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.SensoryHub.route) {
                                SensoryHubScreen(
                                    onNavigateToSensory = { route -> navController.navigate(route) },
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.FluidCanvas.route) {
                                FluidCanvasScreen(onNavigateBack = { navController.popBackStack() })
                            }

                            composable(Screen.WorryStone.route) {
                                WorryStoneScreen(
                                    hapticEngine = hapticEngine,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.BubblePop.route) {
                                BubblePopScreen(
                                    hapticEngine = hapticEngine,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Kaleidoscope.route) {
                                KaleidoscopeScreen(onNavigateBack = { navController.popBackStack() })
                            }

                            composable(Screen.ColorSort.route) {
                                ColorSortScreen(onNavigateBack = { navController.popBackStack() })
                            }

                            composable(Screen.Quotes.route) {
                                QuotesScreen(
                                    quotesRepository = quotesRepository,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.Sounds.route) {
                                SoundMixerScreen(
                                    audioEngine = audioEngine,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.ThoughtDissolver.route) {
                                ThoughtDissolverScreen(onNavigateBack = { navController.popBackStack() })
                            }

                            composable(Screen.Grounding54321.route) {
                                Grounding54321Screen(
                                    hapticEngine = hapticEngine,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToBreathe = {
                                        navController.navigate(Screen.Breathe.route) {
                                            popUpTo(Screen.Grounding54321.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.ZenSand.route) {
                                ZenSandScreen(
                                    hapticEngine = hapticEngine,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.SingingBowl.route) {
                                SingingBowlScreen(
                                    hapticEngine = hapticEngine,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }

                        // Truly Floating Bottom Bar Overlay (Apple Music Inspired Acrylic Glass Pill)
                        AnimatedVisibility(
                            visible = showBottomBar,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(bottom = 12.dp),
                            enter = fadeIn(tween(400, easing = LinearOutSlowInEasing)) + slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(400, easing = IbmEasing)
                            ),
                            exit = fadeOut(tween(250, easing = FastOutLinearInEasing)) + slideOutVertically(
                                targetOffsetY = { it / 3 },
                                animationSpec = tween(250, easing = FastOutLinearInEasing)
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppleMusicMiniPlayer(
                                    visible = audioEngine.hasActiveAudio,
                                    title = audioEngine.activeTrackTitle,
                                    subtitle = audioEngine.activeTrackSubtitle,
                                    isPlaying = audioEngine.isPlaying,
                                    onTogglePlay = {
                                        hapticEngine.triggerStoneFriction()
                                        if (audioEngine.isPlaying) {
                                            audioEngine.pause()
                                        } else {
                                            audioEngine.resume()
                                        }
                                    },
                                    onClose = {
                                        hapticEngine.triggerStoneFriction()
                                        audioEngine.stop()
                                    },
                                    onClick = { navController.navigate(Screen.Sounds.route) },
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                AppleMusicBottomBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { targetRoute ->
                                        hapticEngine.triggerStoneFriction()
                                        navController.navigate(targetRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
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
}
}
}
