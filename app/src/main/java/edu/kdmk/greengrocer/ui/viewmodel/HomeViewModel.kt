package edu.kdmk.greengrocer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import edu.kdmk.greengrocer.data.model.Plant
import edu.kdmk.greengrocer.data.model.Post
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.PlantDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PlantStorageRepository
import java.io.File

class HomeViewModel(
    private val localStorageRepository: LocalStorageRepository,
    private val plantDatabaseRepository: PlantDatabaseRepository,
    private val plantStorageRepository: PlantStorageRepository
) {

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

    fun addPost(
        type: String,
        title: String,
        description: String,
        image: File?,
        timestamp: Timestamp
    ) {
        Log.d("HomeViewModel", "Adding post to database with type: $type, title: $title, description: $description, image: $image, timestamp: $timestamp")
    }
}