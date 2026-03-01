package dev.danielk.cluein

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val HOME = "home"
    const val MARKING = "marking"
    const val LOADING = "loading"
    const val RESULT = "result"
    const val SOURCES = "sources"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history_detail/{historyId}"
    fun historyDetail(id: String) = "history_detail/$id"
}

@Composable
fun ClueinNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.MARKING) {
            MarkingScreen(navController)
        }
        composable(Routes.LOADING) {
            LoadingScreen(navController)
        }
        composable(Routes.RESULT) {
            ResultScreen(navController)
        }
        composable(Routes.SOURCES) {
            SourcesScreen(navController)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController)
        }
        composable(Routes.HISTORY_DETAIL) { backStackEntry ->
            val historyId = backStackEntry.arguments?.getString("historyId") ?: ""
            HistoryDetailScreen(navController, historyId)
        }
    }
}
