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

    private fun esEmailValido(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun registrar(email: String, contra: String, nombre: String, onSuccess: () -> Unit) = viewModelScope.launch {
        if (nombre.trim().isEmpty()) {
            _state.value = AuthEstado(error = "Ingresa tu nombre completo")
            return@launch
        }
        if (!esEmailValido(email)) {
            _state.value = AuthEstado(error = "El formato del correo no es válido")
            return@launch
        }
        if (contra.length < 6) {
            _state.value = AuthEstado(error = "La contraseña debe tener mínimo 6 caracteres")
            return@launch
        }

        _state.value = AuthEstado(cargando = true)
        runCatching { repo.registrar(email.trim(), contra, nombre.trim()) }
            .onSuccess {
                _state.value = AuthEstado(esLogueado = true)
                onSuccess()
            }
            .onFailure { _state.value = AuthEstado(error = traducirError(it)) }
    }

    fun login(email: String, contra: String, onSuccess: () -> Unit) = viewModelScope.launch {
        if (!esEmailValido(email)) {
            _state.value = AuthEstado(error = "El formato del correo no es válido")
            return@launch
        }
        if (contra.isEmpty()) {
            _state.value = AuthEstado(error = "Ingresa tu contraseña")
            return@launch
        }

        _state.value = AuthEstado(cargando = true)
        runCatching { repo.login(email.trim(), contra) }
            .onSuccess {
                _state.value = AuthEstado(esLogueado = true)
                onSuccess()
            }
            .onFailure { _state.value = AuthEstado(error = traducirError(it)) }
    }

    private fun traducirError(e: Throwable): String {
        val msg = e.message ?: return "Error desconocido"
        return when {
            msg.contains("already in use", ignoreCase = true) -> "Este correo ya está registrado."
            msg.contains("invalid-credential", ignoreCase = true) -> "Correo o contraseña incorrectos."
            msg.contains("badly formatted", ignoreCase = true) -> "El correo está mal escrito."
            msg.contains("PERMISSION_DENIED", ignoreCase = true) -> "Error crítico: Firestore no permite guardar datos."
            else -> "Error: $msg"
        }
    }

    fun limpiarError() {
        if (_state.value.error != null) {
            _state.value = _state.value.copy(error = null)
        }
    }
}