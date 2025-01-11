package edu.kdmk.greengrocer.ui.view.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.data.model.Plant
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.PlantDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PlantStorageRepository
import edu.kdmk.greengrocer.ui.viewmodel.GardenViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun GardenScreen(
    navController: NavController,
) {
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
        } else {
            EditGardenItemScreen(
                gardenViewModel = gardenViewModel,
                context = context,
                onBackClicked = { isListScreen = true }
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {
            Text(
                text = "Your plants",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                //color = Color.Black,
                modifier = Modifier.align(Alignment.Center) // Wyśrodkowanie napisu w Box
            )

            IconButton(
                onClick = {
                    gardenViewModel.loadPlants()
                },
                modifier = Modifier.align(Alignment.CenterEnd) // Wyrównanie do prawej
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    //tint = Color.Black
                )
            }
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
                                                val inputStream =
                                                    context.contentResolver.openInputStream(uri)
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

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Species") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            Text(
                text = "Added: ${selectedPlant.timestamp?.toDate()}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Your Plant",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold, // Pogrubienie tekstu
                            fontSize = 18.sp            // Dopasowany rozmiar
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Walidacja danych wejściowych
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
                                        val inputStream =
                                            context.contentResolver.openInputStream(uri)
                                        val outputStream = FileOutputStream(file)
                                        inputStream?.copyTo(outputStream)
                                        file
                                    }
                                )
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save"
                        )
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

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = plantName,
                onValueChange = {
                    plantName = it
                    isNameError = false
                },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = isNameError
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
                    .fillMaxWidth(),
                isError = isSpeciesError
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
                    .fillMaxWidth(),
                maxLines = 5,
                isError = isDescriptionError
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isDescriptionError) {
                Text("Description is required", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .clickable { imagePickerLauncher.launch("image/*") },
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
                            color = if (isImageError) Color.Red else Color.Gray
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






