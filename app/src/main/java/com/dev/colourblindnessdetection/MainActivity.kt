package com.dev.colourblindnessdetection

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.dev.colourblindnessdetection.ui.navigation.AppNavigation
import com.dev.colourblindnessdetection.ui.theme.ColorBlindnessDetectionTheme
import com.dev.colourblindnessdetection.util.AssetHelper
import com.dev.colourblindnessdetection.util.AssetMonitor
import com.dev.colourblindnessdetection.util.RuntimeAssetCopy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AssetHelper and check if assets are accessible
        val assetHelper = AssetHelper(applicationContext)
        val imagesPath = "images"
        
        // Log available image assets
        val imageFiles = assetHelper.listAssetFiles(imagesPath)
        Log.d("MainActivity", "Found ${imageFiles.size} images in assets/$imagesPath: $imageFiles")
        
        // Print asset directory structure for debugging
        try {
            val rootFiles = assets.list("")
            if (rootFiles != null) {
                Log.d("MainActivity", "Root asset directories: ${rootFiles.joinToString()}")
                
                for (dir in rootFiles) {
                    val filesInDir = assets.list(dir)
                    if (filesInDir != null && filesInDir.isNotEmpty()) {
                        Log.d("MainActivity", "Files in $dir: ${filesInDir.joinToString()}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error listing assets", e)
        }
        
        // Verify assets at startup
        verifyAssets()
        
        setContent {
            ColorBlindnessDetectionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
    
    /**
     * Verify assets and display a message if there are issues
     */
    private fun verifyAssets() {
        lifecycleScope.launch {
            val assetMonitor = AssetMonitor(applicationContext)
            val results = assetMonitor.scanAllAssets()
            
            // Check if images are available
            val expectedImageCount = 10
            val foundImages = results.count { it.value }
            
            Log.d("MainActivity", "Asset Verification: Found $foundImages of $expectedImageCount expected images")
            
            if (foundImages < expectedImageCount) {
                // Try to copy images to cache
                val assetCopier = RuntimeAssetCopy(applicationContext)
                val cacheDir = assetCopier.copyIshiharaImages()
                
                withContext(Dispatchers.Main) {
                    if (cacheDir != null) {
                        // Success - images recovered to cache
                        val cachedImageCount = cacheDir.listFiles()?.size ?: 0
                        Toast.makeText(
                            this@MainActivity,
                            "Some image assets were missing. Recovered $cachedImageCount images to cache.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Failed to recover images
                        Toast.makeText(
                            this@MainActivity,
                            "Warning: Some image assets are missing. The app may not function correctly.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
