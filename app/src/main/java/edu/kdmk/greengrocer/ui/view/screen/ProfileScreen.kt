package edu.kdmk.greengrocer.ui.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import edu.kdmk.greengrocer.data.repository.AuthRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current

    val authRepository = remember { AuthRepository(FirebaseAuth.getInstance()) }
    val localStorageRepository = remember { LocalStorageRepository(context) }
    val profileViewModel = remember { ProfileViewModel(authRepository, localStorageRepository) }

    val userData = profileViewModel.getUserData()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Screen",
            fontSize = 40.sp,
            color = Color.Black
        )
        Text(
            text = "User data: $userData",
            fontSize = 20.sp,
            color = Color.Black
        )
    }

    Button(
        onClick = {
            profileViewModel.clearUserData()
            onLogout()
        }) {
        Text("Clear user data")
    }

}