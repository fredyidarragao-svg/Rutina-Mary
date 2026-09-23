package com.mary.rutina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mary.rutina.ui.DayDetailScreen
import com.mary.rutina.ui.HistoryScreen
import com.mary.rutina.ui.LibraryScreen
import com.mary.rutina.ui.SettingsScreen
import com.mary.rutina.ui.WeekScreen
import com.mary.rutina.ui.theme.RutinaMaryTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RutinaMaryTheme {
                RutinaMaryApp(viewModel)
            }
        }
    }
}

@Composable
fun RutinaMaryApp(viewModel: AppViewModel) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "semana") {
        composable("semana") {
            WeekScreen(
                viewModel = viewModel,
                onDayClick = { dayId -> navController.navigate("dia/$dayId") },
                onLibraryClick = { navController.navigate("biblioteca") },
                onHistoryClick = { navController.navigate("historial") },
                onSettingsClick = { navController.navigate("ajustes") }
            )
        }
        composable("ajustes") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("historial") {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("dia/{dayId}") { backStackEntry ->
            val dayId = backStackEntry.arguments?.getString("dayId") ?: return@composable
            DayDetailScreen(
                viewModel = viewModel,
                dayId = dayId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("biblioteca") {
            LibraryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
