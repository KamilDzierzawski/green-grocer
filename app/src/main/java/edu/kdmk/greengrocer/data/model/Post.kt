package edu.kdmk.greengrocer.data.model

import com.google.firebase.Timestamp
import java.io.File

data class Post(
    val id: String? = null,
    val userId: String? = null,
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val image: File? = null,
    val timestamp: Timestamp? = null,
    val comments: List<Comment>? = null,
    val postUser: PostUser? = null,
    val likes: List<Like>? = null
)