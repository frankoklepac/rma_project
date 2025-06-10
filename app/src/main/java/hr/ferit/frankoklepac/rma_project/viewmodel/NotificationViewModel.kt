package hr.ferit.frankoklepac.rma_project.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hr.ferit.frankoklepac.rma_project.MainActivity
import hr.ferit.frankoklepac.rma_project.R
import hr.ferit.frankoklepac.rma_project.model.Game
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun checkGameNotification() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val today = LocalDate.now()
                val startOfDay = java.util.Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant())
                val endOfDay = java.util.Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant())

                val snapshot = db.collection("users").document(userId).collection("games")
                    .whereGreaterThanOrEqualTo("timestamp", com.google.firebase.Timestamp(startOfDay))
                    .whereLessThan("timestamp", com.google.firebase.Timestamp(endOfDay))
                    .get()
                    .await()

                if (snapshot.isEmpty) {
                    sendGameReminderNotification()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun sendGameReminderNotification() {
        val context = getApplication<Application>().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "game_reminder_channel"

        val channel = NotificationChannel(
            channelId,
            "Game Reminder Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifies user to add a game if none added by 5:00 PM"
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigateTo", if (auth.currentUser != null) "mainmenu" else "start")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Time to Play LoL!")
            .setContentText("You haven't logged a game today. Jump into a match and record it!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }
}