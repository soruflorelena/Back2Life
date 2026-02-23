package com.example.back2life.data.repo

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
        auth.createUserWithEmailAndPassword(email, password).await()
        val uid = auth.currentUser?.uid ?: return

        // Guardamos el perfil con el nombre en Firestore
        val perfil = UserProfile(uid = uid, nombre = nombre, email = email)
        db.collection("usuarios").document(uid).set(perfil).await()
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