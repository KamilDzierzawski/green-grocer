package edu.kdmk.greengrocer.ui.viewmodel

import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.PlantDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PlantStorageRepository
import java.io.File

class GardenViewModel(
    private val localStorageRepository: LocalStorageRepository,
    private val plantDatabaseRepository: PlantDatabaseRepository,
    private val plantStorageRepository: PlantStorageRepository
) {
    fun addPlantToGarden(name: String, description: String, File: File) {
        // Add plant to garden
    }
}