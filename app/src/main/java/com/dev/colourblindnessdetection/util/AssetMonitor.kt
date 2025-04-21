package com.dev.colourblindnessdetection.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class to monitor and verify assets at runtime
 */
class AssetMonitor(private val context: Context) {
    
    private val TAG = "AssetMonitor"
    
    /**
     * Scan all assets and log their availability
     */
    suspend fun scanAllAssets() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== ASSET MONITORING SCAN STARTED ===")
            
            // Check root assets
            val rootAssets = context.assets.list("") ?: emptyArray()
            Log.d(TAG, "Root assets (${rootAssets.size}): ${rootAssets.joinToString()}")
            
            // Check if images directory exists
            val hasImagesDir = rootAssets.contains("images")
            if (hasImagesDir) {
                val imageAssets = context.assets.list("images") ?: emptyArray()
                Log.d(TAG, "Images directory assets (${imageAssets.size}): ${imageAssets.joinToString()}")
            } else {
                Log.w(TAG, "No 'images' directory found in root assets")
            }
            
            // Check for assests directory (the custom assets location)
            try {
                val assestsAssets = context.assets.list("assests") ?: emptyArray()
                Log.d(TAG, "Assests directory assets (${assestsAssets.size}): ${assestsAssets.joinToString()}")
                
                if (assestsAssets.contains("images")) {
                    val assestsImageAssets = context.assets.list("assests/images") ?: emptyArray()
                    Log.d(TAG, "Assests/images directory assets (${assestsImageAssets.size}): ${assestsImageAssets.joinToString()}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "No 'assests' directory found: ${e.message}")
            }
            
            // Check specific image files
            val expectedImages = listOf(
                "Screenshot 2025-04-21 210205.png",
                "Screenshot 2025-04-21 210222.png",
                "Screenshot 2025-04-21 210248.png",
                "Screenshot 2025-04-21 210301.png",
                "Screenshot 2025-04-21 210321.png",
                "Screenshot 2025-04-21 210341.png",
                "Screenshot 2025-04-21 210355.png",
                "Screenshot 2025-04-21 210417.png",
                "Screenshot 2025-04-21 210429.png",
                "Screenshot 2025-04-21 210443.png"
            )
            
            val results = mutableMapOf<String, Boolean>()
            
            // Check each image in different possible locations
            for (imageName in expectedImages) {
                val possiblePaths = listOf(
                    imageName,                  // Direct in assets
                    "images/$imageName",        // In images subfolder
                    "assests/images/$imageName" // In assests/images
                )
                
                var imageFound = false
                
                // Avoid using break in lambda by using a regular for loop
                for (path in possiblePaths) {
                    if (imageFound) continue
                    
                    try {
                        // Use auto-closing resources
                        val inputStream = context.assets.open(path)
                        inputStream.close()
                        
                        imageFound = true
                        results[imageName] = true
                        Log.d(TAG, "✓ Image found: $path")
                    } catch (e: Exception) {
                        // Just continue to next path
                    }
                }
                
                if (!imageFound) {
                    results[imageName] = false
                    Log.e(TAG, "✗ Image NOT found: $imageName (tried multiple locations)")
                }
            }
            
            // Summary of image availability
            val foundCount = results.values.count { it }
            Log.d(TAG, "=== ASSET SCAN SUMMARY ===")
            Log.d(TAG, "Found $foundCount of ${expectedImages.size} expected image assets")
            Log.d(TAG, "Missing images: ${results.filterValues { !it }.keys.joinToString()}")
            Log.d(TAG, "=== ASSET MONITORING SCAN COMPLETED ===")
            
            return@withContext results
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning assets", e)
            return@withContext emptyMap<String, Boolean>()
        }
    }
}
