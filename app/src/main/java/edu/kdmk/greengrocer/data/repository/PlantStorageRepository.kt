package edu.kdmk.greengrocer.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import edu.kdmk.greengrocer.data.model.Plant
import java.io.File

class PlantStorageRepository(
    private val db: FirebaseStorage
) {
    private val storageReference: StorageReference = db.reference

    fun addPlantImage(
        plant: Plant,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val plantImageRef = storageReference.child("plants/${plant.id}/image.jpg")

        val fileUri = Uri.fromFile(plant.image)

        plantImageRef.putFile(fileUri)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPlantImage(
        plant: Plant,
        onSuccess: (File) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val plantImageRef = storageReference.child("plants/${plant.id}/image.jpg")

        val tempFile = File.createTempFile("plant_image", ".jpg")

        plantImageRef.getFile(tempFile)
            .addOnSuccessListener {
                onSuccess(tempFile)
            }
            .addOnFailureListener { exception ->
                tempFile.delete()
                onFailure(exception)
            }
    }

    fun deletePlantImage(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val plantImageRef = storageReference.child("plants/$id/image.jpg")

        plantImageRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}