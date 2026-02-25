package com.example.back2life.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.back2life.data.model.Comentario
import com.example.back2life.data.model.Post
import com.example.back2life.data.repo.AuthRepository
import com.example.back2life.data.repo.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PostDetalleEstado(
    val cargando: Boolean = false,
    val post: Post? = null,
    val comentarios: List<Comentario> = emptyList(),
    val error: String? = null,
    val fueEliminado: Boolean = false // <-- Para saber si debemos cerrar la pantalla
)

class PostDetalleViewModel(
    private val postRepo: PostRepository = PostRepository(),
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _estado = MutableStateFlow(PostDetalleEstado())
    val estado = _estado.asStateFlow()

    fun cargar(postId: String) = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true, error = null)
        runCatching {
            val p = postRepo.getPost(postId)
            val c = postRepo.getComentario(postId)
            p to c
        }.onSuccess { (p, c) ->
            _estado.value = PostDetalleEstado(post = p, comentarios = c)
        }.onFailure {
            _estado.value = PostDetalleEstado(error = it.message)
        }
    }

    fun addComentario(postId: String, text: String) = viewModelScope.launch {
        val uid = authRepo.currentUser?.uid ?: return@launch
        val perfil = authRepo.obtenerPerfilActual()
        val nombreUsuario = perfil?.nombre ?: "Usuario"

        runCatching {
            postRepo.addComentario(postId, Comentario(autorId = uid, autorNombre = nombreUsuario, texto = text))
            postRepo.getComentario(postId)
        }.onSuccess { updated ->
            _estado.value = _estado.value.copy(comentarios = updated)
        }
    }

    fun marcarEntregado(postId: String) = viewModelScope.launch {
        runCatching { postRepo.marcarEntregado(postId) }.onSuccess { cargar(postId) }
    }

    // NUEVO: Eliminar post y avisarle a la pantalla
    fun eliminarPost(postId: String) = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true)
        runCatching { postRepo.borrarPost(postId) }
            .onSuccess { _estado.value = _estado.value.copy(fueEliminado = true) }
            .onFailure { _estado.value = _estado.value.copy(cargando = false, error = it.message) }
    }

    // NUEVO: Actualizar los datos en Firebase y recargar
    fun editarPost(postId: String, titulo: String, descripcion: String, precio: Double) = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true)
        runCatching {
            postRepo.actualizarPost(postId, mapOf(
                "titulo" to titulo.trim(),
                "descripcion" to descripcion.trim(),
                "precio" to precio
            ))
        }.onSuccess { cargar(postId) } // Recargamos para ver los cambios
    }

    fun esAutor(): Boolean {
        val uid = authRepo.currentUser?.uid ?: return false
        return _estado.value.post?.autorId == uid
    }

    // Necesario para que las burbujas de chat se acomoden izquierda/derecha
    fun currentUserId() = authRepo.currentUser?.uid
}