package dev.jmoicano.textbrush.ui

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.Math.toDegrees
import javax.inject.Inject
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

@HiltViewModel
class TextBrushViewModel @Inject constructor() : ViewModel() {
    private val _uiState =
        MutableStateFlow(TextBrushState())
    val uiState: StateFlow<TextBrushState> = _uiState.asStateFlow()

    private var currentWord: String = "TEXT BRUSH"
    private var currentLetter: Int = 0
    private var distanceToNextLetter = Int.MAX_VALUE

    private val _currentWordField = MutableStateFlow(currentWord)
    val currentWordField = _currentWordField.asStateFlow()

    private lateinit var lastLetter: Letter

    fun handleActionDown(x: Float, y: Float) {
        val currentWords = _uiState.value.words.toMutableList()
        currentWords.add(listOf())
        currentLetter = 0

        val letter =
            Letter(letter = currentWord[currentLetter++], Offset(x, y), 0F)
        _uiState.update { currentState ->
            currentState.copy(
                words = currentWords.also {
                    currentWords[currentWords.lastIndex] =
                        currentWords.last().toMutableList().also { it.add(letter) }
                },
                isClicking = true,
            )
        }
        lastLetter = letter
    }

    fun handleActionMove(x: Float, y: Float) {
        val angle = calculateRotationAngle(x, y)
        val currentWords = _uiState.value.words.toMutableList()
        val distanceToLast = calculateDistance(x, y)
        _uiState.update { currentState ->
            currentState.copy(
                words = currentWords.also {
                    it[currentWords.lastIndex] = it.last().toMutableList().apply {
                        set(
                            lastIndex,
                            last().copy(angle = angle))
                        if (distanceToLast > distanceToNextLetter*2 && currentLetter < currentWord.length) {
                            val letter = Letter(
                                letter = currentWord[currentLetter++],
                                offset = Offset(x, y),
                                angle = angle
                            )
                            add(letter)
                            lastLetter = letter
                        }
                    }
                }
            )
        }
    }

    fun deleteLastDraw() {
        val currentWords = _uiState.value.words.toMutableList()
        _uiState.update {currentState ->
            currentState.copy(
                words = currentWords.apply { if (isNotEmpty()) removeLast() }
            )
        }
    }

    fun setDistanceToNextLetter(distance: Int) {
        distanceToNextLetter = distance
    }

    fun setCurrentWordField(word: String) {
        _currentWordField.update { word }
    }

    fun resetCurrentWordField() {
        _currentWordField.update { currentWord }
    }

    fun updateCurrentWord(word: String) {
        currentWord = word
        setCurrentWordField(word)
    }

    private fun calculateRotationAngle(x: Float, y: Float): Float {
        val correction = if (x < lastLetter.offset.x) -180 else 0
        return toDegrees(atan((y - lastLetter.offset.y) / (x - lastLetter.offset.x).toDouble())).toFloat() + correction
    }

    private fun calculateDistance(x: Float, y: Float): Float {
        return sqrt((x - lastLetter.offset.x).pow(2) + (y - lastLetter.offset.y).pow(2))
    }
}