package com.dev.colourblindnessdetection.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException

/**
 * Helper class for loading assets
 */
class AssetHelper(private val context: Context) {

    /**
     * List all asset files in a directory
     */
    fun listAssetFiles(path: String): List<String> {
        return try {
            context.assets.list(path)?.toList() ?: emptyList()
        } catch (e: IOException) {
            Log.e("AssetHelper", "Failed to list assets at path: $path", e)
            emptyList()
        }
    }

    /**
     * Load bitmap from assets
     */
    fun loadBitmapFromAssets(path: String): Bitmap? {
        return try {
            context.assets.open(path).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            Log.e("AssetHelper", "Failed to load bitmap from: $path", e)
            null
        }
    }

    /**
     * Check if a file exists in assets
     */
    fun assetExists(path: String): Boolean {
        return try {
            context.assets.open(path).use { true }
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Verify all image paths
     */
    fun verifyImagePaths(paths: List<String>): Map<String, Boolean> {
        return paths.associateWith { assetExists(it) }
    }
}
