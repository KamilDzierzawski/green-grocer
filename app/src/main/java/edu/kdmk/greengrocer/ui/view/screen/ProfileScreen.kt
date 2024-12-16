package edu.kdmk.greengrocer.ui.view.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var isImageMenuOpen by remember { mutableStateOf(false) }
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
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Section 1: Profile image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                if (isImageLoading) {
                    CircularProgressIndicator()
                } else {
                    userProfileImage?.let { imageFile ->
                        Image(
                            painter = rememberAsyncImagePainter(model = imageFile),
                            contentDescription = "User profile image",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    } ?: run {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default profile icon",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            tint = Color.Gray
                        )
                    }
                }
            }

            IconButton(
                onClick = { isImageMenuOpen = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Image options"
                )
            }

            DropdownMenu(
                expanded = isImageMenuOpen,
                onDismissRequest = { isImageMenuOpen = false },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                DropdownMenuItem(
                    text = { Text("Edit Profile Image", fontSize = 16.sp) },
                    onClick = {
                        isImageMenuOpen = false
                        imagePickerLauncher.launch("image/*")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete Profile Image", fontSize = 16.sp) },
                    onClick = {
                        isImageMenuOpen = false
                        profileViewModel.deleteUserProfileImage(userData?.id ?: "")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            thickness = 1.dp,
            color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Section 2: User data
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserDataField(label = "ID", value = userData?.id ?: "N/A")
                UserDataField(label = "Email", value = userData?.email ?: "N/A")
                UserDataField(label = "First Name", value = fname)
                UserDataField(label = "Last Name", value = lname)
                UserDataField(label = "Phone", value = phone)
            }

            IconButton(
                onClick = { isEditDialogOpen = true },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit user data"
                )
            }

            // Edit dialog
            if (isEditDialogOpen) {
                EditProfileDialog(
                    fname = fname,
                    lname = lname,
                    phone = phone,
                    onSave = {
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
                        isEditDialogOpen = false
                    },
                    onFnameChange = { fname = it },
                    onLnameChange = { lname = it },
                    onPhoneChange = { phone = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            thickness = 1.dp,
            color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Logout button
        Button(
            onClick = {
                profileViewModel.clearUserData()
                onLogout()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Logout")
        }
    }
}

@Composable
fun UserDataField(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
            //color = Color.Gray,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            //color = Color.Black
        )
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