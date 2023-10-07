package dev.jmoicano.textbrush.ui

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.asFloatState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import dev.jmoicano.textbrush.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BrushComponent(viewModel: TextBrushViewModel) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val words by viewModel.uiState.collectAsState()

    val currentWord by viewModel.currentWordField.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ChangeWordDialog(
            word = currentWord,
            onWordChanged = viewModel::setCurrentWordField,
            onConfirmButton = viewModel::updateCurrentWord,
            onDismissButton = viewModel::resetCurrentWordField,
            setShowDialog = { showDialog = false })
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged {
            size = it
        }
    ) {

        AsyncImage(
            modifier =
            Modifier
                .clip(RoundedCornerShape(40.dp))
                .pointerInteropFilter { event ->
                    return@pointerInteropFilter when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            viewModel.handleActionDown(event.x, event.y)
                            true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            viewModel.handleActionMove(event.x, event.y)
                            true
                        }

                        else -> false
                    }
                },
            model = stringResource(R.string.picsum_adress, size.width, size.height),
            contentDescription = stringResource(R.string.background)
        )

        words.words.forEach { word ->
            word.forEach { letter ->
                LetterExhibition(letter, viewModel)
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp),
            onClick = { showDialog = true }) {
            Icon(
                imageVector = Icons.Outlined.Create,
                contentDescription = stringResource(R.string.edit_word)
            )
        }

        IconButton(modifier = Modifier
            .align(Alignment.TopStart)
            .padding(20.dp),
            onClick = { viewModel.deleteLastDraw() }) {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = stringResource(R.string.remove_last_word)
            )
        }

    }
}

@Composable
private fun ChangeWordDialog(
    word: String,
    onWordChanged: (String) -> Unit,
    onConfirmButton: (String) -> Unit = {},
    onDismissButton: () -> Unit = {},
    setShowDialog: (Boolean) -> Unit = {}
) {
    Dialog(
        onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier
                .padding(20.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(value = word, onValueChange = onWordChanged)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        onConfirmButton(word)
                        setShowDialog(false)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = stringResource(R.string.confirm_change_word)
                        )
                    }
                    IconButton(onClick = {
                        onDismissButton()
                        setShowDialog(false)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cancel_change_word)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LetterExhibition(
    letter: Letter,
    viewModel: TextBrushViewModel
) {
    val textSize = extractTextSize(letter)
    viewModel.setDistanceToNextLetter(textSize.width)
    val angle = animateFloatAsState(targetValue = letter.angle).asFloatState()
    Text(
        modifier = Modifier
            .offset(
                x = (letter.offset.x - textSize.width / 2).convertToDp(LocalDensity.current),
                y = (letter.offset.y - textSize.height / 2).convertToDp(LocalDensity.current)
            )
            .rotate(angle.value),
        text = "${letter.letter}",
        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold)
    )
}

@Composable
private fun extractTextSize(
    letter: Letter
): IntSize {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult =
        textMeasurer.measure(
            text = AnnotatedString("${letter.letter}"),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold)
        )
    return textLayoutResult.size
}

fun Float.convertToDp(density: Density): Dp {
    return density.run {
        this@convertToDp
            .toInt()
            .toDp()
    }
}