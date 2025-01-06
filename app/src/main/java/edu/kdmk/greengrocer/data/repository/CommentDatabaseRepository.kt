package edu.kdmk.greengrocer.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.Comment

class CommentDatabaseRepository(
    private val firebase: Firebase
) {
    private val db = firebase.firestore

    fun getComments(
        postId: String,
        onSuccess: (List<Comment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("comments")
            .whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val comments = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Comment::class.java)?.copy(
                        id = document.id,
                        postId = postId,
                        userId = document.getString("userId") ?: "",
                        content = document.getString("content") ?: "",
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: "",
                        timestamp = document.getTimestamp("timestamp")
                    )
                }
                onSuccess(comments)
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "CommentsRepository",
                    "Failed to retrieve comments for post $postId: ${exception.message}"
                )
                onFailure(exception)
            }
    }
}