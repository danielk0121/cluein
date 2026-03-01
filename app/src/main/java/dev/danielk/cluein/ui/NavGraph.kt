package dev.danielk.cluein.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.danielk.cluein.data.ApiKeyManager

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
    val gugeoViewModel: GugeoViewModel = viewModel(
        factory = GugeoViewModelFactory(context)
    )

    NavHost(navController = navController, startDestination = startDest) {
        composable(Routes.API_KEY_SETUP) {
            ApiKeySetupScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController, gugeoViewModel)
        }
        composable(Routes.MARKING) {
            MarkingScreen(navController, gugeoViewModel)
        }
        composable(Routes.LOADING) {
            LoadingScreen(navController, gugeoViewModel)
        }
        composable(Routes.RESULT) {
            ResultScreen(navController, gugeoViewModel)
        }
        composable(Routes.SOURCES) {
            SourcesScreen(navController, gugeoViewModel)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController, gugeoViewModel)
        }
        composable(Routes.HISTORY_DETAIL) { backStackEntry ->
            val historyId = backStackEntry.arguments?.getString("historyId") ?: ""
            HistoryDetailScreen(navController, historyId, gugeoViewModel)
        }
    }
}
