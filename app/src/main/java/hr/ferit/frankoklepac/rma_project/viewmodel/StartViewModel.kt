package hr.ferit.frankoklepac.rma_project.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StartViewModel : ViewModel() {
    private val _welcomeMessage = MutableStateFlow("Welcome to the App!")
    val welcomeMessage: StateFlow<String> = _welcomeMessage.asStateFlow()

}