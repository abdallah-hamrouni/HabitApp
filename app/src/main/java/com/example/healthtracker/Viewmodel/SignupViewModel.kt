package com.example.healthtracker.Viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.Repositrory.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    var message: String = ""
        private set

    fun signup(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = repository.findUserByEmail(email)
            if (user != null) {
                message = "Cet email existe déjà"
                return@launch
            }

            repository.insertUser(name, email, password)
            message = "Compte créé avec succès ✅"
            onSuccess()
        }
    }
}