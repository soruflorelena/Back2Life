package com.example.back2life.data.model

import com.google.firebase.Timestamp

enum class PostType { COMIDA, MEDICINA }
enum class PostStatus { DISPONIBLE, ENTREGADA, EXPIRADA }

data class UserProfile(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val fotoUrl: String = "",
    val creado: Timestamp? = null
)

data class Post(
    val id: String = "",
    val autorId: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: PostType = PostType.COMIDA,
    val fechaExp: String = "", // <-- Cambiado a String para simplificar
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
    val texto: String = "",
    val creado: Timestamp? = null
)

data class Comentario(
    val id: String = "",
    val postId: String = "",
    val autorId: String = "",
    val autorNombre: String = "Usuario", // <-- NUEVO CAMPO
    val texto: String = "",
    val creado: Timestamp? = null
)