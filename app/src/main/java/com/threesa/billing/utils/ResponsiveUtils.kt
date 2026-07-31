package com.threesa.billing.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.min

/**
 * Calculates a responsive text size based on the screen's width and height ratio.
 * This ensures that on smaller devices (both short and narrow), the text scales down automatically.
 */
@Composable
fun responsiveSp(baseSize: Float): TextUnit {
    val configuration = LocalConfiguration.current
    
    // Baseline dimensions for a standard phone (e.g., Pixel 4/5)
    val baselineWidth = 390f
    val baselineHeight = 844f
    
    val currentWidth = configuration.screenWidthDp.toFloat()
    val currentHeight = configuration.screenHeightDp.toFloat()
    
    // Calculate ratio based on both width and height to account for unusually short or narrow devices
    val widthRatio = currentWidth / baselineWidth
    val heightRatio = currentHeight / baselineHeight
    
    // Use the smaller ratio to ensure text fits on the most constrained dimension
    val ratio = min(widthRatio, heightRatio)
    
    // Constrain the scale factor to prevent text from becoming too unreadable or too massive
    val scaleFactor = ratio.coerceIn(0.75f, 1.25f)
    
    return (baseSize * scaleFactor).sp
}

/**
 * Extension property to easily use responsive text sizing.
 * Usage: 16.rsp
 */
val Int.rsp: TextUnit
    @Composable
    get() = responsiveSp(this.toFloat())

/**
 * Extension property for Float responsive text sizing.
 * Usage: 16.5f.rsp
 */
val Float.rsp: TextUnit
    @Composable
    get() = responsiveSp(this)
