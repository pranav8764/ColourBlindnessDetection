package com.dev.colourblindnessdetection

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dev.colourblindnessdetection.util.AssetHelper
import com.dev.colourblindnessdetection.util.AssetMonitor
import com.dev.colourblindnessdetection.util.IshiharaAssetManager
import com.dev.colourblindnessdetection.util.RuntimeAssetCopy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Custom Application class for global state and initialization
 */
class ColorBlindnessApplication : Application() {
    
    // Asset helpers
    lateinit var assetHelper: AssetHelper
        private set
    
    lateinit var assetMonitor: AssetMonitor
        private set
    
    // Main Ishihara asset manager
    lateinit var ishiharaAssetManager: IshiharaAssetManager
        private set
        
    // Application scope for coroutines
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize asset helpers
        assetHelper = AssetHelper(applicationContext)
        assetMonitor = AssetMonitor(applicationContext)
        
        // Initialize the primary asset manager
        ishiharaAssetManager = IshiharaAssetManager(applicationContext)
        
        // Log application startup and verify asset directories
        Log.i("ColorBlindnessApp", "Application started")
        
        // Initialize assets in background
        applicationScope.launch {
            initializeAssets()
        }
    }
    
    /**
     * Initialize all assets needed for the app
     */
    private suspend fun initializeAssets() {
        // 1. Scan assets to understand what we have
        assetMonitor.scanAllAssets()
        
        // 2. Copy to cache if needed
        val assetsAvailable = ishiharaAssetManager.checkAllImagesAvailable()
        if (!assetsAvailable) {
            Log.w("ColorBlindnessApp", "Not all images are available, trying to copy to cache")
            val copied = ishiharaAssetManager.copyAssetsToCacheIfNeeded()
            
            // Show a toast message if we're in trouble
            if (!copied) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Warning: Some image assets are missing. The test may not display properly.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            Log.d("ColorBlindnessApp", "All Ishihara test images are available")
        }
    }
}
