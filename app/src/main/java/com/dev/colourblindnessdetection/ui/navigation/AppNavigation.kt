package com.dev.colourblindnessdetection.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.colourblindnessdetection.ui.screens.IntroScreen
import com.dev.colourblindnessdetection.ui.screens.QuestionScreen
import com.dev.colourblindnessdetection.ui.screens.ResultScreen
import com.dev.colourblindnessdetection.viewmodel.ColorBlindTestViewModel
import com.dev.colourblindnessdetection.viewmodel.ViewModelFactory

/**
 * Enum class defining the different screens in the app.
 */
enum class AppScreen {
    Intro,
    Question,
    Result
}

/**
 * Navigation component for the app.
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    // Get application context for ViewModel factory
    val application = LocalContext.current.applicationContext as Application
    val factory = ViewModelFactory(application)
    
    // Create a shared ViewModel instance with the factory
    val viewModel: ColorBlindTestViewModel = viewModel(factory = factory)
    
    NavHost(
        navController = navController,
        startDestination = AppScreen.Intro.name
    ) {
        composable(AppScreen.Intro.name) {
            IntroScreen(
                onStartClick = {
                    navController.navigate(AppScreen.Question.name) {
                        popUpTo(AppScreen.Intro.name) { inclusive = true }
                    }
                }
            )
        }
        
        composable(AppScreen.Question.name) {
            val isTestComplete by viewModel.isTestComplete.collectAsState()
            
            // If the test is complete, navigate to the result screen
            if (isTestComplete) {
                navController.navigate(AppScreen.Result.name) {
                    popUpTo(AppScreen.Question.name) { inclusive = true }
                }
            }
            
            QuestionScreen(
                onAnswerSelected = { answer ->
                    viewModel.answerQuestion(answer)
                },
                viewModel = viewModel
            )
        }
        
        composable(AppScreen.Result.name) {
            val testResult by viewModel.testResult.collectAsState()
            
            ResultScreen(
                testResult = testResult,
                onRestartClick = {
                    viewModel.restartTest()
                    navController.navigate(AppScreen.Intro.name) {
                        popUpTo(AppScreen.Result.name) { inclusive = true }
                    }
                }
            )
        }
    }
}
