package dev.jmoicano.textbrush.ui

import androidx.compose.ui.geometry.Offset

data class Letter(
    val letter: Char,
    var offset: Offset,
    var angle: Float
)
