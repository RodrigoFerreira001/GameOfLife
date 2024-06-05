package ui.screens.game_of_life

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

object GameOfLifeContracts {

    sealed interface State {
        data class Displaying(
            val matrixSize: Int,
            val matrix: List<List<Color>>,
            val isRunningSimulation: Boolean,
            val iteration: Int,
            val numberOfCells: Int
        ) : State
    }

    sealed interface Event {
        data class OnMatrixSizeChange(val newMatrixSize: String) : Event
        data class OnCellClicked(val offset: Offset) : Event
        data class OnDensityAcquired(val density: Float) : Event
        data class OnColorChange(val color: Color) : Event
        data object OnMatrixSizePlusClick : Event
        data object OnMatrixSizeMinusClick : Event
        data object OnPlayPauseClick : Event
        data object OnResetMatrixClick : Event
    }

    sealed class Effect
}