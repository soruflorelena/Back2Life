package com.example.back2life.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.back2life.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthEstado(
    val cargando: Boolean = false,
    val error: String? = null,
    val esLogueado: Boolean = false
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(AuthEstado(esLogueado = repo.currentUser != null))
    val state = _state.asStateFlow()

    fun registrar(email: String, contra: String, nombre: String) = viewModelScope.launch {
        if (email.isBlank() || contra.isBlank() || nombre.isBlank()) {
            _state.value = AuthEstado(error = "Llena todos los campos")
            return@launch
        }
        _state.value = AuthEstado(cargando = true)
        runCatching { repo.registrar(email, contra, nombre) }
            .onSuccess { _state.value = AuthEstado(esLogueado = true) }
            .onFailure { _state.value = AuthEstado(error = it.message) }
    }

    fun login(email: String, contra: String) = viewModelScope.launch {
        if (email.isBlank() || contra.isBlank()) {
            _state.value = AuthEstado(error = "Llena todos los campos")
            return@launch
        }
        _state.value = AuthEstado(cargando = true)
        runCatching { repo.login(email, contra) }
            .onSuccess { _state.value = AuthEstado(esLogueado = true) }
            .onFailure { _state.value = AuthEstado(error = it.message) }
    }
}