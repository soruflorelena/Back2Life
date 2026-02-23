package com.example.back2life.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.back2life.data.model.Post
import com.example.back2life.data.model.PostType
import com.example.back2life.data.repo.AuthRepository
import com.example.back2life.data.repo.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CrearPostEstado(
    val cargando: Boolean = false,
    val error: String? = null
)

class CrearPostViewModel(
    private val postRepo: PostRepository = PostRepository(),
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(CrearPostEstado())
    val estado = _estado.asStateFlow()

    fun create(
        titulo: String,
        descripcion: String,
        tipo: PostType,
        precio: Double,
        lugar: String,
        fechaExp: String,
        onExito: (String) -> Unit
    ) = viewModelScope.launch {
        val uid = authRepo.currentUser?.uid
        if (uid == null) {
            _estado.value = CrearPostEstado(error = "No hay sesión iniciada")
            return@launch
        }

        if (titulo.isBlank() || descripcion.isBlank() || lugar.isBlank() || fechaExp.isBlank()) {
            _estado.value = CrearPostEstado(error = "Por favor llena todos los campos")
            return@launch
        }

        _estado.value = CrearPostEstado(cargando = true)

        runCatching {
            postRepo.crearPost(
                Post(
                    autorId = uid,
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    tipo = tipo,
                    precio = precio,
                    lugar = lugar.trim(),
                    fechaExp = fechaExp.trim()
                )
            )
        }.onSuccess { id ->
            _estado.value = CrearPostEstado(cargando = false)
            onExito(id)
        }.onFailure {
            _estado.value = CrearPostEstado(error = it.message)
        }
    }
}