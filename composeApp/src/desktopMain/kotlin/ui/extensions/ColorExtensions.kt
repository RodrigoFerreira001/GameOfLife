package ui.extensions

import androidx.compose.ui.graphics.Color

val Color.isAlive get() = (red + green + blue) != 3f