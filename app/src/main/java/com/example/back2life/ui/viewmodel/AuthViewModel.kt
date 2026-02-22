package com.example.back2life.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.back2life.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val esLogueado: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(AuthState(esLogueado = repo.currentUser != null))
    val state = _estado.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true, error = null)
        runCatching { repo.login(email, password) }
            .onSuccess { _estado.value = AuthState(esLogueado = true) }
            .onFailure { _estado.value = AuthState(esLogueado = false, error = it.message) }
    }

    fun registrar(email: String, password: String) = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true, error = null)
        runCatching { repo.registrar(email, password) }
            .onSuccess { _estado.value = AuthState(esLogueado = true) }
            .onFailure { _estado.value = AuthState(esLogueado = false, error = it.message) }
    }

    fun logout() {
        repo.logout()
        _estado.value = AuthState(esLogueado = false)
    }
}