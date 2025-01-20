package edu.kdmk.greengrocer.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
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

    fun getFilteredPosts(
        searchText: String,
        searchTag: String,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query = db.collection("posts") as Query

        if (searchTag.isNotBlank()) {
            query = query.whereEqualTo("type", searchTag)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val posts = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Post::class.java)?.copy(id = document.id)
                }

                val filteredPosts = if (searchText.isNotBlank()) {
                    posts.filter { post ->
                        post.title.contains(searchText, ignoreCase = true) ||
                                post.description.contains(searchText, ignoreCase = true)
                    }
                } else {
                    posts // Jeśli searchText jest pusty, zwracamy wszystkie posty
                }

                onSuccess(filteredPosts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}