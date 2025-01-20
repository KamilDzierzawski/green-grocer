package edu.kdmk.greengrocer.data.model

import com.google.firebase.Timestamp
import java.io.File

data class Plant(
    val id: String? = null,
    val userId: String? = null,
    val name: String = "",
    val description: String = "",
    val species: String = "",
    val image: File? = null,
    val timestamp: Timestamp? = null
)
