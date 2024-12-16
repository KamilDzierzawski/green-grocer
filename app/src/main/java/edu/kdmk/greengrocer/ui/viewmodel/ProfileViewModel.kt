package edu.kdmk.greengrocer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.kdmk.greengrocer.data.model.AuthUser
import edu.kdmk.greengrocer.data.repository.AuthRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.StorageRepository
import edu.kdmk.greengrocer.data.repository.UserRepository
import java.io.File

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val localStorageRepository: LocalStorageRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfileImage = MutableLiveData<File?>()
    val userProfileImage: LiveData<File?> = _userProfileImage

    private val _isImageLoading = MutableLiveData<Boolean>()
    val isImageLoading: LiveData<Boolean> = _isImageLoading

    init {
        loadUserProfileImage()
    }

    fun getUserData() = localStorageRepository.getUserData()

    fun clearUserData() = localStorageRepository.clearUserData()

    private fun loadUserProfileImage() {
        _isImageLoading.value = true
        val localImageFile = localStorageRepository.getUserProfileImage()
        _userProfileImage.value = localImageFile
        _isImageLoading.value = false
    }

    fun deleteUserProfileImage(userId: String) {
        localStorageRepository.deleteUserProfileImage()
        storageRepository.deleteUserProfileImage(userId, {
            _userProfileImage.value = null
        }, {
            Log.e("ProfileViewModel", "Failed to delete user profile image: ${it.message}")
        })
    }

    fun uploadUserProfileImage(userId: String, imageFile: File, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        localStorageRepository.saveUserProfileImage(imageFile)

        storageRepository.uploadUserProfileImage(userId, imageFile, {
            _userProfileImage.value = imageFile
            onSuccess()
        }, onFailure)
    }

    fun updateUserProfile(user: AuthUser) {
        localStorageRepository.saveUserData(user)
        userRepository.updateUserProfile(user, {
            Log.d("ProfileViewModel", "User profile updated successfully")
        }, {
            Log.e("ProfileViewModel", "Failed to update user profile: ${it.message}")
        })
    }
}