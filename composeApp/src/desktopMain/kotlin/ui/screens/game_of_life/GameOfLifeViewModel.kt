package ui.screens.game_of_life

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import base.ScreenViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ui.extensions.isAlive
import ui.extensions.like
import ui.extensions.updateAs
import ui.screens.game_of_life.GameOfLifeContracts.Effect
import ui.screens.game_of_life.GameOfLifeContracts.Event
import ui.screens.game_of_life.GameOfLifeContracts.State
import kotlin.time.Duration.Companion.seconds


class GameOfLifeViewModel(
    initialMatrixSize: Int
) : ScreenViewModel<State, Event, Effect>() {

    private val canvasToMatrixMap = mutableMapOf<IntRange, Int>()
    private var canvasDensity: Float = 0f
    val canvasSize = 600
    private var simulationJob: Job? = null
    private var cellColor: Color = Color.Black

    override val internalUIState = MutableStateFlow<State>(
        State.Displaying(
            matrixSize = initialMatrixSize,
            matrix = squareBooleanMatrix(initialMatrixSize),
            isRunningSimulation = false,
            numberOfCells = 0,
            iteration = 0
        )
    )

    override fun onEvent(event: Event) {
        when (event) {
            is Event.OnMatrixSizeChange -> onMatrixSizeChange(event.newMatrixSize)
            Event.OnMatrixSizeMinusClick -> onMatrixSizeMinusClick()
            Event.OnMatrixSizePlusClick -> onMatrixSizePlusClick()
            is Event.OnCellClicked -> onCellClicked(event.offset)
            is Event.OnDensityAcquired -> onDensityAcquired(event.density)
            Event.OnPlayPauseClick -> onPlayPauseClick()
            Event.OnResetMatrixClick -> onResetMatrixClick()
            is Event.OnColorChange -> onColorChange(event.color)
        }
    }

    private fun onColorChange(color: Color) {
        cellColor = color
    }

    private fun onResetMatrixClick() {
        simulationJob?.cancel()
        internalUIState.updateAs<State.Displaying> {
            copy(
                iteration = 0,
                numberOfCells = 0,
                isRunningSimulation = false,
                matrix = squareBooleanMatrix(matrixSize)
            )
        }
    }

    private fun onPlayPauseClick() {
        val isRunningSimulation = internalUIState.like<State.Displaying>().isRunningSimulation

        if (isRunningSimulation) {
            simulationJob?.cancel()
            internalUIState.updateAs<State.Displaying> {
                copy(
                    isRunningSimulation = false,
                )
            }
        } else {
            internalUIState.updateAs<State.Displaying> {
                copy(
                    isRunningSimulation = true,
                )
            }

            startSimulation()
        }
    }

    private fun startSimulation() {
        simulationJob = viewModelScope.launch {
            while (true) {
                delay(0.1.seconds)
                internalUIState.updateAs<State.Displaying> {

                    val targetMatrix = List(matrixSize) {
                        MutableList(matrixSize) { Color.White }
                    }

                    for (i in 0..<matrixSize) {
                        for (j in 0..<matrixSize) {
                            val isAlive = matrix[i][j].isAlive
                            val closestNeighbours = matrix.closestNeighbours(i, j).filter { it.isAlive }
                            val neighboursAlive = closestNeighbours.size

                            targetMatrix[i][j] = if (isAlive) {
                                if (neighboursAlive < 2) Color.White
                                else if (neighboursAlive > 3) Color.White
                                else matrix[i][j]
                            } else if (neighboursAlive == 3) closestNeighbours.blend()
                            else Color.White
                        }
                    }

                    copy(matrix = targetMatrix)
                }
            }
        }
    }


    private fun onDensityAcquired(density: Float) {
        canvasDensity = density
        updateCanvasToMatrixMap()
    }

    private fun updateCanvasToMatrixMap() {
        with(internalUIState.like<State.Displaying>()) {
            val cellSize = canvasSize * canvasDensity / matrixSize

            canvasToMatrixMap.apply {
                clear()
                (0..matrixSize).forEach { index ->
                    val start = (index * cellSize).toInt()
                    val end = start + cellSize.toInt()
                    put(start until end, index)
                }
            }
        }
    }

    private fun onCellClicked(
        offset: Offset
    ) {
        val i = canvasToMatrixMap.entries.firstOrNull {
            it.key.contains(offset.x.toInt())
        }?.value ?: 0

        val j = canvasToMatrixMap.entries.firstOrNull {
            it.key.contains(offset.y.toInt())
        }?.value ?: 0

        val state = internalUIState.like<State.Displaying>()

        val newMatrix = state.matrix.toMutableList().apply {
            this[i] = this[i].toMutableList().apply {
                this[j] = if (this[j].isAlive) Color.White else cellColor
            }
        }

        internalUIState.updateAs<State.Displaying> {
            copy(
                matrix = newMatrix
            )
        }
    }

    private fun onMatrixSizePlusClick() {
        internalUIState.updateAs<State.Displaying> {
            val newValue = matrixSize.plus(1)
            copy(
                matrixSize = newValue,
                matrix = squareBooleanMatrix(newValue)
            )
        }
    }

    private fun onMatrixSizeMinusClick() {
        internalUIState.updateAs<State.Displaying> {
            val newValue = matrixSize.minus(1).coerceAtLeast(100)
            copy(
                matrixSize = newValue,
                matrix = squareBooleanMatrix(newValue)
            )
        }
    }

    private fun onMatrixSizeChange(newMatrixSize: String) {

        val newValue = try {
            newMatrixSize.toInt()
        } catch (e: Throwable) {
            100
        }

        internalUIState.updateAs<State.Displaying> {
            copy(
                matrixSize = newValue,
                matrix = squareBooleanMatrix(newValue)
            )
        }
    }

    private fun List<List<Color>>.closestNeighbours(i: Int, j: Int): List<Color> {
        return if (i == 0 && j == 0) {
            listOf(
                this[i][j + 1],
                this[i + 1][j + 1],
                this[i + 1][j]
            )
        } else if (i == 0 && j == lastIndex) {
            listOf(
                this[i][j - 1],
                this[i + 1][j - 1],
                this[i + 1][j]
            )
        } else if (i == lastIndex && j == 0) {
            listOf(
                this[i][j + 1],
                this[i - 1][j + 1],
                this[i - 1][j]
            )
        } else if (i == lastIndex && j == lastIndex) {
            listOf(
                this[i][j - 1],
                this[i - 1][j - 1],
                this[i - 1][j]
            )
        } else if (i == 0) {
            listOf(
                this[i][j - 1],
                this[i][j + 1],
                this[i + 1][j],
                this[i + 1][j - 1],
                this[i + 1][j + 1]
            )
        } else if (i == lastIndex) {
            listOf(
                this[i][j - 1],
                this[i][j + 1],
                this[i - 1][j],
                this[i - 1][j - 1],
                this[i - 1][j + 1]
            )
        } else if (j == 0) {
            listOf(
                this[i - 1][j],
                this[i + 1][j],
                this[i][j + 1],
                this[i - 1][j + 1],
                this[i + 1][j + 1],
            )
        } else if (j == lastIndex) {
            listOf(
                this[i - 1][j],
                this[i + 1][j],
                this[i][j - 1],
                this[i - 1][j - 1],
                this[i + 1][j - 1],
            )
        } else {
            listOf(
                this[i][j + 1],
                this[i][j - 1],
                this[i - 1][j + 1],
                this[i - 1][j],
                this[i - 1][j - 1],
                this[i + 1][j + 1],
                this[i + 1][j],
                this[i + 1][j - 1],
            )
        }
    }

    private fun List<Color>.blend(): Color {
        val condensedColors = map { color ->
            Triple(color.red, color.green, color.blue)
        }
            .fold(Triple(0f, 0f, 0f)) { acc, triple ->
                Triple(
                    acc.first + triple.first,
                    acc.second + triple.second,
                    acc.third + triple.third
                )
            }

        return Color(
            red = condensedColors.first / size,
            green = condensedColors.second / size,
            blue = condensedColors.third / size
        )
    }

    private fun squareBooleanMatrix(size: Int) = List(size) {
        List(size) { Color.White }
    }
}