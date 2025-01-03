package edu.kdmk.greengrocer.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class UserStorageRepository(
    private val db: FirebaseStorage
) {
    private val storageReference: StorageReference = db.reference

    fun uploadUserProfileImage(
        userId: String, imageFile:
        File,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userProfileRef = storageReference.child("users/$userId/profile_image.jpg") // Ścieżka do zdjęcia

        val fileUri = Uri.fromFile(imageFile)

        userProfileRef.putFile(fileUri)
            .addOnSuccessListener {
                Log.d("StorageRepository", "Image uploaded successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("StorageRepository", "Failed to upload image: ${exception.message}")
                onFailure(exception)
            }
    }

    fun downloadUserProfileImage(
        userId: String,
        onSuccess: (File) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userProfileRef = storageReference.child("users/$userId/profile_image.jpg")

        val tempFile = File.createTempFile("profile_image", ".jpg")

        userProfileRef.getFile(tempFile)
            .addOnSuccessListener {
                Log.d("StorageRepository", "Image downloaded to temporary file at ${tempFile.absolutePath}")
                onSuccess(tempFile)
            }
            .addOnFailureListener { exception ->
                Log.e("StorageRepository", "Failed to download image: ${exception.message}")
                tempFile.delete()
                onFailure(exception)
            }
    }

    fun deleteUserProfileImage(
        userId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userProfileRef = storageReference.child("users/$userId/profile_image.jpg")

        userProfileRef.delete()
            .addOnSuccessListener {
                Log.d("StorageRepository", "Image deleted successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("StorageRepository", "Failed to delete image: ${exception.message}")
                onFailure(exception)
            }
    }
}