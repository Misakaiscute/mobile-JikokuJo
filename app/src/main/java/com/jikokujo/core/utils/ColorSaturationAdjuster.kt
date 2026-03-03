package com.jikokujo.core.utils

import androidx.compose.ui.graphics.Color

@Throws(IllegalArgumentException::class)
fun Color.lighten(by: Float): Color {
    require(by in 0f..1f) { "Lightening must be a value between 0 and 1 (was $by)" }
    return this.copy(
        red = red + by,
        green = green + by,
        blue = blue + by
    )
}
@Throws(IllegalArgumentException::class)
fun Color.darken(by: Float): Color {
    require(by in 0f..1f) { "Darkening must be a value between 0 and 1 (was $by)" }
    return this.copy(
        red = red - by,
        green = green - by,
        blue = blue - by
    )
}