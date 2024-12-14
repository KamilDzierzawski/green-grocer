package edu.kdmk.greengrocer.data.model

data class AuthUser(
    val id: String? = null,
    val email: String? = "",
    val password: String? = "",
    val fname: String = "",
    val lname: String = "",
    val phone: String = ""
)