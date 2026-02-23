package com.example.back2life.data.model

import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = ""
)

data class Post(
    val id: String = "",
    val autorId: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: PostType = PostType.COMIDA,
    val fechaExp: String = "",
    val estado: PostStatus = PostStatus.DISPONIBLE,
    val precio: Double = 0.0,
    val lugar: String = "",
    val fotoUrl: String = "",
    val creado: Timestamp? = null,
    val actualizado: Timestamp? = null
)

data class Comentario(
    val id: String = "",
    val postId: String = "",
    val autorId: String = "",
    val autorNombre: String = "Usuario",
    val texto: String = "",
    val creado: Timestamp? = null
)

enum class PostType {
    COMIDA, MEDICINA
}

enum class PostStatus {
    DISPONIBLE, ENTREGADA, EXPIRADA
}