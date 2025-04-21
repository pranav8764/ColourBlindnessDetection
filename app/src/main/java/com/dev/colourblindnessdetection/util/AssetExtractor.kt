package com.dev.colourblindnessdetection.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility to extract assets from APK and store them externally
 * for easier access and debugging
 */
class AssetExtractor(private val context: Context) {
    
    private val TAG = "AssetExtractor"
    
    /**
     * Extract all Ishihara test images from assets to external files directory
     * This is useful for apps that want to allow users to use their own images
     */
    fun extractIshiharaImages(): Boolean {
        try {
            Log.d(TAG, "Starting to extract Ishihara images to external files directory")
            
            // Expected image files
            val expectedImageNames = listOf(
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
            
            // Possible paths to search for images in assets
            val possibleSourcePaths = listOf(
                "", // Root directory
                "images/", // images subdirectory
                "assests/images/" // custom assests path
            )
            
            // Create the destination directory in external files directory
            val destDir = File(context.getExternalFilesDir(null), "ishihara_images")
            if (!destDir.exists()) {
                destDir.mkdirs()
            }
            
            // Copy files
            var successCount = 0
            
            // For each image, try different source paths
            for (imageName in expectedImageNames) {
                var imageCopied = false
                
                // Try each possible source path until we find the image
                for (sourcePath in possibleSourcePaths) {
                    if (imageCopied) continue
                    
                    val fullSourcePath = sourcePath + imageName
                    
                    try {
                        // Open the source asset
                        val inputStream = context.assets.open(fullSourcePath)
                        
                        // Read all bytes from input
                        val bytes = inputStream.readBytes()
                        inputStream.close()
                        
                        // Create the destination file
                        val destFile = File(destDir, imageName)
                        
                        // Write to the destination file
                        val outputStream = FileOutputStream(destFile)
                        outputStream.write(bytes)
                        outputStream.flush()
                        outputStream.close()
                        
                        imageCopied = true
                        successCount++
                        Log.d(TAG, "Successfully extracted $fullSourcePath to ${destFile.absolutePath}")
                    } catch (e: IOException) {
                        Log.d(TAG, "Failed to extract from $fullSourcePath: ${e.message}")
                        // Continue to next path
                    }
                }
                
                if (!imageCopied) {
                    Log.w(TAG, "Could not extract $imageName from any path")
                }
            }
            
            Log.d(TAG, "Extraction complete: $successCount of ${expectedImageNames.size} images extracted")
            return successCount > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error during asset extraction", e)
            return false
        }
    }
    
    /**
     * Create a simple HTML report of extracted images for viewing
     */
    fun createImageReport(): File? {
        try {
            val imagesDir = File(context.getExternalFilesDir(null), "ishihara_images")
            if (!imagesDir.exists() || !imagesDir.isDirectory) {
                return null
            }
            
            val reportFile = File(context.getExternalFilesDir(null), "ishihara_report.html")
            
            FileOutputStream(reportFile).use { fos ->
                val html = StringBuilder()
                html.append("<html><head><title>Ishihara Test Images</title>")
                html.append("<style>body{font-family:sans-serif;max-width:800px;margin:0 auto;padding:20px;}")
                html.append("h1{color:#2196F3;} img{max-width:300px;margin:10px;border:1px solid #ccc;}</style>")
                html.append("</head><body>")
                html.append("<h1>Ishihara Test Plates</h1>")
                html.append("<p>These are the test plates used in the application.</p>")
                
                val imageFiles = imagesDir.listFiles()?.filter { it.isFile && it.name.endsWith(".png") }
                
                if (imageFiles.isNullOrEmpty()) {
                    html.append("<p>No images found.</p>")
                } else {
                    html.append("<div style='display:flex;flex-wrap:wrap;'>")
                    
                    imageFiles.sortedBy { it.name }.forEach { file ->
                        html.append("<div style='margin:10px;text-align:center;'>")
                        html.append("<img src='ishihara_images/${file.name}' alt='${file.name}' />")
                        html.append("<p>${file.name}</p>")
                        html.append("</div>")
                    }
                    
                    html.append("</div>")
                }
                
                html.append("</body></html>")
                
                fos.write(html.toString().toByteArray())
            }
            
            return reportFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create image report", e)
            return null
        }
    }
}
