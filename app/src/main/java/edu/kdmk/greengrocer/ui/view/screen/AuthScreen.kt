package edu.kdmk.greengrocer.ui.view.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.R
import edu.kdmk.greengrocer.data.model.AuthUser
import edu.kdmk.greengrocer.data.repository.AuthRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.UserStorageRepository
import edu.kdmk.greengrocer.data.repository.UserDatabaseRepository
import edu.kdmk.greengrocer.ui.viewmodel.AuthViewModel
import edu.kdmk.greengrocer.ui.viewmodel.LoginState
import edu.kdmk.greengrocer.ui.viewmodel.RegistrationState

@Composable
fun AuthScreen() {
    val context = LocalContext.current

    val authRepository = remember { AuthRepository(FirebaseAuth.getInstance()) }
    val localStorageRepository = remember { LocalStorageRepository(context) }
    val userDatabaseRepository = remember { UserDatabaseRepository(Firebase) }
    val userStorageRepository = remember { UserStorageRepository(FirebaseStorage.getInstance()) }
    val authViewModel = remember { AuthViewModel(authRepository, localStorageRepository, userDatabaseRepository, userStorageRepository) }

    val loginState by authViewModel.loginState.collectAsState()

    when (loginState) {
        is LoginState.Success -> {
            Log.d("AuthScreen", "User is logged in")
            MainScreen()
        }
        else -> {
            AuthOptionsScreen(authViewModel)
        }
    }
}

@Composable
fun AuthOptionsScreen(authViewModel: AuthViewModel) {
    var isLoginScreen by remember { mutableStateOf(true) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoginScreen) {
                LoginScreen(authViewModel)
                //Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { isLoginScreen = false }) {
                    Text("Do not have an account? Sign up")
                }
            } else {
                RegisterScreen(authViewModel)
                //Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { isLoginScreen = true }) {
                    Text("Already have an account? Log in")
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Zmień na nazwę swojego zasobu
            contentDescription = "App Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.loginUser(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (loginState) {
            is LoginState.Loading -> CircularProgressIndicator()
            is LoginState.Error -> Text(
                (loginState as LoginState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            else -> {}
        }
    }
}

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fname by remember { mutableStateOf("") }
    var lname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val registrationState by authViewModel.registrationState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = fname,
            onValueChange = { fname = it },
            label = { Text("First name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = lname,
            onValueChange = { lname = it },
            label = { Text("Last name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.registerUser(
                    AuthUser(email = email, password = password, fname = fname, lname = lname, phone = phone)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (registrationState) {
            is RegistrationState.Loading -> CircularProgressIndicator()
            is RegistrationState.Success -> Text("Registration successful!", color = MaterialTheme.colorScheme.primary)
            is RegistrationState.Error -> Text(
                (registrationState as RegistrationState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            else -> {}
        }
    }
}
