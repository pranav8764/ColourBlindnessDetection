package com.dev.colourblindnessdetection.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility class to copy assets at runtime if needed
 */
class RuntimeAssetCopy(private val context: Context) {
    
    private val TAG = "RuntimeAssetCopy"
    
    /**
     * Copy all Ishihara test images to the app's cache directory
     * Returns the directory where images were saved
     */
    suspend fun copyIshiharaImages(): File? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting to copy Ishihara images to cache directory")
            
            // Create a directory in the cache for the images
            val imagesDir = File(context.cacheDir, "ishihara_images").apply { 
                if (!exists()) mkdirs()
            }
            
            // List of expected image files
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
            
            var successCount = 0
            
            // Try to copy each image
            for (imageName in expectedImages) {
                // Try different paths for the source image
                val possiblePaths = listOf(
                    imageName,                  // Direct in assets
                    "images/$imageName",        // In images subfolder
                    "assests/images/$imageName" // In assests/images
                )
                
                var foundPath = false
                
                // Try each path until we find the image - no lambdas with break
                for (path in possiblePaths) {
                    if (foundPath) continue
                    
                    try {
                        // Source
                        val inputStream = context.assets.open(path)
                        inputStream.use { input ->
                            // Destination
                            val outFile = File(imagesDir, imageName)
                            FileOutputStream(outFile).use { outputStream ->
                                // Copy bytes
                                val buffer = ByteArray(1024)
                                var length: Int
                                while (input.read(buffer).also { length = it } > 0) {
                                    outputStream.write(buffer, 0, length)
                                }
                                outputStream.flush()
                            }
                        }
                        
                        foundPath = true
                        successCount++
                        Log.d(TAG, "Successfully copied $path to cache")
                    } catch (e: IOException) {
                        // Try next path
                    }
                }
                
                if (!foundPath) {
                    Log.e(TAG, "Failed to copy $imageName from any location")
                }
            }
            
            Log.d(TAG, "Copied $successCount of ${expectedImages.size} images to cache directory")
            
            if (successCount > 0) {
                return@withContext imagesDir
            } else {
                Log.e(TAG, "No images were successfully copied")
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error copying assets to cache", e)
            return@withContext null
        }
    }
}
