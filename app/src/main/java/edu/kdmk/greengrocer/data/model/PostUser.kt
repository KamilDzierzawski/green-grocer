package edu.kdmk.greengrocer.data.model

import java.io.File

data class PostUser(
    val id: String? = null,
    val fname: String = "",
    val lname: String = "",
    val phone: String = "",
    val image: File? = null
)