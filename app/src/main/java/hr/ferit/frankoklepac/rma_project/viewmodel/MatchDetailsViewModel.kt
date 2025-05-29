package hr.ferit.frankoklepac.rma_project.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import hr.ferit.frankoklepac.rma_project.model.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MatchDetailsViewModel : ViewModel() {
    private val _game = mutableStateOf<Game?>(null)
    val game: State<Game?> = _game

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

    fun deleteGame(context: Context, gameId: String, onSuccess: () -> Unit) {
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

                db.collection("users").document(userId)
                    .collection("games").document(gameId)
                    .delete()
                    .await()

                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete game: ${e.message}"
            }
        }
    }
}