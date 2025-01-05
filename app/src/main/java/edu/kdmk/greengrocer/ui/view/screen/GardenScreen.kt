package edu.kdmk.greengrocer.ui.view.screen

import android.content.Context
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.data.model.Plant
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.PlantDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PlantStorageRepository
import edu.kdmk.greengrocer.ui.view.navigation.NavigationItem
import edu.kdmk.greengrocer.ui.viewmodel.GardenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun GardenScreen(navController: NavController) {
    val context = LocalContext.current
    val localStorageRepository = remember { LocalStorageRepository(context) }
    val plantStorageRepository = remember { PlantStorageRepository(FirebaseStorage.getInstance()) }
    val plantDatabaseRepository = remember { PlantDatabaseRepository(Firebase) }

    val gardenViewModel = remember {
        GardenViewModel(
            localStorageRepository = localStorageRepository,
            plantDatabaseRepository = plantDatabaseRepository,
            plantStorageRepository = plantStorageRepository
        )
    }

    var isListScreen by remember { mutableStateOf(true) }

    val plants by gardenViewModel.plants.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        gardenViewModel.loadPlants()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.background(Color.White)
    ) {
        if (isListScreen) {
            PlantList(
                plants = plants,
                gardenViewModel = gardenViewModel,
                onEditClicked = { isListScreen = false } // Update state here
            )
        } else {
            EditGardenItemScreen(
                gardenViewModel = gardenViewModel,
                context = context,
                onBackClicked = { isListScreen = true }
            )
        }

        if (isListScreen) {
            FloatingActionButton(
                onClick = {
                    navController.navigate("addGardenItem")
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
        }
    }
}

@Composable
fun PlantList(
    plants: List<Plant>,
    gardenViewModel: GardenViewModel,
    onEditClicked: () -> Unit
) {
    Column {
        IconButton(
            onClick = {
                gardenViewModel.loadPlants()
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh",
                tint = Color.Black
            )
        }

        LazyColumn {
            items(plants) { plant ->
                PlantItem(
                    plant = plant,
                    gardenViewModel = gardenViewModel,
                    onEditClicked = onEditClicked
                )
            }
        }
    }
}


@Composable
fun PlantItem(
    plant: Plant,
    gardenViewModel: GardenViewModel,
    onEditClicked: () -> Unit // Callback for editing
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
            .height(100.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    //.padding(4.dp) // Wyrównanie odstępu zdjęcia
            ) {
                AsyncImage(
                    model = plant.image,
                    contentDescription = "Plant Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)), // Zaokrąglenie rogów zdjęcia
                        //.border(1.dp, Color.Gray, RoundedCornerShape(12.dp)), // Ramka wokół zdjęcia
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = plant.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f), // Wypełnienie przestrzeni dla nazwy
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Większy odstęp między ikonami

                    IconButton(
                        onClick = {
                            gardenViewModel.setSelectedPlant(plant)
                            onEditClicked()
                            //navController.navigate("editGardenItem")
                        },
                        modifier = Modifier.size(24.dp) // Rozmiar przycisku dopasowany do stylu Material
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz, // Pozioma wersja trzech kropek
                            contentDescription = "Edit Options"
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp)) // Większy odstęp między ikonami

                    IconButton(
                        onClick = {
                            plant.id?.let { gardenViewModel.deletePlantFromGarden(it) }
                            gardenViewModel.loadPlants()
                                  },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete"
                        )
                    }
                }

                Text(
                    text = plant.species,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp) // Blisko pod nazwą
                )
            }
        }
    }
}

