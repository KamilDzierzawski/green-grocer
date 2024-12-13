package edu.kdmk.greengrocer.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.AuthUser
import javax.inject.Inject

class AuthRepository (
    private val auth: FirebaseAuth,
    private val firebase: Firebase
) {
    private val db = firebase.firestore

    fun registerUser(
        authUser: AuthUser,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (authUser.email.isNullOrEmpty() || authUser.password.isNullOrEmpty()) {
            onFailure(Exception("Email and password cannot be empty"))
            return
        }

        auth.createUserWithEmailAndPassword(authUser.email!!, authUser.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val userDetails = mapOf(
                            "fname" to authUser.fname,
                            "lname" to authUser.lname,
                            "phone" to authUser.phone,
                            "email" to authUser.email,
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                        db.collection("users").document(uid).set(userDetails)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { exception -> onFailure(exception) }
                    } else {
                        onFailure(Exception("Failed to retrieve user UID"))
                    }
                } else {
                    onFailure(task.exception ?: Exception("Registration failed"))
                }
            }
    }
}