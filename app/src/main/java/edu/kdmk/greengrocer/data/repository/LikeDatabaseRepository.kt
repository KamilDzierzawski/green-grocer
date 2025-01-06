package edu.kdmk.greengrocer.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.Like

class LikeDatabaseRepository(
    private val firebase: Firebase
) {
    private val db = firebase.firestore

    fun getLikes(
        postId: String,
        onSuccess: (List<Like>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("likes")
            .whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val likes = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Like::class.java)?.copy(
                        id = document.id,
                        postId = postId,
                        userId = document.getString("userId") ?: ""
                    )
                }
                onSuccess(likes)
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "LikesRepository",
                    "Failed to retrieve likes for post $postId: ${exception.message}"
                )
                onFailure(exception)
            }
    }
}


