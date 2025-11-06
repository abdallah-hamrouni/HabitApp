package com.example.healthtracker.Viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.Repositrory.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUser(email, password)
            if (user != null) {
                onResult(true, "Connexion réussie ✅")
            } else {
                onResult(false, "Email ou mot de passe incorrect ❌")
            }
        }
    }
}