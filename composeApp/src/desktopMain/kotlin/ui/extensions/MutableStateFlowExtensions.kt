package ui.extensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

inline fun <reified R> MutableStateFlow<*>.updateAs(
    block: R.() -> R
) {
    @Suppress("UNCHECKED_CAST")
    (this as? MutableStateFlow<R>)?.let {
        withNotNull(value as? R) {
            update { block() }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <R> MutableStateFlow<*>.like() = value as R