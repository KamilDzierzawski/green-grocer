package edu.kdmk.greengrocer.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class PlantDatabaseRepository(
    private val firebase: Firebase
) {
    private val db = firebase.firestore
}