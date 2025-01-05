package edu.kdmk.greengrocer.data.model

import com.google.firebase.Timestamp

data class Comment(
    val id: String? = null,
    val userId: String? = null,
    val postId: String? = null,
    val content: String = "",
    val timestamp: Timestamp? = null,
    val firstName: String = "",
    val lastName: String = ""
)