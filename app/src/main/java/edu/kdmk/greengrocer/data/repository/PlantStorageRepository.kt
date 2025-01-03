package edu.kdmk.greengrocer.data.repository

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PlantStorageRepository(
    private val db: FirebaseStorage
) {
    private val storageReference: StorageReference = db.reference
}