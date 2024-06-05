package base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ScreenViewModel <State, Event, Effect>: ViewModel() {

    protected abstract val internalUIState: MutableStateFlow<State>
    val uiState get() = internalUIState.asStateFlow()

    protected val internalEffects = MutableSharedFlow<Effect>()
    val effects get() = internalEffects.asSharedFlow()

    abstract fun onEvent(event: Event)
}