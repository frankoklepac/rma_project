package hr.ferit.frankoklepac.rma_project.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val errorMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()

    fun login(context: Context, onSuccess: () -> Unit) {
        if (email.value.isBlank()) {
            errorMessage.value = "Email is required"
            return
        }
        if (password.value.isBlank()) {
            errorMessage.value = "Password is required"
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email.value, password.value).await()
                val userId = result.user?.uid ?: throw Exception("User ID not found")

                DataStoreManager.saveUserId(context, userId)

                isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = when {
                    e.message?.contains("user-not-found") == true -> "No user found with this email"
                    e.message?.contains("wrong-password") == true -> "Incorrect password"
                    e.message?.contains("invalid-email") == true -> "Invalid email format"
                    else -> "Login failed: ${e.message}"
                }
                isLoading.value = false
            }
        }
    }
}