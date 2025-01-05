package edu.kdmk.greengrocer.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.Plant
import kotlinx.coroutines.tasks.await

class PlantDatabaseRepository(
    private val firebase: Firebase
) {
    private val db = firebase.firestore

    fun addPlant(
        plant: Plant,
        onSuccess: (Plant) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val data = mapOf(
            "userId" to plant.userId,
            "name" to plant.name,
            "species" to plant.species,
            "description" to plant.description,
            "timestamp" to plant.timestamp
        )

        db.collection("plants").add(data)
            .addOnSuccessListener { documentReference ->
                val updatedPlant = plant.copy(id = documentReference.id)
                onSuccess(updatedPlant)
            }
            .addOnFailureListener { exception ->
                // Handle the error
                onFailure(exception)
            }
    }

    fun getPlants(
        userId: String,
        onSuccess: (List<Plant>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("plants")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val plants = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Plant::class.java)?.copy(id = document.id)
                }
                onSuccess(plants)
            }
            .addOnFailureListener { exception ->
                // Handle the error
                onFailure(exception)
            }
    }

    fun deletePlant(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("plants").document(id)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Handle the error
                onFailure(exception)
            }
    }

    fun updatePlant(
        plant: Plant,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val data = mapOf(
            "name" to plant.name,
            "species" to plant.species,
            "description" to plant.description
        )

        plant.id?.let {
            db.collection("plants").document(it)
                .update(data)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }
}