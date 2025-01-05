package edu.kdmk.greengrocer.data.model

import com.google.firebase.Timestamp
import org.w3c.dom.Comment
import java.io.File

data class Post(
    val id: String? = null,
    val userId: String? = null,
    val title: String = "",
    val description: String = "",
    val like: Int = 0,
    val dislike: Int = 0,
    val image: File? = null,
    val timestamp: Timestamp? = null,
    val comments: List<Comment>? = null,
    val postUser: PostUser? = null
)