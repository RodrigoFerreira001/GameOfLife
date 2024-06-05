package ui.routes

object Routes {
    object Home {
        operator fun invoke(): String = "home"
    }

    object GameOfLife {
        operator fun invoke(): String = "gameOfLife/{matrixSize}"

        fun navigate(
            matrixSize: Int
        ) = this().replace("{${Args.matrixSize}}", matrixSize.toString())

        object Args {
            val matrixSize = "matrixSize"
        }
    }
}