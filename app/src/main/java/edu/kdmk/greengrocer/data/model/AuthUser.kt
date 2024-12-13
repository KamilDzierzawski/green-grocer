package edu.kdmk.greengrocer.data.model

data class AuthUser(
    val email:String? = "",
    val password:String? = "",
    val fname: String = "",
    val lname: String = "",
    val phone: String = ""
)