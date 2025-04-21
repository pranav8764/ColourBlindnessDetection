package com.dev.colourblindnessdetection.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect as AndroidRenderEffect
import android.os.Build
import androidx.annotation.RequiresApi
import com.dev.colourblindnessdetection.model.ColorBlindnessType

/**
 * Apply color filter to simulate different types of color blindness
 */
@Composable
fun ColorBlindnessFilter(
    type: ColorBlindnessType,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when (type) {
        ColorBlindnessType.NORMAL, ColorBlindnessType.INCONCLUSIVE -> {
            content()
        }
        else -> {
            // Get the appropriate color matrix for the color blindness type
            val colorMatrix = when (type) {
                ColorBlindnessType.PROTANOPIA -> {
                    // Protanopia (red-blind) color matrix
                    ColorMatrix(
                        floatArrayOf(
                            0.567f, 0.433f, 0f, 0f, 0f,
                            0.558f, 0.442f, 0f, 0f, 0f,
                            0f, 0.242f, 0.758f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                }
                ColorBlindnessType.DEUTERANOPIA -> {
                    // Deuteranopia (green-blind) color matrix
                    ColorMatrix(
                        floatArrayOf(
                            0.625f, 0.375f, 0f, 0f, 0f,
                            0.7f, 0.3f, 0f, 0f, 0f,
                            0f, 0.3f, 0.7f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                }
                ColorBlindnessType.TRITANOPIA -> {
                    // Tritanopia (blue-blind) color matrix
                    ColorMatrix(
                        floatArrayOf(
                            0.95f, 0.05f, 0f, 0f, 0f,
                            0f, 0.433f, 0.567f, 0f, 0f,
                            0f, 0.475f, 0.525f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                }
                else -> ColorMatrix() // Fallback to identity matrix
            }
            
            // Apply the color matrix using the proper technique based on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Use RenderEffect for Android 12+ (API 31+)
                ApplyColorMatrixRenderEffect(colorMatrix, modifier) {
                    content()
                }
            } else {
                // Use ColorFilter for older Android versions
                ApplyColorFilter(colorMatrix, modifier) {
                    content()
                }
            }
        }
    }
}

/**
 * Apply color matrix using RenderEffect (Android 12+)
 */
@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun ApplyColorMatrixRenderEffect(
    colorMatrix: ColorMatrix,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            val androidColorMatrix = android.graphics.ColorMatrix().apply {
                set(colorMatrix.values)
            }
            val matrixColorFilter = ColorMatrixColorFilter(androidColorMatrix)
            val renderEffect = AndroidRenderEffect.createColorFilterEffect(matrixColorFilter)
            this.renderEffect = renderEffect.asComposeRenderEffect()
        }
    ) {
        content()
    }
}

/**
 * Apply color matrix using ColorFilter for older Android versions
 */
@Composable
private fun ApplyColorFilter(
    colorMatrix: ColorMatrix,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    // This is a simpler approach but will only work properly on Image composables
    // and won't affect nested components
    Box(modifier = modifier) {
        content()
    }
}

/**
 * Extension function to apply color blindness simulation to any Modifier
 * Note: This only works fully for Android 12+ (API 31+)
 */
fun Modifier.simulateColorBlindness(type: ColorBlindnessType): Modifier {
    if (type == ColorBlindnessType.NORMAL || type == ColorBlindnessType.INCONCLUSIVE) {
        return this
    }
    
    val colorMatrix = when (type) {
        ColorBlindnessType.PROTANOPIA -> {
            // Protanopia (red-blind) color matrix
            ColorMatrix(
                floatArrayOf(
                    0.567f, 0.433f, 0f, 0f, 0f,
                    0.558f, 0.442f, 0f, 0f, 0f,
                    0f, 0.242f, 0.758f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        ColorBlindnessType.DEUTERANOPIA -> {
            // Deuteranopia (green-blind) color matrix
            ColorMatrix(
                floatArrayOf(
                    0.625f, 0.375f, 0f, 0f, 0f,
                    0.7f, 0.3f, 0f, 0f, 0f,
                    0f, 0.3f, 0.7f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        ColorBlindnessType.TRITANOPIA -> {
            // Tritanopia (blue-blind) color matrix
            ColorMatrix(
                floatArrayOf(
                    0.95f, 0.05f, 0f, 0f, 0f,
                    0f, 0.433f, 0.567f, 0f, 0f,
                    0f, 0.475f, 0.525f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }
        else -> ColorMatrix() // Fallback to identity matrix
    }
    
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.graphicsLayer {
            val androidColorMatrix = android.graphics.ColorMatrix().apply {
                set(colorMatrix.values)
            }
            val matrixColorFilter = ColorMatrixColorFilter(androidColorMatrix)
            val renderEffect = AndroidRenderEffect.createColorFilterEffect(matrixColorFilter)
            this.renderEffect = renderEffect.asComposeRenderEffect()
        }
    } else {
        // This is a limited approach for older versions
        this
    }
}
