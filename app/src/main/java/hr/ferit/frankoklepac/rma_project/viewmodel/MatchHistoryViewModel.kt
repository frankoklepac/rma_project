package hr.ferit.frankoklepac.rma_project.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import hr.ferit.frankoklepac.rma_project.model.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MatchHistoryViewModel : ViewModel() {
    private val _games = mutableStateOf<List<Game>>(emptyList())
    val games: State<List<Game>> = _games

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun loadGames(context: Context) {
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

                val snapshot = db.collection("users").document(userId).collection("games")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val gamesList = snapshot.documents.mapNotNull { it.toObject(Game::class.java) }
                _games.value = gamesList
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load games: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}