package edu.kdmk.greengrocer.ui.viewmodel

import android.util.Log
import com.google.firebase.Timestamp
import edu.kdmk.greengrocer.data.model.Plant
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.PlantDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PlantStorageRepository
import java.io.File

class GardenViewModel(
    private val localStorageRepository: LocalStorageRepository,
    private val plantDatabaseRepository: PlantDatabaseRepository,
    private val plantStorageRepository: PlantStorageRepository
) {
    fun addPlantToGarden(name: String, description: String, file: File?) {
        val plant = Plant(
            userId = localStorageRepository.getUserData()?.id ?: "",
            name = name,
            description = description,
            image = file,
            timestamp = Timestamp.now()
        )

        plantDatabaseRepository.addPlant(
            plant,
            onSuccess = { addedPlant ->
                Log.d("GardenViewModel", "Plant added to garden: $addedPlant")
                plantStorageRepository.addPlantImage(
                    addedPlant,
                    onSuccess = {
                        Log.d("GardenViewModel", "Plant image added to storage")
                    },
                    onFailure = { exception ->
                        Log.e("GardenViewModel", "Failed to add plant image to storage: ${exception.message}")
                    }
                )
            },
            onFailure = { exception ->
                Log.e("GardenViewModel", "Failed to add plant to garden: ${exception.message}")
            }
        )
    }
}