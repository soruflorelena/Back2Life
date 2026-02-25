package com.example.back2life.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.back2life.data.model.Post
import com.example.back2life.data.model.PostType
import com.example.back2life.data.repo.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FeedEstado(
    val cargando: Boolean = false,
    val postsOriginales: List<Post> = emptyList(),
    val postsFiltrados: List<Post> = emptyList(),
    val filtroActual: String = "Todos",
    val error: String? = null
)

class FeedViewModel(private val repo: PostRepository = PostRepository()) : ViewModel() {
    private val _estado = MutableStateFlow(FeedEstado())
    val estado = _estado.asStateFlow()

    // Cagar los Posts
    fun cargar() = viewModelScope.launch {
        _estado.value = _estado.value.copy(cargando = true)
        runCatching { repo.getFeed() }
            .onSuccess { posts ->
                val disponibles = posts.filter { it.estado.name == "DISPONIBLE" }

                _estado.value = _estado.value.copy(
                    cargando = false,
                    postsOriginales = disponibles,
                    postsFiltrados = aplicarFiltro(disponibles, _estado.value.filtroActual)
                )
            }
            .onFailure {
                _estado.value = _estado.value.copy(cargando = false, error = it.message)
            }
    }

    // Función para cambiar el filtro
    fun cambiarFiltro(nuevoFiltro: String) {
        val filtrados = aplicarFiltro(_estado.value.postsOriginales, nuevoFiltro)
        _estado.value = _estado.value.copy(
            filtroActual = nuevoFiltro,
            postsFiltrados = filtrados
        )
    }

    // Aplicar el filtro
    private fun aplicarFiltro(posts: List<Post>, filtro: String): List<Post> {
        return when (filtro) {
            "COMIDA" -> posts.filter { it.tipo == PostType.COMIDA }
            "MEDICINA" -> posts.filter { it.tipo == PostType.MEDICINA }
            else -> posts
        }
    }
}