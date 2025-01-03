package edu.kdmk.greengrocer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import edu.kdmk.greengrocer.data.model.AuthUser
import edu.kdmk.greengrocer.data.repository.AuthRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.UserStorageRepository
import edu.kdmk.greengrocer.data.repository.UserDatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val localStorageRepository: LocalStorageRepository,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val storageRepository: UserStorageRepository
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> get() = _registrationState

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    init {
        checkUserLogin()
    }

    fun checkUserLogin() {
        val user = localStorageRepository.getUserData()
        if (user != null) {
            _loginState.value = LoginState.Success
        } else {
            _loginState.value = LoginState.Idle
        }
    }

    fun registerUser(authUser: AuthUser) {
        _registrationState.value = RegistrationState.Loading

        authRepository.registerUser(
            authUser,
            onSuccess = { updatedAuthUser ->
                userDatabaseRepository.addUserToDatabase(updatedAuthUser,
                    onSuccess = {
                        localStorageRepository.saveUserData(updatedAuthUser)
                        _registrationState.value = RegistrationState.Success
                        _loginState.value = LoginState.Success
                    },
                    onFailure = { exception ->
                        _registrationState.value = RegistrationState.Error(exception.message ?: "Firestore Error")
                    }
                )
            },
            onFailure = { exception ->
                _registrationState.value = RegistrationState.Error(exception.message ?: "Registration failed")
            }
        )
    }

    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        authRepository.loginUser(
            email, password,
            onSuccess = { authUser ->
                userDatabaseRepository.getUserFromDatabase(
                    authUser,
                    onSuccess = { updatedUser ->
                        localStorageRepository.saveUserData(updatedUser)
                        _loginState.value = LoginState.Success
                    },
                    onFailure = { exception ->
                        _loginState.value = LoginState.Error(exception.message ?: "Failed to fetch user data")
                    }
                )

                storageRepository.downloadUserProfileImage(
                    userId = authUser.id!!,
                    onSuccess = { tempFile ->
                        localStorageRepository.saveUserProfileImage(tempFile)
                        tempFile.delete()
                        Log.d("ProfileViewModel", "Profile image downloaded and saved locally")
                    },
                    onFailure = { exception ->
                        _loginState.value = LoginState.Success
                    }
                )
            },
            onFailure = { exception ->
                _loginState.value = LoginState.Error(exception.message ?: "Login failed")
            }
        )
    }
}
