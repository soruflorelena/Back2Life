package com.example.back2life.data.repo

import com.example.back2life.data.model.Comentario
import com.example.back2life.data.model.Post
import com.example.back2life.data.model.PostStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val postsCol get() = db.collection("posts")

    suspend fun crearPost(post: Post): String {
        val now = Timestamp.now()
        val doc = postsCol.document()
        val data = post.copy(
            id = doc.id,
            creado = now,
            actualizado = now
        )
        doc.set(data).await()
        return doc.id
    }

    suspend fun actualizarPost(postId: String, updates: Map<String, Any>) {
        postsCol.document(postId).update(updates + mapOf("actualizado" to Timestamp.now())).await()
    }

    suspend fun marcarEntregado(postId: String) {
        actualizarPost(postId, mapOf("estado" to PostStatus.ENTREGADA.name))
    }

    suspend fun getFeed(): List<Post> {
        val snap = postsCol
            .orderBy("creado", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()

        // Firestore guarda enums como String (name)
        return snap.documents.mapNotNull { d ->
            val base = d.toObject(Post::class.java) ?: return@mapNotNull null
            base.copy(id = d.id)
        }
    }

    suspend fun getPost(postId: String): Post? {
        val d = postsCol.document(postId).get().await()
        val base = d.toObject(Post::class.java) ?: return null
        return base.copy(id = d.id)
    }

    suspend fun addComentario(postId: String, comentario: Comentario) {
        val now = Timestamp.now()
        val doc = postsCol.document(postId).collection("comentarios").document()
        doc.set(comentario.copy(id = doc.id, postId = postId, creado = now)).await()
    }

    suspend fun getComentario(postId: String): List<Comentario> {
        val snap = postsCol.document(postId)
            .collection("comentarios")
            .orderBy("creado", Query.Direction.ASCENDING)
            .get()
            .await()

        return snap.documents.mapNotNull { it.toObject(Comentario::class.java) }
    }
}