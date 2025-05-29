package hr.ferit.frankoklepac.rma_project.model

import com.google.firebase.Timestamp

data class Game(
    val id: String = "",
    val yourChampion: String = "",
    val enemyChampion: String = "",
    val result: String = "",
    val kills: Int = 0,
    val assists: Int = 0,
    val deaths: Int = 0,
    val notes: String = "",
    val timestamp: Timestamp? = null,
)
