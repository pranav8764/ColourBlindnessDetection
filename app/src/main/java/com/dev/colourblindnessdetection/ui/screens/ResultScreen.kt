package com.dev.colourblindnessdetection.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dev.colourblindnessdetection.ColorBlindnessApplication
import com.dev.colourblindnessdetection.data.TestRepository
import com.dev.colourblindnessdetection.model.ColorBlindnessType
import com.dev.colourblindnessdetection.model.TestResult
import com.dev.colourblindnessdetection.ui.components.ColorBlindnessFilter
import kotlinx.coroutines.launch
import java.io.File

/**
 * Screen that displays the test results.
 */
@Composable
fun ResultScreen(
    testResult: TestResult?,
    onRestartClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { TestRepository(context) }
    
    var showSimulationDropdown by remember { mutableStateOf(false) }
    var selectedSimulation by remember { mutableStateOf(ColorBlindnessType.NORMAL) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        testResult?.let { result ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Test Results",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Result card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Diagnosis box
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Diagnosis",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = when (result.colorBlindnessType) {
                                        ColorBlindnessType.NORMAL -> "Normal Color Vision"
                                        ColorBlindnessType.PROTANOPIA -> "Protanopia (Red-Green Color Blindness)"
                                        ColorBlindnessType.DEUTERANOPIA -> "Deuteranopia (Red-Green Color Blindness)"
                                        ColorBlindnessType.TRITANOPIA -> "Tritanopia (Blue-Yellow Color Blindness)"
                                        ColorBlindnessType.INCONCLUSIVE -> "Inconclusive Results"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Description
                        Text(
                            text = result.colorBlindnessType.getDescription(),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Result breakdown
                        Text(
                            text = "Result Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ResultStatItem(label = "Normal vision matches:", count = result.normalCount, total = result.responses.size)
                        ResultStatItem(label = "Protanopia matches:", count = result.protanopiaCount, total = result.responses.size)
                        ResultStatItem(label = "Deuteranopia matches:", count = result.deuteranopiaCount, total = result.responses.size)
                        ResultStatItem(label = "Tritanopia matches:", count = result.tritanopiaCount, total = result.responses.size)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Color Blindness Simulation
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Color Blindness Simulation",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Box {
                                IconButton(onClick = { showSimulationDropdown = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Change simulation type"
                                    )
                                }
                                
                                DropdownMenu(
                                    expanded = showSimulationDropdown,
                                    onDismissRequest = { showSimulationDropdown = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Normal Vision") },
                                        onClick = {
                                            selectedSimulation = ColorBlindnessType.NORMAL
                                            showSimulationDropdown = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Protanopia") },
                                        onClick = {
                                            selectedSimulation = ColorBlindnessType.PROTANOPIA
                                            showSimulationDropdown = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Deuteranopia") },
                                        onClick = {
                                            selectedSimulation = ColorBlindnessType.DEUTERANOPIA
                                            showSimulationDropdown = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Tritanopia") },
                                        onClick = {
                                            selectedSimulation = ColorBlindnessType.TRITANOPIA
                                            showSimulationDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "This is how someone with ${
                                when (selectedSimulation) {
                                    ColorBlindnessType.NORMAL -> "normal color vision"
                                    ColorBlindnessType.PROTANOPIA -> "protanopia"
                                    ColorBlindnessType.DEUTERANOPIA -> "deuteranopia"
                                    ColorBlindnessType.TRITANOPIA -> "tritanopia"
                                    ColorBlindnessType.INCONCLUSIVE -> "normal color vision"
                                }
                            } might see the world:",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Show a color blindness simulation example with the first plate
                        val firstImagePath = result.responses.firstOrNull()?.first?.imageResPath
                        
                        ColorBlindnessFilter(type = selectedSimulation) {
                            if (firstImagePath != null) {
                                // Load image from assets with multiple path options
                                var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
                                var loadError by remember { mutableStateOf(false) }
                                var isRecovering by remember { mutableStateOf(false) }
                                
                                LaunchedEffect(firstImagePath) {
                                    try {
                                        bitmap = repository.getImageBitmap(firstImagePath)
                                        loadError = bitmap == null
                                    } catch (e: Exception) {
                                        Log.e("ResultScreen", "Error loading image: ${e.message}")
                                        loadError = true
                                    }
                                }
                                
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap!!.asImageBitmap(),
                                        contentDescription = "Color blindness simulation example",
                                        modifier = Modifier
                                            .size(200.dp)
                                            .padding(8.dp)
                                    )
                                } else {
                                    Column(
                                        modifier = Modifier.size(200.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        if (isRecovering) {
                                            CircularProgressIndicator()
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Recovering image...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            Text(
                                                text = if (loadError) 
                                                    "Failed to load simulation image" 
                                                else 
                                                    "Loading simulation...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center
                                            )
                                            
                                            if (loadError) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Button(
                                                    onClick = {
                                                        isRecovering = true
                                                        coroutineScope.launch {
                                                            try {
                                                                // Force copy images to cache
                                                                val success = repository.forceImageCopy()
                                                                if (success) {
                                                                    // Try to load from cache
                                                                    bitmap = repository.getImageBitmap(firstImagePath)
                                                                    loadError = bitmap == null
                                                                }
                                                            } catch (e: Exception) {
                                                                Log.e("ResultScreen", "Recovery failed", e)
                                                            } finally {
                                                                isRecovering = false
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Text("Recover Image", 
                                                         style = MaterialTheme.typography.bodySmall)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = "No image available for simulation",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Restart button
                    Button(
                        onClick = onRestartClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Restart Test")
                    }
                    
                    Spacer(modifier = Modifier.size(16.dp))
                    
                    // Share button
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "I took a color blindness test and my result shows: " +
                                            when (result.colorBlindnessType) {
                                                ColorBlindnessType.NORMAL -> "Normal Color Vision"
                                                ColorBlindnessType.PROTANOPIA -> "Protanopia (Red-Green Color Blindness)"
                                                ColorBlindnessType.DEUTERANOPIA -> "Deuteranopia (Red-Green Color Blindness)"
                                                ColorBlindnessType.TRITANOPIA -> "Tritanopia (Blue-Yellow Color Blindness)"
                                                ColorBlindnessType.INCONCLUSIVE -> "Inconclusive Results"
                                            }
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share result via"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share"
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Share Results")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Educational Resources Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Educational Resources",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Learn more about your results and possible solutions:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Display links based on the color blindness type
                        when (result.colorBlindnessType) {
                            ColorBlindnessType.PROTANOPIA -> {
                                ResourceLinkItem(
                                    title = "Understanding Protanopia (Red-Green Color Blindness)",
                                    url = "https://www.aao.org/eye-health/diseases/what-is-color-blindness"
                                )
                                ResourceLinkItem(
                                    title = "Protanopia: Correction Options and Assistive Technology",
                                    url = "https://www.allaboutvision.com/conditions/color-blindness/"
                                )
                                ResourceLinkItem(
                                    title = "Living with Red-Green Color Blindness",
                                    url = "https://nei.nih.gov/learn-about-eye-health/eye-conditions-and-diseases/color-blindness"
                                )
                            }
                            ColorBlindnessType.DEUTERANOPIA -> {
                                ResourceLinkItem(
                                    title = "Understanding Deuteranopia (Red-Green Color Blindness)",
                                    url = "https://www.aao.org/eye-health/diseases/what-is-color-blindness"
                                )
                                ResourceLinkItem(
                                    title = "Deuteranopia: Correction Options and Assistive Technology",
                                    url = "https://www.allaboutvision.com/conditions/color-blindness/"
                                )
                                ResourceLinkItem(
                                    title = "Living with Red-Green Color Blindness",
                                    url = "https://www.color-blindness.com/living-with-color-vision-deficiency/"
                                )
                            }
                            ColorBlindnessType.TRITANOPIA -> {
                                ResourceLinkItem(
                                    title = "Understanding Tritanopia (Blue-Yellow Color Blindness)",
                                    url = "https://www.aao.org/eye-health/diseases/what-is-color-blindness"
                                )
                                ResourceLinkItem(
                                    title = "Tritanopia: Rare Blue-Yellow Color Blindness",
                                    url = "https://www.color-blindness.com/tritanopia-blue-yellow-color-blindness/"
                                )
                                ResourceLinkItem(
                                    title = "Treatment Options for Blue-Yellow Color Blindness",
                                    url = "https://www.allaboutvision.com/conditions/color-blindness/"
                                )
                            }
                            ColorBlindnessType.NORMAL -> {
                                ResourceLinkItem(
                                    title = "Understanding Color Vision and Eye Health",
                                    url = "https://www.aao.org/eye-health/tips-prevention/eye-health-101"
                                )
                                ResourceLinkItem(
                                    title = "Maintaining Healthy Color Vision",
                                    url = "https://www.nei.nih.gov/learn-about-eye-health/healthy-vision"
                                )
                            }
                            ColorBlindnessType.INCONCLUSIVE -> {
                                ResourceLinkItem(
                                    title = "Types of Color Blindness",
                                    url = "https://www.aao.org/eye-health/diseases/what-is-color-blindness"
                                )
                                ResourceLinkItem(
                                    title = "When to See an Eye Doctor for Color Vision Testing",
                                    url = "https://www.allaboutvision.com/eye-exam/eye-tests/"
                                )
                                ResourceLinkItem(
                                    title = "Professional Color Vision Testing Options",
                                    url = "https://www.color-blindness.com/color-blindness-tests/"
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Disclaimer
                Text(
                    text = "Disclaimer: This test provides an indication only and is not a medical diagnosis. If you are concerned about your color vision, please consult an eye care professional.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } ?: run {
            // Fallback if there's no test result
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No test results available. Please take the test first.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(onClick = onRestartClick) {
                    Text(text = "Take Test")
                }
            }
        }
    }
}

/**
 * Component that displays a clickable link to an educational resource.
 */
@Composable
private fun ResourceLinkItem(title: String, url: String) {
    val context = LocalContext.current
    val urlIntent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(url)) }
    
    OutlinedButton(
        onClick = { context.startActivity(urlIntent) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Tap to open",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Component that displays a single statistic from the test results.
 */
@Composable
private fun ResultStatItem(label: String, count: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$count / $total",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.size(8.dp))
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = if (total > 0) count.toFloat() / total else 0f
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}
