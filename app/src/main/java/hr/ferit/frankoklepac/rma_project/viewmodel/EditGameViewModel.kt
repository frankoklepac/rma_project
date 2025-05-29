package hr.ferit.frankoklepac.rma_project.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import hr.ferit.frankoklepac.rma_project.model.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EditGameViewModel : ViewModel() {
    private val _game = mutableStateOf<Game?>(null)
    val game: State<Game?> = _game

    private val _yourChampion = mutableStateOf("")
    val yourChampion: State<String> = _yourChampion

    private val _enemyChampion = mutableStateOf("")
    val enemyChampion: State<String> = _enemyChampion

    private val _result = mutableStateOf("")
    val result: State<String> = _result

    private val _kills = mutableStateOf("")
    val kills: State<String> = _kills

    private val _assists = mutableStateOf("")
    val assists: State<String> = _assists

    private val _deaths = mutableStateOf("")
    val deaths: State<String> = _deaths

    private val _notes = mutableStateOf("")
    val notes: State<String> = _notes

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun loadGame(context: Context, gameId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    _errorMessage.value = "User not logged in"
                    _isLoading.value = false
                    return@launch
                }

                val storedUserId = DataStoreManager.getUserId(context).first()
                if (storedUserId != userId) {
                    _errorMessage.value = "Session mismatch. Please log in again."
                    _isLoading.value = false
                    return@launch
                }

                val document = db.collection("users").document(userId)
                    .collection("games").document(gameId)
                    .get()
                    .await()

                val game = document.toObject(Game::class.java)
                if (game != null) {
                    _game.value = game
                    _yourChampion.value = game.yourChampion
                    _enemyChampion.value = game.enemyChampion
                    _result.value = game.result
                    _kills.value = game.kills.toString()
                    _assists.value = game.assists.toString()
                    _deaths.value = game.deaths.toString()
                    _notes.value = game.notes
                } else {
                    _errorMessage.value = "Game not found"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load game: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun onYourChampionChange(value: String) {
        _yourChampion.value = value
    }

    fun onEnemyChampionChange(value: String) {
        _enemyChampion.value = value
    }

    fun onResultChange(value: String) {
        _result.value = value
    }

    fun onKillsChange(value: String) {
        _kills.value = value
    }

    fun onAssistsChange(value: String) {
        _assists.value = value
    }

    fun onDeathsChange(value: String) {
        _deaths.value = value
    }

    fun onNotesChange(value: String) {
        _notes.value = value
    }

    fun saveGame(context: Context, gameId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: run {
                    _errorMessage.value = "User not logged in"
                    return@launch
                }

                val storedUserId = DataStoreManager.getUserId(context).first()
                if (storedUserId != userId) {
                    _errorMessage.value = "Session mismatch. Please log in again."
                    return@launch
                }

                // Validate inputs
                if (_yourChampion.value.isBlank() || _enemyChampion.value.isBlank()) {
                    _errorMessage.value = "Please enter both champions"
                    return@launch
                }
                if (_result.value.isBlank()) {
                    _errorMessage.value = "Please select a result"
                    return@launch
                }
                val kills = _kills.value.toIntOrNull() ?: run {
                    _errorMessage.value = "Please enter valid kills"
                    return@launch
                }
                val assists = _assists.value.toIntOrNull() ?: run {
                    _errorMessage.value = "Please enter valid assists"
                    return@launch
                }
                val deaths = _deaths.value.toIntOrNull() ?: run {
                    _errorMessage.value = "Please enter valid deaths"
                    return@launch
                }

                val updatedGame = Game(
                    id = gameId,
                    yourChampion = _yourChampion.value,
                    enemyChampion = _enemyChampion.value,
                    result = _result.value,
                    kills = kills,
                    assists = assists,
                    deaths = deaths,
                    notes = _notes.value,
                    timestamp = Timestamp.now()
                )

                db.collection("users").document(userId)
                    .collection("games").document(gameId)
                    .set(updatedGame)
                    .await()

                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save game: ${e.message}"
            }
        }
    }
}