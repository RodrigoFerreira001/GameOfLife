import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.Modules
import org.koin.compose.KoinApplication

fun main() = application {

    val windowsState = rememberWindowState(
        size = DpSize(800.dp, 800.dp)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowsState,
        title = "GameOfLife",
    ) {
        KoinApplication(
            application = {
                modules(Modules.modules)
            }
        ) {
            App()
        }
    }
}