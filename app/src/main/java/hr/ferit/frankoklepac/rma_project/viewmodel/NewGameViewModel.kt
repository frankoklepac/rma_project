package hr.ferit.frankoklepac.rma_project.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import hr.ferit.frankoklepac.rma_project.model.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NewGameViewModel : ViewModel() {
    private val _game = mutableStateOf(Game("", "", "", "", 0, 0, 0, "", null))
    val game: State<Game> = _game

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun updateGame(field: String, value: Any) {
        _game.value = when (field) {
            "yourChampion" -> _game.value.copy(yourChampion = value as String)
            "enemyChampion" -> _game.value.copy(enemyChampion = value as String)
            "result" -> _game.value.copy(result = value as String)
            "kills" -> _game.value.copy(kills = value as Int)
            "assists" -> _game.value.copy(assists = value as Int)
            "deaths" -> _game.value.copy(deaths = value as Int)
            "notes" -> _game.value.copy(notes = value as String)
            else -> _game.value
        }
    }

    fun saveGame(context: Context, onSuccess: () -> Unit) {
        if (_game.value.yourChampion.isBlank()) {
            _errorMessage.value = "Your Champion is required"
            return
        }
        if (_game.value.enemyChampion.isBlank()) {
            _errorMessage.value = "Enemy Champion is required"
            return
        }
        if (_game.value.result.isBlank()) {
            _errorMessage.value = "Result is required"
            return
        }

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

                val gameData = _game.value.copy(
                    id = db.collection("users").document(userId).collection("games").document().id,
                    timestamp = Timestamp.now()
                )

                db.collection("users").document(userId).collection("games").document(gameData.id)
                    .set(gameData).await()

                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save game: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}