package edu.kdmk.greengrocer.data.repository

import com.google.firebase.auth.FirebaseAuth
import edu.kdmk.greengrocer.data.model.AuthUser

class AuthRepository (
    private val auth: FirebaseAuth,
) {

    fun registerUser(
        authUser: AuthUser,
        onSuccess: (AuthUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (authUser.email.isNullOrEmpty() || authUser.password.isNullOrEmpty()) {
            onFailure(Exception("Email and password cannot be empty"))
            return
        }

        auth.createUserWithEmailAndPassword(authUser.email, authUser.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {

                        val updatedAuthUser = authUser.copy(id = uid)

                        onSuccess(updatedAuthUser)
                    } else {
                        onFailure(Exception("Failed to retrieve user UID"))
                    }
                } else {
                    onFailure(task.exception ?: Exception("Registration failed"))
                }
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (AuthUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (email.isEmpty() || password.isEmpty()) {
            onFailure(Exception("Email and password cannot be empty"))
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val authUser = AuthUser(id = uid, email = email, password = password)
                        onSuccess(authUser)
                    } else {
                        onFailure(Exception("User UID is null"))
                    }
                } else {
                    onFailure(task.exception ?: Exception("Login failed"))
                }
            }
    }
}