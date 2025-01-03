package edu.kdmk.greengrocer.data.model

import com.google.type.Date
import java.io.File

data class Plant(
    val id: String? = null,
    val userId: String? = null,
    val name: String = "",
    val description: String = "",
    val image: File? = null,
    val addedDate: Date
)
