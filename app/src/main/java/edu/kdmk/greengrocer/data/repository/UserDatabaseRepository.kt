package edu.kdmk.greengrocer.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.AuthUser
import edu.kdmk.greengrocer.data.model.PostUser

class UserDatabaseRepository(
    private val firebase: Firebase
) {
    private val db = firebase.firestore

    fun addUserToDatabase(authUser: AuthUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = authUser.id

        if (uid != null) {
            val userDetails = mapOf(
                "fname" to authUser.fname,
                "lname" to authUser.lname,
                "phone" to authUser.phone,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("users").document(uid).set(userDetails)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            onFailure(Exception("User ID is null"))
        }
    }

    fun updateUserProfile(
        user: AuthUser,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = user.id

        if (uid != null) {
            val userDetails = mapOf(
                "fname" to user.fname,
                "lname" to user.lname,
                "phone" to user.phone
            )

            db.collection("users").document(uid).update(userDetails)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            onFailure(Exception("User ID is null"))
        }
    }

    fun getUserFromDatabase(
        authUser: AuthUser,
        onSuccess: (AuthUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users").document(authUser.id ?: "").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val updatedUser = documentSnapshot.toObject(AuthUser::class.java)?.copy(
                        id = authUser.id,
                        email = authUser.email,
                        password = authUser.password
                    )

                    if (updatedUser != null) {
                        onSuccess(updatedUser)
                    } else {
                        onFailure(Exception("User data not found"))
                    }
                } else {
                    onFailure(Exception("User document not found in Firestore"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPostUserFromDatabase(
        id: String,
        onSuccess: (PostUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users").document(id).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val postUser = documentSnapshot.toObject(PostUser::class.java)?.copy(
                        id = id,
                        fname = documentSnapshot.getString("fname") ?: "",
                        lname = documentSnapshot.getString("lname") ?: "",
                        phone = documentSnapshot.getString("phone") ?: ""
                    )

                    if (postUser != null) {
                        onSuccess(postUser)
                    } else {
                        onFailure(Exception("User data not found"))
                    }
                } else {
                    onFailure(Exception("User document not found in Firestore"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}