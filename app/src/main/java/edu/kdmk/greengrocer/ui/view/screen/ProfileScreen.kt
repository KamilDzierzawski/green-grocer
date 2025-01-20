package edu.kdmk.greengrocer.ui.view.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.UserStorageRepository
import edu.kdmk.greengrocer.data.repository.UserDatabaseRepository
import edu.kdmk.greengrocer.ui.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val context = LocalContext.current

    val localStorageRepository = remember { LocalStorageRepository(context) }
    val userStorageRepository = remember { UserStorageRepository(FirebaseStorage.getInstance()) }
    val userDatabaseRepository = remember { UserDatabaseRepository(Firebase) }
    val profileViewModel = remember { ProfileViewModel(localStorageRepository, userStorageRepository, userDatabaseRepository) }

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
            //.background(Color.White)
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
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
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
            // Kolumna do przechowywania informacji o użytkowniku
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Odstępy między elementami
            ) {
                // Imię i nazwisko w jednej linii
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${userData?.fname ?: "First Name"} ${userData?.lname ?: "Last Name"}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    IconButton(
                        onClick = { isEditDialogOpen = true }
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

                // Szczegóły użytkownika (telefon i email)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Odstępy między elementami
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email, // Ikona email
                            contentDescription = "Email icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = userData?.email ?: "N/A",
                            )
                            Text(
                                text = "Email",
                                color = Color.Gray
                            )
                        }
                    }

                    // Telefon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone, // Ikona telefonu
                            contentDescription = "Phone icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = userData?.phone ?: "N/A",
                                //style = MaterialTheme.typography.body1
                            )
                            Text(
                                text = "Phone",
                                //fontSize = 20.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
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