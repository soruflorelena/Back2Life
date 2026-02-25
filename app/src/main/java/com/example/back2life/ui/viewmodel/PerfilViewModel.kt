package com.example.back2life.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.back2life.data.model.Post
import com.example.back2life.data.model.UserProfile
import com.example.back2life.data.repo.AuthRepository
import com.example.back2life.data.repo.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PerfilEstado(
    val cargando: Boolean = true,
    val perfil: UserProfile? = null,
    val misPosts: List<Post> = emptyList(),
    val error: String? = null
)

class PerfilViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val postRepo: PostRepository = PostRepository()
) : ViewModel() {
    private val _estado = MutableStateFlow(PerfilEstado())
    val estado = _estado.asStateFlow()

    fun cargarDatos() = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true)
        val uid = authRepo.currentUser?.uid
        if (uid != null) {
            val p = authRepo.obtenerPerfilActual()
            val posts = postRepo.getMisPosts(uid)
            _estado.value = PerfilEstado(cargando = false, perfil = p, misPosts = posts)
        } else {
            _estado.value = PerfilEstado(cargando = false, error = "No se encontró el usuario")
        }
    }

    fun actualizarNombre(nuevoNombre: String) = viewModelScope.launch {
        if (nuevoNombre.isNotBlank()) {
            runCatching {
                authRepo.actualizarNombre(nuevoNombre.trim())
            }.onSuccess {
                cargarDatos() // Recargamos para ver el cambio
            }
        }
    }

    fun logout() = authRepo.logout()
}