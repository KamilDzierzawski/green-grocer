package edu.kdmk.greengrocer.ui.view.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import edu.kdmk.greengrocer.data.model.AuthUser
import edu.kdmk.greengrocer.data.repository.AuthRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.UserRepository
import edu.kdmk.greengrocer.ui.viewmodel.AuthViewModel
import edu.kdmk.greengrocer.ui.viewmodel.LoginState
import edu.kdmk.greengrocer.ui.viewmodel.RegistrationState

@Composable
fun AuthScreen() {
    val context = LocalContext.current

    val authRepository = remember { AuthRepository(FirebaseAuth.getInstance()) }
    val localStorageRepository = remember { LocalStorageRepository(context) }
    val userRepository = remember { UserRepository(Firebase) }
    val authViewModel = remember { AuthViewModel(authRepository, localStorageRepository, userRepository) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, please log in or register")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoginScreen = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoginScreen = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoginScreen) {
            LoginScreen(authViewModel)
        } else {
            RegisterScreen(authViewModel)
        }
    }
}

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            Text("Log In")
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
fun RegisterScreen(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fname by remember { mutableStateOf("") }
    var lname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val registrationState by authViewModel.registrationState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = lname,
            onValueChange = { lname = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
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


//@Composable
//fun AuthScreen() {
//
//    val context = LocalContext.current
//
//    val authRepository = remember { AuthRepository(FirebaseAuth.getInstance(), Firebase) }
//    val localStorageRepository = remember { LocalStorageRepository(context) }
//
//    val authViewModel = remember { AuthViewModel(authRepository, localStorageRepository) }
//
//
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var fname by remember { mutableStateOf("") }
//    var lname by remember { mutableStateOf("") }
//    var phone by remember { mutableStateOf("") }
//
//    val registrationState by authViewModel.registrationState.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        TextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        TextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        TextField(
//            value = fname,
//            onValueChange = { fname = it },
//            label = { Text("First Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        TextField(
//            value = lname,
//            onValueChange = { lname = it },
//            label = { Text("Last Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        TextField(
//            value = phone,
//            onValueChange = { phone = it },
//            label = { Text("Phone Number") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                authViewModel.registerUser(
//                    AuthUser(
//                        email = email,
//                        password = password,
//                        fname = fname,
//                        lname = lname,
//                        phone = phone
//                    )
//                )
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Register")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        when (registrationState) {
//            is RegistrationState.Loading -> CircularProgressIndicator()
//            is RegistrationState.Success -> Text("Registration successful!", color = MaterialTheme.colorScheme.primary)
//            is RegistrationState.Error -> Text(
//                (registrationState as RegistrationState.Error).message,
//                color = MaterialTheme.colorScheme.error
//            )
//            else -> {}
//        }
//    }
//}