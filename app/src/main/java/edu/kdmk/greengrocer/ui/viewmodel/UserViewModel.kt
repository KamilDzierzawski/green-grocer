package edu.kdmk.greengrocer.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import edu.kdmk.greengrocer.data.model.User
import edu.kdmk.greengrocer.data.repository.UserRepository

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    internal var users by mutableStateOf<List<User>>(emptyList())

    suspend fun getUsers() {
        users = userRepository.getUsers()

    }
}