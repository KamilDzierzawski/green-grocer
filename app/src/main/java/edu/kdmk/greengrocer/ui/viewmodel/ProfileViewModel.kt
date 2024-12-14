package edu.kdmk.greengrocer.ui.viewmodel

import androidx.lifecycle.ViewModel
import edu.kdmk.greengrocer.data.repository.AuthRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val localStorageRepository: LocalStorageRepository
) : ViewModel() {
    fun getUserData() = localStorageRepository.getUserData()

    fun clearUserData() = localStorageRepository.clearUserData()
}