package com.example.back2life.data.repo

import android.util.Log
import com.example.back2life.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser get() = auth.currentUser

    suspend fun registrar(email: String, password: String, nombre: String) {
        // 1. Crear usuario en Authentication
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("No se pudo obtener el ID del usuario")

        // 2. Intentar guardar el perfil en Firestore
        try {
            val perfil = UserProfile(uid = uid, nombre = nombre, email = email)
            db.collection("usuarios").document(uid).set(perfil).await()
        } catch (e: Exception) {
            // SI FALLA LA BASE DE DATOS: Borramos la cuenta para que el usuario pueda volver a intentarlo
            Log.e("AuthRepository", "Error en Firestore: ${e.message}")
            authResult.user?.delete()?.await() // Eliminamos la cuenta fantasma

            if (e.message?.contains("PERMISSION_DENIED") == true) {
                throw Exception("PERMISSION_DENIED")
            } else {
                throw Exception(e.message)
            }
        }
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() = auth.signOut()

    suspend fun obtenerPerfilActual(): UserProfile? {
        val uid = currentUser?.uid ?: return null
        return try {
            val doc = db.collection("usuarios").document(uid).get().await()
            doc.toObject(UserProfile::class.java)
        } catch (e: Exception) { null }
    }
}