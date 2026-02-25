package com.example.back2life.data.repo

import android.util.Log
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
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("No se pudo obtener el ID")

        val perfil = UserProfile(uid = uid, nombre = nombre, email = email)

        val exito = withTimeoutOrNull(5000) {
            db.collection("usuarios").document(uid).set(perfil).await()
            true
        }

        if (exito == null) {
            authResult.user?.delete()?.await()
            throw Exception("La base de datos no responde.")
        }
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() = auth.signOut()

    suspend fun obtenerPerfilActual(): UserProfile? {
        val uid = currentUser?.uid ?: return null
        return try {
            withTimeoutOrNull(5000) {
                val doc = db.collection("usuarios").document(uid).get().await()
                doc.toObject(UserProfile::class.java)
            }
        } catch (e: Exception) { null }
    }

    // NUEVA FUNCIÓN: Actualiza el nombre en Firebase
    suspend fun actualizarNombre(nuevoNombre: String) {
        val uid = currentUser?.uid ?: throw Exception("No hay sesión")
        db.collection("usuarios").document(uid).update("nombre", nuevoNombre).await()
    }
}