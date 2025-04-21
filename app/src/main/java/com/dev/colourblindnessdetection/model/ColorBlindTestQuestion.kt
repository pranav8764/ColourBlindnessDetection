package com.dev.colourblindnessdetection.model

/**
 * Data class that represents a single color blindness test question.
 */
data class ColorBlindTestQuestion(
    val id: Int,
    val imageResPath: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val protanopiaAnswer: String,
    val deuteranopiaAnswer: String,
    val tritanopiaAnswer: String
)

/**
 * Enumeration for different types of color blindness.
 */
enum class ColorBlindnessType {
    NORMAL,
    PROTANOPIA,
    DEUTERANOPIA,
    TRITANOPIA,
    INCONCLUSIVE;

    fun getDescription(): String {
        return when (this) {
            NORMAL -> "Your color vision appears to be normal. You can differentiate between most colors and shades."
            PROTANOPIA -> "You may have Protanopia (red-green color blindness with deficiency in red perception). This affects about 1% of males. People with protanopia have difficulty distinguishing between reds, greens, and yellows."
            DEUTERANOPIA -> "You may have Deuteranopia (red-green color blindness with deficiency in green perception). This is the most common type, affecting about 6% of males. People with deuteranopia have difficulty distinguishing between reds, greens, and yellows."
            TRITANOPIA -> "You may have Tritanopia (blue-yellow color blindness). This is a rare form affecting less than 0.1% of the population. People with tritanopia have difficulty distinguishing between blues and yellows."
            INCONCLUSIVE -> "The test results are inconclusive. This could happen if your answers don't align clearly with a specific type of color blindness. Consider retaking the test or consulting an eye care professional."
        }
    }
}

/**
 * Data class that represents the result of the color blindness test.
 */
data class TestResult(
    val normalCount: Int,
    val protanopiaCount: Int,
    val deuteranopiaCount: Int,
    val tritanopiaCount: Int,
    val colorBlindnessType: ColorBlindnessType,
    val responses: List<Pair<ColorBlindTestQuestion, String>>
)
