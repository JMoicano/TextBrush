package dev.jmoicano.textbrush.ui

data class TextBrushState(
    val words: List<List<Letter>> = listOf(),
    val isClicking: Boolean = false
)
