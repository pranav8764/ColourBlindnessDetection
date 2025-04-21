package com.dev.colourblindnessdetection.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Centralized manager for handling Ishihara test assets
 */
class IshiharaAssetManager(private val context: Context) {

    private val TAG = "IshiharaAssetManager"
    
    // The standard list of Ishihara test images
    val expectedImages = listOf(
        "Screenshot 2025-04-21 210205.png", // 12
        "Screenshot 2025-04-21 210222.png", // 8
        "Screenshot 2025-04-21 210248.png", // 29
        "Screenshot 2025-04-21 210301.png", // 5
        "Screenshot 2025-04-21 210321.png", // 3
        "Screenshot 2025-04-21 210341.png", // 15
        "Screenshot 2025-04-21 210355.png", // 74
        "Screenshot 2025-04-21 210417.png", // 6
        "Screenshot 2025-04-21 210429.png", // 45
        "Screenshot 2025-04-21 210443.png"  // 16
    )
    
    // The numbers in the images
    val imageNumbers = mapOf(
        "Screenshot 2025-04-21 210205.png" to "12",
        "Screenshot 2025-04-21 210222.png" to "8",
        "Screenshot 2025-04-21 210248.png" to "29",
        "Screenshot 2025-04-21 210301.png" to "5",
        "Screenshot 2025-04-21 210321.png" to "3",
        "Screenshot 2025-04-21 210341.png" to "15",
        "Screenshot 2025-04-21 210355.png" to "74",
        "Screenshot 2025-04-21 210417.png" to "6",
        "Screenshot 2025-04-21 210429.png" to "45",
        "Screenshot 2025-04-21 210443.png" to "16"
    )
    
    // Potential locations for images
    private val assetsPaths = listOf(
        "",                 // Root
        "images/",          // Images folder
        "assests/images/"   // Custom assests folder
    )
    
    // Path to cache directory
    private val cacheDir by lazy {
        File(context.cacheDir, "ishihara_images").apply {
            if (!exists()) mkdirs()
        }
    }
    
    /**
     * Check if all images are available in assets
     */
    suspend fun checkAllImagesAvailable(): Boolean = withContext(Dispatchers.IO) {
        var allAvailable = true
        
        for (image in expectedImages) {
            var imageFound = false
            
            // Check in assets
            for (path in assetsPaths) {
                try {
                    context.assets.open(path + image).close()
                    imageFound = true
                    break
                } catch (e: Exception) {
                    // Continue to next path
                }
            }
            
            // Check in cache if not found in assets
            if (!imageFound) {
                val cacheFile = File(cacheDir, image)
                imageFound = cacheFile.exists() && cacheFile.length() > 0
            }
            
            if (!imageFound) {
                Log.w(TAG, "Image not available: $image")
                allAvailable = false
            }
        }
        
        return@withContext allAvailable
    }
    
    /**
     * Load a bitmap from the best available source
     */
    suspend fun loadImage(imageName: String): Bitmap? = withContext(Dispatchers.IO) {
        // Try loading from assets
        for (path in assetsPaths) {
            try {
                val fullPath = path + imageName
                val inputStream = context.assets.open(fullPath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                
                if (bitmap != null) {
                    Log.d(TAG, "Loaded image from assets: $fullPath")
                    return@withContext bitmap
                }
            } catch (e: Exception) {
                // Try next path
            }
        }
        
        // Try loading from cache
        try {
            val cacheFile = File(cacheDir, imageName)
            if (cacheFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath)
                if (bitmap != null) {
                    Log.d(TAG, "Loaded image from cache: ${cacheFile.absolutePath}")
                    return@withContext bitmap
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading from cache: ${e.message}")
        }
        
        // If all else fails, try to recover by copying to cache
        if (copyAssetsToCacheIfNeeded()) {
            try {
                val cacheFile = File(cacheDir, imageName)
                if (cacheFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(cacheFile.absolutePath)
                    if (bitmap != null) {
                        Log.d(TAG, "Loaded image from cache after recovery: ${cacheFile.absolutePath}")
                        return@withContext bitmap
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading from cache after recovery: ${e.message}")
            }
        }
        
        Log.e(TAG, "Failed to load image: $imageName")
        return@withContext null
    }
    
    /**
     * Copy assets to cache if needed
     */
    suspend fun copyAssetsToCacheIfNeeded(): Boolean = withContext(Dispatchers.IO) {
        var anySuccess = false
        
        for (imageName in expectedImages) {
            // Check if already in cache
            val cacheFile = File(cacheDir, imageName)
            if (cacheFile.exists() && cacheFile.length() > 0) {
                continue
            }
            
            // Try to copy from assets
            var copied = false
            for (path in assetsPaths) {
                if (copied) continue
                
                try {
                    val fullPath = path + imageName
                    val inputStream = context.assets.open(fullPath)
                    val bytes = inputStream.readBytes()
                    inputStream.close()
                    
                    if (bytes.isNotEmpty()) {
                        cacheFile.outputStream().use { it.write(bytes) }
                        copied = true
                        anySuccess = true
                        Log.d(TAG, "Copied $fullPath to cache")
                    }
                } catch (e: Exception) {
                    // Try next path
                }
            }
            
            if (!copied) {
                Log.w(TAG, "Failed to copy $imageName to cache")
            }
        }
        
        return@withContext anySuccess
    }
    
    /**
     * Get the cache directory containing Ishihara images
     */
    fun getImageCacheDir(): File = cacheDir
}
