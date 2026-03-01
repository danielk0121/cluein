package dev.danielk.cluein

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes {
    const val API_KEY_SETUP = "api_key_setup"
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
    val context = LocalContext.current
    val startDest = if (ApiKeyManager.hasApiKey(context)) Routes.HOME else Routes.API_KEY_SETUP

    NavHost(navController = navController, startDestination = startDest) {
        composable(Routes.API_KEY_SETUP) {
            ApiKeySetupScreen(navController)
        }
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
