package hr.ferit.frankoklepac.rma_project

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import hr.ferit.frankoklepac.rma_project.ui.theme.Rma_projectTheme
import hr.ferit.frankoklepac.rma_project.view.*
import hr.ferit.frankoklepac.rma_project.workers.GameReminderReceiver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import kotlin.jvm.java

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            }
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        scheduleGameReminder()
        setContent {
            Rma_projectTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val auth = FirebaseAuth.getInstance()


                LaunchedEffect(Unit) {
                    intent.getStringExtra("navigateTo")?.let { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                }

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
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleGameReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, GameReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}