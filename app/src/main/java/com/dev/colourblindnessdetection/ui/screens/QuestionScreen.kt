package com.dev.colourblindnessdetection.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.colourblindnessdetection.ColorBlindnessApplication
import com.dev.colourblindnessdetection.data.TestRepository
import com.dev.colourblindnessdetection.model.ColorBlindTestQuestion
import com.dev.colourblindnessdetection.viewmodel.ColorBlindTestViewModel
import kotlinx.coroutines.launch
import java.io.File

/**
 * Screen that displays a single test question.
 */
@Composable
fun QuestionScreen(
    onAnswerSelected: (String) -> Unit,
    viewModel: ColorBlindTestViewModel = viewModel()
) {
    // ViewModel is now passed as a parameter
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val currentQuestionFromViewModel by viewModel.currentQuestion.collectAsState()
    
    // Use the currentQuestion from viewModel which is guaranteed to be updated
    val displayQuestion = currentQuestionFromViewModel
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Get repository for image loading
    val repository = remember { TestRepository(context) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        displayQuestion?.let { currentQuestion ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress indicator
                LinearProgressIndicator(
                    progress = (currentQuestionIndex + 1).toFloat() / questions.size,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Question counter
                Text(
                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Question text
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Ishihara test plate image
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.size(280.dp)
                ) {
                    // Use repository to load image
                    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
                    var loadError by remember { mutableStateOf(false) }
                    var isRecovering by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(currentQuestion.imageResPath, currentQuestionIndex) {
                        try {
                            // Use the repository to load the image
                            bitmap = repository.getImageBitmap(currentQuestion.imageResPath)
                            loadError = bitmap == null
                        } catch (e: Exception) {
                            Log.e("QuestionScreen", "Error loading image: ${e.message}")
                            loadError = true
                        }
                    }
                    
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Ishihara Plate ${currentQuestion.id}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    } else {
                        // Fallback if image loading fails
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isRecovering) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Attempting to recover images...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    text = if (loadError) 
                                        "Error loading image:\n${currentQuestion.imageResPath}" 
                                    else 
                                        "Loading image...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                
                                if (loadError) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            isRecovering = true
                                            coroutineScope.launch {
                                                try {
                                                    // Attempt to recover images by forcing a copy to cache
                                                    val success = repository.forceImageCopy()
                                                    if (success) {
                                                        // Retry loading with the repository
                                                        bitmap = repository.getImageBitmap(currentQuestion.imageResPath)
                                                        loadError = bitmap == null
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("QuestionScreen", "Recovery failed", e)
                                                } finally {
                                                    isRecovering = false
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Recover Images")
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Answer options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentQuestion.options.forEach { option ->
                        OutlinedButton(
                            onClick = { onAnswerSelected(option) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        } ?: run {
            // Fallback if there's no question (shouldn't happen)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Loading test questions...",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
