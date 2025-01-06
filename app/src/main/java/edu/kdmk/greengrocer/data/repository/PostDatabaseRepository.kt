package edu.kdmk.greengrocer.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.Like
import edu.kdmk.greengrocer.data.model.Post

class PostDatabaseRepository(
    private val firebase: Firebase
) {
    private val db = firebase.firestore

    fun addPost(
        post: Post,
        onSuccess: (Post) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val data = mapOf(
            "userId" to post.userId,
            "type" to post.type,
            "title" to post.title,
            "description" to post.description,
            "timestamp" to post.timestamp
        )

        db.collection("posts").add(data)
            .addOnSuccessListener { documentReference ->
                val updatedPost = post.copy(id = documentReference.id)
                onSuccess(updatedPost)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getAllPosts(
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("posts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val posts = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Post::class.java)?.copy(id = document.id)
                }
                onSuccess(posts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}