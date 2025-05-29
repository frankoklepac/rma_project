package hr.ferit.frankoklepac.rma_project

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import hr.ferit.frankoklepac.rma_project.ui.theme.Rma_projectTheme
import hr.ferit.frankoklepac.rma_project.utils.NotificationScheduler
import hr.ferit.frankoklepac.rma_project.utils.NotificationUtils
import hr.ferit.frankoklepac.rma_project.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    NotificationUtils.createNotificationChannel(this)
                    if (isGranted) {
                        NotificationScheduler.scheduleDailyReminder(this)
                    }
                    prefs.edit().putBoolean("isFirstLaunch", false).apply()
                }.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationUtils.createNotificationChannel(this)
                NotificationScheduler.scheduleDailyReminder(this)
                prefs.edit().putBoolean("isFirstLaunch", false).apply()
            }
        }

        setContent {
            Rma_projectTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val auth = FirebaseAuth.getInstance()

                val startDestination = remember {
                    val userId = runBlocking { DataStoreManager.getUserId(context).first() }
                    if (auth.currentUser != null && userId.isNotEmpty()) {
                        "mainmenu"
                    } else {
                        "start"
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("start") {
                        StartScreen(
                            onLoginClick = { navController.navigate("login") },
                            onRegisterClick = { navController.navigate("register") }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            onBackClick = { navController.popBackStack() },
                            onLoginClick = {  }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            navController = navController,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("mainmenu") {
                        MainMenuScreen(
                            navController = navController,
                            onBackClick = {
                                auth.signOut()
                                runBlocking { DataStoreManager.clearUserId(context) }
                                navController.navigate("start") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable("newgame") {
                        NewGameScreen(
                            navController = navController,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("matchHistory") {
                        MatchHistoryScreen(
                            navController = navController,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("gameDetails/{gameId}") { backStackEntry ->
                        Text(
                            text = "Game Details for ID: ${backStackEntry.arguments?.getString("gameId")}",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                    composable("gameDetails/{gameId}") { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                        MatchDetailsScreen(
                            navController = navController,
                            gameId = gameId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("editGame/{gameId}") { backStackEntry ->
                        val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                        EditGameScreen(
                            navController = navController,
                            gameId = gameId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}