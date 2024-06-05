package ui.extensions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
inline fun <reified T, R> withNotNull(receiver: T?, scopeBlock: T.() -> R): R? {
    contract {
        callsInPlace(scopeBlock, InvocationKind.EXACTLY_ONCE)
        returnsNotNull() implies (receiver is T)
    }
    return receiver?.scopeBlock()
}