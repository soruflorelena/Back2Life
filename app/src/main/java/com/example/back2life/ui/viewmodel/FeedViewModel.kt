package com.example.back2life.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.back2life.data.model.Post
import com.example.back2life.data.repo.AuthRepository
import com.example.back2life.data.repo.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FeedEstado(
    val cargando: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)

class FeedViewModel(
    private val repo: PostRepository = PostRepository(),
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _estado = MutableStateFlow(FeedEstado())
    val estado = _estado.asStateFlow()

    fun cargar() = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true, error = null)
        runCatching { repo.getFeed() }
            .onSuccess { _estado.value = FeedEstado(posts = it) }
            .onFailure { _estado.value = FeedEstado(error = it.message) }
    }

    fun cerrarSesion() {
        authRepo.logout()
    }
}