package com.example.back2life.data.repo

import com.example.back2life.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser get() = auth.currentUser

    suspend fun registrar(email: String, password: String, nombre: String) {
        // 1. Crear usuario en Authentication
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("No se pudo obtener el ID del usuario")

        val perfil = UserProfile(uid = uid, nombre = nombre, email = email)

        // 2. Intentar guardar en Firestore con un límite de 5 segundos
        val exito = withTimeoutOrNull(5000) {
            db.collection("usuarios").document(uid).set(perfil).await()
            true // Retorna true si lo logró a tiempo
        }

        if (exito == null) {
            // Si pasaron 5 segundos y Firestore no respondió, lanzamos el error a la pantalla
            throw Exception("La base de datos no responde. Verifica tu conexión a internet o las Reglas de Firestore.")
        }
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() = auth.signOut()

    suspend fun obtenerPerfilActual(): UserProfile? {
        val uid = currentUser?.uid ?: return null
        return try {
            // También le ponemos límite de tiempo al obtener el perfil
            withTimeoutOrNull(5000) {
                val doc = db.collection("usuarios").document(uid).get().await()
                doc.toObject(UserProfile::class.java)
            }
        } catch (e: Exception) { null }
    }
}