@Composable
fun EditGardenItemScreen(
    gardenViewModel: GardenViewModel,
    context: Context,
    onBackClicked: () -> Unit
) {

    val selectedPlant by gardenViewModel.selectedPlant.observeAsState(Plant())

    var name by remember { mutableStateOf(selectedPlant.name) }
    var description by remember { mutableStateOf(selectedPlant.description) }
    var species by remember { mutableStateOf(selectedPlant.species) }
    var imageUri by remember { mutableStateOf(selectedPlant.image?.toUri()) }
    var isImageChanged by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            isImageChanged = true
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            onBackClicked()
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Back",
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            val hasChanged = name != selectedPlant.name ||
                                    description != selectedPlant.description ||
                                    species != selectedPlant.species ||
                                    isImageChanged

                            if (hasChanged) {
                                gardenViewModel.updatePlant(
                                    plant = selectedPlant.copy(
                                        name = name,
                                        description = description,
                                        species = species,
                                        image = if (isImageChanged) {
                                            imageUri?.let { uri ->
                                                val file = File(context.cacheDir, "temp_image")
                                                val inputStream = context.contentResolver.openInputStream(uri)
                                                val outputStream = FileOutputStream(file)
                                                inputStream?.copyTo(outputStream)
                                                file
                                            }
                                        } else {
                                            selectedPlant.image
                                        }
                                    )
                                )
                            }
                            onBackClicked()
                            gardenViewModel.loadPlants()
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp, 8.dp)
        ) {
            Text(
                text = "Edit Plant",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pole tekstowe dla nazwy
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pole tekstowe dla gatunku
            TextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Species") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pole tekstowe dla opisu
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Wyświetlanie zdjęcia z możliwością zmiany
            Box(
                modifier = Modifier
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .border(1.dp, Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image Selected",
                            color = Color.Gray
                        )
                    }
                } else {

                    /*AsyncImage(
                    model = plant.image,
                    contentDescription = "Plant Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)), // Zaokrąglenie rogów zdjęcia
                        //.border(1.dp, Color.Gray, RoundedCornerShape(12.dp)), // Ramka wokół zdjęcia
                    contentScale = ContentScale.Crop
                )*/
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .border(1.dp, Color.Gray),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wyświetlanie znacznika czasu
            Text(
                text = "Added: ${selectedPlant.timestamp?.toDate()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AddGardenItemScreen(navController: NavController) {
    val context = LocalContext.current
    val localStorageRepository = remember { LocalStorageRepository(context) }
    val plantStorageRepository = remember { PlantStorageRepository(FirebaseStorage.getInstance()) }
    val plantDatabaseRepository = remember { PlantDatabaseRepository(Firebase) }

    val gardenViewModel = remember {
        GardenViewModel(
            localStorageRepository = localStorageRepository,
            plantDatabaseRepository = plantDatabaseRepository,
            plantStorageRepository = plantStorageRepository
        )
    }

    var plantName by remember { mutableStateOf("") }
    var plantDescription by remember { mutableStateOf("") }
    var plantSpecies by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isNameError by remember { mutableStateOf(false) }
    var isDescriptionError by remember { mutableStateOf(false) }
    var isSpeciesError by remember { mutableStateOf(false) }
    var isImageError by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        isImageError = false
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                        .clip(RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Back",
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            // Validate inputs
                            isNameError = plantName.isEmpty()
                            isDescriptionError = plantDescription.isEmpty()
                            isSpeciesError = plantSpecies.isEmpty()
                            isImageError = selectedImageUri == null

                            if (!isNameError && !isDescriptionError && !isImageError) {
                                gardenViewModel.addPlantToGarden(
                                    name = plantName,
                                    description = plantDescription,
                                    species = plantSpecies,
                                    file = selectedImageUri?.let { uri ->
                                        val file = File(context.cacheDir, "temp_image")
                                        val inputStream = context.contentResolver.openInputStream(uri)
                                        val outputStream = FileOutputStream(file)
                                        inputStream?.copyTo(outputStream)
                                        file
                                    }
                                )
                                navController.popBackStack()
                            }
                        }
                        .clip(RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp, 8.dp)
        ) {
            Text(
                text = "Add Your Plant",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = plantName,
                onValueChange = {
                    plantName = it
                    isNameError = false
                },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isNameError) 2.dp else 0.dp,
                        color = if (isNameError) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isNameError) {
                Text("Name is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = plantSpecies,
                onValueChange = {
                    plantSpecies = it
                    isSpeciesError = false
                },
                label = { Text("Species") },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSpeciesError) 2.dp else 0.dp,
                        color = if (isSpeciesError) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isSpeciesError) {
                Text("Species is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = plantDescription,
                onValueChange = {
                    plantDescription = it
                    isDescriptionError = false
                },
                label = { Text("Description") },
                modifier = Modifier
                    .heightIn(min = 100.dp)
                    .fillMaxWidth()
                    .border(
                        width = if (isDescriptionError) 2.dp else 0.dp,
                        color = if (isDescriptionError) Color.Red else Color.Transparent
                    ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isDescriptionError) {
                Text("Description is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .border(
                        width = if (isImageError) 2.dp else 0.dp,
                        color = if (isImageError) Color.Red else Color.Transparent,
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(2.dp, Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select Image",
                            color = Color.Gray
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = selectedImageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .border(2.dp, Color.Gray),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            if (isImageError) {
                Text("Image is required", color = Color.Red, fontSize = 12.sp)
            }
        }
    }
}






