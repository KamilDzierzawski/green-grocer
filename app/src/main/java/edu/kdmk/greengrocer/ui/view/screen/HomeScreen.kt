package edu.kdmk.greengrocer.ui.view.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.data.model.Plant
import edu.kdmk.greengrocer.data.model.Post
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.PlantDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PlantStorageRepository
import edu.kdmk.greengrocer.ui.viewmodel.GardenViewModel
import edu.kdmk.greengrocer.ui.viewmodel.HomeViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
        //.background(Color.White)
    ) {
//        if (isListScreen) {
//            PlantList(
//                plants = plants,
//                gardenViewModel = gardenViewModel,
//                onEditClicked = { isListScreen = false } // Update state here
//            )
//        } else {
//            EditGardenItemScreen(
//                gardenViewModel = gardenViewModel,
//                context = context,
//                onBackClicked = { isListScreen = true }
//            )
//        }

        //if (isListScreen) {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addPost")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Garden Item"
                    )
                }
            )
       // }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val localStorageRepository = remember { LocalStorageRepository(context) }
    val plantStorageRepository = remember { PlantStorageRepository(FirebaseStorage.getInstance()) }
    val plantDatabaseRepository = remember { PlantDatabaseRepository(Firebase) }

    val homeViewModel = remember {
        HomeViewModel(
            localStorageRepository = localStorageRepository,
            plantDatabaseRepository = plantDatabaseRepository,
            plantStorageRepository = plantStorageRepository
        )
    }

    val plants by homeViewModel.plants.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        homeViewModel.loadPlants()
    }

    var selectedType by remember { mutableStateOf("Need Help") }
    val typeOptions = listOf("Need Help", "Event", "Question")

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPlant by remember { mutableStateOf<Plant?>(null) }

    var isTitleError by remember { mutableStateOf(false) }
    var isDescriptionError by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Post",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Walidacja
                            isTitleError = title.isEmpty()
                            isDescriptionError = description.isEmpty()

                            if (!isTitleError && !isDescriptionError) {
                                homeViewModel.addPost(
                                    type = selectedType,
                                    title = title,
                                    description = description,
                                    image = selectedImageUri?.let { uri ->
                                        val file = File(context.cacheDir, "image")
                                        val inputStream = context.contentResolver.openInputStream(uri)
                                        val outputStream = FileOutputStream(file)
                                        inputStream?.copyTo(outputStream)
                                        file
                                    },
                                    timestamp = Timestamp.now()
                                )
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp, 8.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            DropdownMenuBox(
                label = "Plant (optional)",
                options = plants.map { "${it.name} (${it.species})" },
                selectedOption = selectedPlant?.let { "${it.name} (${it.species})" } ?: "Select Plant",
                onOptionSelected = { selectedOption ->
                    val selected = plants.find { "${it.name} (${it.species})" == selectedOption }
                    selectedPlant = selected
                    if (selected != null) {
                        title = selected.species
                        selected.image?.let {
                            selectedImageUri = Uri.fromFile(it)
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            DropdownMenuBox(
                label = "Type *",
                options = typeOptions,
                selectedOption = selectedType,
                onOptionSelected = { selectedType = it }
            )

            Spacer(Modifier.height(16.dp))

            // Tytuł
            TextField(
                value = title,
                onValueChange = { title = it; isTitleError = false },
                label = { Text("Title *") },
                isError = isTitleError,
                modifier = Modifier.fillMaxWidth()
            )
            if (isTitleError) {
                Text("Title is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            // Opis
            TextField(
                value = description,
                onValueChange = { description = it; isDescriptionError = false },
                label = { Text("Description *") },
                isError = isDescriptionError,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )
            if (isDescriptionError) {
                Text("Description is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clickable { if (selectedImageUri == null) imagePickerLauncher.launch("image/*") }
                    .border(
                        width = if (selectedImageUri == null) 2.dp else 0.dp,
                        color = if (selectedImageUri == null) Color.Gray else Color.Transparent
                        //shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth(), // Usunięto sztywną wysokość
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Wysokość okna dla pustego stanu
                            .border(2.dp, Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select Image",
                            color = Color.Gray
                        )
                    }
                } else {
                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(model = selectedImageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f) // Automatyczne dopasowanie wysokości na podstawie proporcji obrazu
                                .border(2.dp, Color.Gray),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .background(Color.White, shape = CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove Image")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownMenuBox(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


