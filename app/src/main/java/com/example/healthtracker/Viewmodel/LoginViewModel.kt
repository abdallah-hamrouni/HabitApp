package com.example.healthtracker.Viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.Repositrory.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    val loggedInUserEmail = mutableStateOf<String?>(null)

    fun login(email: String, password: String, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUser(email, password)
            if (user != null) {
                loggedInUserEmail.value = user.email  // ✅ On stocke l’email du user connecté
                onResult(true, "Connexion réussie ✅")
            } else {
                onResult(false, "Email ou mot de passe incorrect ❌")
            }
        }
    }
}
