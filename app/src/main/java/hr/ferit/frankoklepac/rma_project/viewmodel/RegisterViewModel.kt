package hr.ferit.frankoklepac.rma_project.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.ferit.frankoklepac.rma_project.data.DataStoreManager
import hr.ferit.frankoklepac.rma_project.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay

class RegisterViewModel : ViewModel() {
    val username = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")
    val errorMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun register(context: Context, onSuccess: () -> Unit) {
        if (password.value != confirmPassword.value) {
            errorMessage.value = "Passwords do not match"
            return
        }

        if (username.value.isBlank()) {
            errorMessage.value = "Username is required"
            return
        }
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
                val result = auth.createUserWithEmailAndPassword(email.value, password.value).await()
                val userId = result.user?.uid ?: throw Exception("User ID not found")

                val user = User(
                    id = userId,
                    username = username.value,
                    email = email.value
                )
                db.collection("users").document(userId).set(user).await()

                DataStoreManager.saveUserId(context, userId)

                isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = when {
                    e.message?.contains("email-already-in-use") == true -> "Email is already registered"
                    e.message?.contains("invalid-email") == true -> "Invalid email format"
                    e.message?.contains("weak-password") == true -> "Password is too weak (minimum 6 characters)"
                    else -> "Registration failed: ${e.message}"
                }
                isLoading.value = false
            }
        }
    }
}