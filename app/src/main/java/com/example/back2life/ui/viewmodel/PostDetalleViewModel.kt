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
    val error: String? = null
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
        runCatching {
            postRepo.addComentario(postId, Comentario(autorId = uid, texto = text))
            postRepo.getComentario(postId)
        }.onSuccess { updated ->
            _estado.value = _estado.value.copy(comentarios = updated)
        }
    }

    fun marcarEntregado(postId: String) = viewModelScope.launch {
        runCatching { postRepo.marcarEntregado(postId) }
            .onSuccess { cargar(postId) }
    }

    fun esAutor(): Boolean {
        val uid = authRepo.currentUser?.uid ?: return false
        return _estado.value.post?.autorId == uid
    }
}