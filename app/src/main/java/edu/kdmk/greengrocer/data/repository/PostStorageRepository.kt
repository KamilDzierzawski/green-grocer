package edu.kdmk.greengrocer.data.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.kdmk.greengrocer.data.model.Post
import java.io.File

class PostStorageRepository(
    private val db: FirebaseStorage
) {
    private val storageReference: StorageReference = db.reference

    fun addPostImage(
        post: Post,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postImageRef = storageReference.child("posts/${post.id}/image.jpg")

        val fileUri = Uri.fromFile(post.image)

        postImageRef.putFile(fileUri)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPostImage(
        id: String,
        onSuccess: (File?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postImageRef = storageReference.child("posts/$id/image.jpg")

        postImageRef.metadata
            .addOnSuccessListener { metadata ->
                if (metadata != null) {
                    val tempFile = File.createTempFile("post_image", ".jpg")

                    postImageRef.getFile(tempFile)
                        .addOnSuccessListener {
                            onSuccess(tempFile)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("PostImage", "Failed to download image for post $id: ${exception.message}")
                            tempFile.delete()
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("PostImage", "No image found for post $id, returning null.")
                onSuccess(null)
            }
    }
}