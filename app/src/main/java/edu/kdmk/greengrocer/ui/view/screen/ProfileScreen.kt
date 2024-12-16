package edu.kdmk.greengrocer.ui.view.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.data.repository.AuthRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.StorageRepository
import edu.kdmk.greengrocer.data.repository.UserRepository
import edu.kdmk.greengrocer.ui.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current

    val authRepository = remember { AuthRepository(FirebaseAuth.getInstance()) }
    val localStorageRepository = remember { LocalStorageRepository(context) }
    val storageRepository = remember { StorageRepository(FirebaseStorage.getInstance()) }
    val userRepository = remember { UserRepository(Firebase) }
    val profileViewModel = remember { ProfileViewModel(authRepository, localStorageRepository, storageRepository, userRepository) }

    val userProfileImage by profileViewModel.userProfileImage.observeAsState()
    val isImageLoading by profileViewModel.isImageLoading.observeAsState(false)
    var isDialogOpen by remember { mutableStateOf(false) }
    var isEditDialogOpen by remember { mutableStateOf(false) }

    val userData = profileViewModel.getUserData()

    // Editable fields
    var fname by remember { mutableStateOf(userData?.fname ?: "") }
    var lname by remember { mutableStateOf(userData?.lname ?: "") }
    var phone by remember { mutableStateOf(userData?.phone ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val imageFile = File(context.cacheDir, "profile_image.jpg")
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val outputStream = FileOutputStream(imageFile)
                    inputStream.copyTo(outputStream)
                    profileViewModel.uploadUserProfileImage(userData?.id ?: "", imageFile, {
                        Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    }, { exception ->
                        Toast.makeText(context, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isImageLoading) {
            CircularProgressIndicator()
        } else {
            userProfileImage?.let { imageFile ->
                Image(
                    painter = rememberAsyncImagePainter(model = imageFile),
                    contentDescription = "User profile image",
                    modifier = Modifier.size(150.dp)
                )
            } ?: run {
                Text("No profile image available")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                isDialogOpen = true
            }) {
                Text("Edit Profile Image")
            }

            if (isDialogOpen) {
                LaunchedEffect(isDialogOpen) {
                    imagePickerLauncher.launch("image/*")
                    isDialogOpen = false
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display user data (excluding password)
            userData?.let {
                Text("First Name: $fname")
                Text("Last Name: $lname")
                Text("Phone: $phone")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                isEditDialogOpen = true
            }) {
                Text("Edit Profile")
            }

            // Edit dialog
            if (isEditDialogOpen) {
                EditProfileDialog(
                    fname = fname,
                    lname = lname,
                    phone = phone,
                    onSave = {
                        // Ensure you are updating the correct user data
                        val updatedUser = userData?.copy(
                            fname = fname,
                            lname = lname,
                            phone = phone
                        )

                        if (updatedUser != null) {
                            profileViewModel.updateUserProfile(updatedUser)
                        }

                        isEditDialogOpen = false
                    },
                    onCancel = {
                        // Cancel the edit dialog
                        isEditDialogOpen = false
                    },
                    onFnameChange = { fname = it },
                    onLnameChange = { lname = it },
                    onPhoneChange = { phone = it }
                )
            }

            Button(
                onClick = {
                    profileViewModel.clearUserData()
                    onLogout()
                }
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    fname: String,
    lname: String,
    phone: String,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onFnameChange: (String) -> Unit,
    onLnameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Edit Profile") },
        text = {
            Column {
                TextField(
                    value = fname,
                    onValueChange = onFnameChange,
                    label = { Text("First Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = lname,
                    onValueChange = onLnameChange,
                    label = { Text("Last Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}