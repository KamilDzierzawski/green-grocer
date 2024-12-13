package edu.kdmk.greengrocer.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebase: Firebase
) {
    private val db = firebase.firestore

    suspend fun getUsers(): List<User> {
        val users = mutableListOf<User>()
        try {
            val result = db.collection("users").get().await() // await() wymaga Coroutines
            for (document in result) {
                val user = document.toObject(User::class.java)
                users.add(user)
            }
        } catch (exception: Exception) {
            Log.w("UserRepository", "Error getting users", exception)
        }
        Log.d("UserRepository", "Users: $users")
        return users
    }
}