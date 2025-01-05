package edu.kdmk.greengrocer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    fun addPlantToGarden(
        name: String,
        species: String,
        description: String,
        file: File?
    ) {
        val plant = Plant(
            userId = localStorageRepository.getUserData()?.id ?: "",
            name = name,
            species = species,
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


    private val _plants = MutableLiveData<List<Plant>>()
    val plants: LiveData<List<Plant>> get() = _plants

    fun loadPlants() {
        getPlantsFromGarden { plantList ->
            _plants.postValue(plantList) // Przekazanie listy do LiveData
        }
    }


    private fun getPlantsFromGarden(
        onSuccess: (List<Plant>) -> Unit,
    ) {
        plantDatabaseRepository.getPlants(
            userId = localStorageRepository.getUserData()?.id ?: "",
            onSuccess = { plants ->
                val updatedPlants = mutableListOf<Plant>()
                var processedCount = 0

                // Funkcja pomocnicza do zakończenia operacji
                fun checkCompletion() {
                    if (processedCount == plants.size) {
                        onSuccess(updatedPlants)
                    }
                }

                plants.forEach { plant ->
                    plantStorageRepository.getPlantImage(
                        plant,
                        onSuccess = { image ->
                            updatedPlants.add(plant.copy(image = image))
                            processedCount++
                            checkCompletion()
                        },
                        onFailure = { exception ->
                            Log.e("GardenViewModel", "Failed to retrieve plant image for ${plant.id}: ${exception.message}")
                            processedCount++
                            checkCompletion()
                        }
                    )
                }

                if (plants.isEmpty()) {
                    onSuccess(emptyList())
                }
            },
            onFailure = { exception ->
                Log.e("GardenViewModel", "Failed to retrieve plants from garden: ${exception.message}")
            }
        )
    }

    fun deletePlantFromGarden(id: String) {
        plantDatabaseRepository.deletePlant(
            id,
            onSuccess = {
                Log.d("GardenViewModel", "Plant deleted from garden")
            },
            onFailure = { exception ->
                Log.e("GardenViewModel", "Failed to delete plant from garden: ${exception.message}")
            }
        )
    }
}