package dev.leonp.strata.draggable.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified

internal fun Color.takeOrElse(color: Color) = if (this.isSpecified) {
    this
} else {
    color
}