package com.example.back2life.data.repo

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser get() = auth.currentUser

    suspend fun registrar(email: String, password: String) {
        // CORRECCIÓN: Usar los métodos oficiales de Firebase en inglés
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun login(email: String, password: String) {
        // CORRECCIÓN: Usar los métodos oficiales de Firebase en inglés
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun logout() = auth.signOut()
}