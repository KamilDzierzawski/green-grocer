package edu.kdmk.greengrocer.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import edu.kdmk.greengrocer.data.model.AuthUser
import edu.kdmk.greengrocer.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository(FirebaseAuth.getInstance(), Firebase)

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> get() = _registrationState

    fun registerUser(authUser: AuthUser) {
        _registrationState.value = RegistrationState.Loading
        authRepository.registerUser(
            authUser,
            onSuccess = {
                _registrationState.value = RegistrationState.Success
            },
            onFailure = { exception ->
                _registrationState.value = RegistrationState.Error(exception.message ?: "Unknown error")
            }
        )
    }
}
