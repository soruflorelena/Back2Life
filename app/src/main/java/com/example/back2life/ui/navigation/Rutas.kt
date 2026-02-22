package com.example.back2life.ui.navigation

sealed class Ruta(val camino: String) {
    data object Auth : Ruta("auth")
    data object Feed : Ruta("feed")
    data object CrearPost : Ruta("crear_post")
    data object PostDetalle : Ruta("post_detalle/{postId}") {
        fun crear(postId: String) = "post_detalle/$postId"
    }
}