import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.animations.slideInFromLeft
import ui.animations.slideInFromRight
import ui.animations.slideOutToLeft
import ui.animations.slideOutToRight
import ui.routes.Routes
import ui.screens.game_of_life.GameOfLifeScreen
import ui.screens.home.HomeScreen

@Composable
@Preview
fun App() {

    val navController = rememberNavController()

    MaterialTheme {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Routes.Home(),
            enterTransition = { slideInFromRight() },
            popEnterTransition = { slideInFromLeft() },
            exitTransition = { slideOutToLeft() },
            popExitTransition = { slideOutToRight() }
        ) {
            composable(Routes.Home()) {
                HomeScreen(navController)
            }
            composable(
                route = Routes.GameOfLife(),
                arguments = listOf(
                    navArgument(Routes.GameOfLife.Args.matrixSize) {
                        type = NavType.IntType
                    }
                )
            ) { stack ->
                stack.arguments?.getInt(Routes.GameOfLife.Args.matrixSize)?.let { matrixSize ->
                    GameOfLifeScreen(
                        navController = navController,
                        matrixSize = matrixSize
                    )
                }
            }
        }
    }
}