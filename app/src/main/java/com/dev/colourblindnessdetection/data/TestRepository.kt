package com.dev.colourblindnessdetection.data

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.dev.colourblindnessdetection.ColorBlindnessApplication
import com.dev.colourblindnessdetection.R
import com.dev.colourblindnessdetection.model.ColorBlindTestQuestion
import com.dev.colourblindnessdetection.model.ColorBlindnessType
import com.dev.colourblindnessdetection.model.TestResult
import com.dev.colourblindnessdetection.util.IshiharaAssetManager
import com.dev.colourblindnessdetection.util.RuntimeAssetCopy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Repository class that provides access to the test questions and analyzes test results.
 */
class TestRepository(private val context: Context) {
    
    private val TAG = "TestRepository"
    
    // Asset manager for handling Ishihara images
    private val assetManager = (context.applicationContext as? ColorBlindnessApplication)?.ishiharaAssetManager
        ?: IshiharaAssetManager(context)

    /**
     * Returns a list of test questions for the color blindness test.
     */
    fun getTestQuestions(): List<ColorBlindTestQuestion> {
        return listOf(
            ColorBlindTestQuestion(
                id = 1,
                imageResPath = "Screenshot 2025-04-21 210205.png", // Number 12
                questionText = "What number do you see in this image?",
                options = listOf("8", "Nothing", "12", "29"), // Jumbled
                correctAnswer = "12",
                protanopiaAnswer = "Nothing",
                deuteranopiaAnswer = "Nothing",
                tritanopiaAnswer = "12"
            ),
            ColorBlindTestQuestion(
                id = 2,
                imageResPath = "Screenshot 2025-04-21 210222.png", // Number 8
                questionText = "What number do you see in this image?",
                options = listOf("3", "8", "6", "Nothing"), // Jumbled
                correctAnswer = "8",
                protanopiaAnswer = "3",
                deuteranopiaAnswer = "3",
                tritanopiaAnswer = "8"
            ),
            ColorBlindTestQuestion(
                id = 3,
                imageResPath = "Screenshot 2025-04-21 210417.png", // Number 6
                questionText = "What number do you see in this image?",
                options = listOf("Nothing", "9", "6", "5"), // Jumbled
                correctAnswer = "6",
                protanopiaAnswer = "Nothing",
                deuteranopiaAnswer = "Nothing",
                tritanopiaAnswer = "6"
            ),
            ColorBlindTestQuestion(
                id = 4,
                imageResPath = "Screenshot 2025-04-21 210248.png", // Number 29
                questionText = "What number do you see in this image?",
                options = listOf("70", "29", "79", "Nothing"), // Jumbled
                correctAnswer = "29",
                protanopiaAnswer = "70",
                deuteranopiaAnswer = "70",
                tritanopiaAnswer = "29"
            ),
            ColorBlindTestQuestion(
                id = 5,
                imageResPath = "Screenshot 2025-04-21 210301.png", // Number 5
                questionText = "What number do you see in this image?",
                options = listOf("Nothing", "5", "2", "7"), // Jumbled
                correctAnswer = "5",
                protanopiaAnswer = "2",
                deuteranopiaAnswer = "2",
                tritanopiaAnswer = "5"
            ),
            ColorBlindTestQuestion(
                id = 6,
                imageResPath = "Screenshot 2025-04-21 210321.png", // Number 3
                questionText = "What number do you see in this image?",
                options = listOf("5", "9", "Nothing", "3"), // Jumbled
                correctAnswer = "3",
                protanopiaAnswer = "5",
                deuteranopiaAnswer = "5",
                tritanopiaAnswer = "Nothing"
            ),
            ColorBlindTestQuestion(
                id = 7,
                imageResPath = "Screenshot 2025-04-21 210341.png", // Number 15
                questionText = "What number do you see in this image?",
                options = listOf("17", "Nothing", "19", "15"), // Jumbled
                correctAnswer = "15",
                protanopiaAnswer = "17",
                deuteranopiaAnswer = "17",
                tritanopiaAnswer = "Nothing"
            ),
            ColorBlindTestQuestion(
                id = 8,
                imageResPath = "Screenshot 2025-04-21 210355.png", // Number 74
                questionText = "What number do you see in this image?",
                options = listOf("74", "Nothing", "21", "71"), // Jumbled
                correctAnswer = "74",
                protanopiaAnswer = "21",
                deuteranopiaAnswer = "21",
                tritanopiaAnswer = "Nothing"
            ),
            ColorBlindTestQuestion(
                id = 9,
                imageResPath = "Screenshot 2025-04-21 210429.png", // Number 45
                questionText = "What number do you see in this image?",
                options = listOf("35", "45", "Nothing", "54"), // Jumbled
                correctAnswer = "45",
                protanopiaAnswer = "Nothing",
                deuteranopiaAnswer = "Nothing",
                tritanopiaAnswer = "45"
            ),
            ColorBlindTestQuestion(
                id = 10,
                imageResPath = "Screenshot 2025-04-21 210443.png", // Number 16
                questionText = "What number do you see in this image?",
                options = listOf("Nothing", "5", "16", "26"), // Jumbled
                correctAnswer = "5",
                protanopiaAnswer = "Nothing",
                deuteranopiaAnswer = "Nothing",
                tritanopiaAnswer = "5"
            )
        )
    }

    /**
     * Analyzes the test results and returns a TestResult object.
     */
    fun analyzeResults(responses: List<Pair<ColorBlindTestQuestion, String>>): TestResult {
        var normalCount = 0
        var protanopiaCount = 0
        var deuteranopiaCount = 0
        var tritanopiaCount = 0

        responses.forEach { (question, answer) ->
            when (answer) {
                question.correctAnswer -> normalCount++
                question.protanopiaAnswer -> protanopiaCount++
                question.deuteranopiaAnswer -> deuteranopiaCount++
                question.tritanopiaAnswer -> tritanopiaCount++
            }
        }

        // Determine the most likely color blindness type
        val colorBlindnessType = when {
            normalCount >= maxOf(protanopiaCount, deuteranopiaCount, tritanopiaCount) -> ColorBlindnessType.NORMAL
            protanopiaCount > maxOf(normalCount, deuteranopiaCount, tritanopiaCount) -> ColorBlindnessType.PROTANOPIA
            deuteranopiaCount > maxOf(normalCount, protanopiaCount, tritanopiaCount) -> ColorBlindnessType.DEUTERANOPIA
            tritanopiaCount > maxOf(normalCount, protanopiaCount, deuteranopiaCount) -> ColorBlindnessType.TRITANOPIA
            else -> ColorBlindnessType.INCONCLUSIVE
        }

        return TestResult(
            normalCount = normalCount,
            protanopiaCount = protanopiaCount,
            deuteranopiaCount = deuteranopiaCount,
            tritanopiaCount = tritanopiaCount,
            colorBlindnessType = colorBlindnessType,
            responses = responses
        )
    }

    /**
     * Gets a drawable from the assets folder - uses the IshiharaAssetManager
     */
    suspend fun getImageBitmap(imageResPath: String) = assetManager.loadImage(imageResPath)
    
    /**
     * Force image recovery by copying assets to cache directory
     */
    suspend fun forceImageCopy(): Boolean = assetManager.copyAssetsToCacheIfNeeded()
}
