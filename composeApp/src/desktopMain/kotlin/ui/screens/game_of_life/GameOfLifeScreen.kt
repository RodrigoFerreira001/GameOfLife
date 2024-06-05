package ui.screens.game_of_life

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eygraber.compose.colorpicker.ColorPicker
import com.eygraber.compose.colorpicker.rememberColorPickerState
import ui.screens.game_of_life.GameOfLifeContracts.Event
import ui.screens.game_of_life.GameOfLifeContracts.State

@Composable
fun GameOfLifeScreen(
    navController: NavController,
    matrixSize: Int
) {

    val stateHolder: GameOfLifeViewModel = viewModel {
        GameOfLifeViewModel(matrixSize)
    }

    val uiState = stateHolder.uiState.collectAsState().value
    val localDensity = LocalDensity.current

    LaunchedEffect(Unit) {
        stateHolder.onEvent(Event.OnDensityAcquired(localDensity.density))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Game of life") },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                navController.navigateUp()
                            }.clip(CircleShape),
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Go back button"
                    )
                },
            )
        },
    ) {

        when (uiState) {
            is State.Displaying -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(stateHolder.canvasSize.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, _ ->
                                        stateHolder.onEvent(
                                            Event.OnCellClicked(offset = change.position)
                                        )
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        stateHolder.onEvent(
                                            Event.OnCellClicked(
                                                offset = it
                                            )
                                        )
                                    }
                                )
                            }
                    ) {

                        val cellSize = (size.width / uiState.matrixSize)

                        // Draw h-lines
                        for (i in 0..uiState.matrixSize) {
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, i * cellSize),
                                end = Offset(size.width, i * cellSize),
                                strokeWidth = 1f
                            )
                        }

                        // Draw v-lines
                        for (i in 0..uiState.matrixSize) {
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(i * cellSize, 0f),
                                end = Offset(i * cellSize, size.height),
                                strokeWidth = 1f
                            )
                        }

                        for (i in 0 until uiState.matrixSize) {
                            for (j in 0 until uiState.matrixSize) {
                                drawRect(
                                    color = uiState.matrix[i][j],
                                    topLeft = Offset(i * cellSize + 1f, j * cellSize + 1f),
                                    size = Size(cellSize - 1f, cellSize - 1f)
                                )
                            }
                        }

                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        IconButton(
                            onClick = {
                                stateHolder.onEvent(Event.OnPlayPauseClick)
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.isRunningSimulation) Icons.Rounded.StopCircle else Icons.Rounded.PlayCircle,
                                contentDescription = "Play/Stop simulation"
                            )
                        }

                        ColorPicker(
                            modifier = Modifier.size(48.dp),
                            state = rememberColorPickerState(magnifier = ColorPicker.Magnifier.None),
                            onColorSelected = {
                                stateHolder.onEvent(Event.OnColorChange(it))
                            }
                        )

                        IconButton(
                            onClick = {
                                stateHolder.onEvent(Event.OnResetMatrixClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Reset matrix"
                            )
                        }

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = uiState.matrixSize.toString(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            onValueChange = {
                                stateHolder.onEvent(Event.OnMatrixSizeChange(it))
                            }
                        )

                        IconButton(
                            onClick = {
                                stateHolder.onEvent(Event.OnMatrixSizeMinusClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Remove,
                                contentDescription = "Decrease matrix size"
                            )
                        }

                        IconButton(
                            onClick = {
                                stateHolder.onEvent(Event.OnMatrixSizePlusClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Increase matrix size"
                            )
                        }
                    }
                }
            }
        }
    }
}