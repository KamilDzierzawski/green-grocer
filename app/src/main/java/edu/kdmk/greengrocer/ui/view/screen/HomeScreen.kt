package edu.kdmk.greengrocer.ui.view.screen

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.data.model.Comment
import edu.kdmk.greengrocer.data.model.Plant
import edu.kdmk.greengrocer.data.model.Post
import edu.kdmk.greengrocer.data.repository.CommentDatabaseRepository
import edu.kdmk.greengrocer.data.repository.LikeDatabaseRepository
import edu.kdmk.greengrocer.data.repository.LocalStorageRepository
import edu.kdmk.greengrocer.data.repository.PlantDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PlantStorageRepository
import edu.kdmk.greengrocer.data.repository.PostDatabaseRepository
import edu.kdmk.greengrocer.data.repository.PostStorageRepository
import edu.kdmk.greengrocer.data.repository.UserDatabaseRepository
import edu.kdmk.greengrocer.data.repository.UserStorageRepository
import edu.kdmk.greengrocer.ui.viewmodel.GardenViewModel
import edu.kdmk.greengrocer.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val firebaseStorage = FirebaseStorage.getInstance()
    val firebase = Firebase

    val localStorageRepository = remember { LocalStorageRepository(context) }
    val plantStorageRepository = remember { PlantStorageRepository(firebaseStorage) }
    val plantDatabaseRepository = remember { PlantDatabaseRepository(firebase) }
    val postDatabaseRepository = remember { PostDatabaseRepository(firebase) }
    val postStorageRepository = remember { PostStorageRepository(firebaseStorage) }
    val userStorageRepository = remember { UserStorageRepository(firebaseStorage) }
    val userDatabaseRepository = remember { UserDatabaseRepository(firebase) }
    val likeDatabaseRepository = remember { LikeDatabaseRepository(firebase) }
    val commentDatabaseRepository = remember { CommentDatabaseRepository(firebase) }

    val homeViewModel = remember {
        HomeViewModel(
            localStorageRepository = localStorageRepository,
            plantDatabaseRepository = plantDatabaseRepository,
            plantStorageRepository = plantStorageRepository,
            postDatabaseRepository = postDatabaseRepository,
            postStorageRepository = postStorageRepository,
            userDatabaseRepository = userDatabaseRepository,
            userStorageRepository = userStorageRepository,
            likeDatabaseRepository = likeDatabaseRepository,
            commentDatabaseRepository = commentDatabaseRepository
        )
    }

    val posts by homeViewModel.posts.observeAsState(emptyList())
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var isCommentScreen by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }  // Stan przechowujący wybrany post

    LaunchedEffect(Unit) {
        homeViewModel.loadPosts()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isCommentScreen) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Posts") },
                        actions = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    homeViewModel.loadPosts()
                                    listState.animateScrollToItem(0)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Refresh Posts"
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                PostList(
                    posts = posts,
                    homeViewModel = homeViewModel,
                    listState = listState,
                    onCommentClicked = { post ->
                        selectedPost = post  // Ustawiamy wybrany post
                        isCommentScreen = true  // Przełączamy na ekran komentarzy
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            FloatingActionButton(
                onClick = { navController.navigate("addPost") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Post",
                    tint = Color.White
                )
            }

        } else {
            // Jeśli jest wybrany post, przekazujemy go do CommentsScreen
            selectedPost?.let { post ->
                CommentsScreen(
                    post = post,
                    onCommentClicked = { post ->
                        selectedPost = post  // Ustawiamy wybrany post
                        isCommentScreen = true  // Przełączamy na ekran komentarzy
                    },
                    onBackClicked = { isCommentScreen = false },
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}

@Composable
fun PostList(
    posts: List<Post>,
    homeViewModel: HomeViewModel,
    listState: LazyListState,
    onCommentClicked: (Post) -> Unit,  // Zmieniony typ argumentu na Post
    modifier: Modifier = Modifier
) {
    val sortedPosts = posts.sortedByDescending { it.timestamp?.toDate()?.time ?: 0L }

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(sortedPosts.size) { index ->
            PostItem(
                post = sortedPosts[index],
                homeViewModel = homeViewModel,
                onCommentClicked = onCommentClicked  // Przekazanie funkcji do PostItem
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    onCommentClicked: (Post) -> Unit,  // Zmieniony typ argumentu na Post
    homeViewModel: HomeViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp, 16.dp, 16.dp, 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(post.postUser?.image?.path ?: ""),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "${post.postUser?.fname ?: ""} ${post.postUser?.lname ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = post.timestamp?.toReadableTime() ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${post.title} - ${post.type}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            post.image?.let { image ->
                val imageUri = Uri.fromFile(File(image.path))

                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop  )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like Icon",

                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${post.likes?.size ?: 0}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }

                Text(
                    text = "Comments: ${post.comments?.size ?: 0}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    homeViewModel.toggleLike(post.id ?: "")
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Like Icon",
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Like It",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                TextButton(onClick = {
                    onCommentClicked(post)  // Przekazujemy post
                }) {
                    Text(
                        text = "Add Comment",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

fun Timestamp.toReadableTime(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(this.toDate())
}

@Composable
fun CommentsScreen(
    post: Post,  // Przekazujemy post, który będzie wyświetlany na tym ekranie
    onBackClicked: () -> Unit,
    onCommentClicked: (Post) -> Unit,
    homeViewModel: HomeViewModel // Przekazujemy ViewModel, aby dodać komentarz
) {
    // Przechowujemy lokalny stan postu, aby móc go modyfikować
    var localPost by remember { mutableStateOf(post) }

    // Sortowanie komentarzy
    val sortedComments = localPost.comments?.sortedBy { it.timestamp?.toDate()?.time } ?: emptyList()

    // Zmienna do przechowywania tekstu nowego komentarza
    var newComment by remember { mutableStateOf("") }

    // Scaffold - layout strukturalny z topBar
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
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Używamy Box, aby przyczepić TextField do góry
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Wyświetlanie komentarzy
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp) // Zwiększamy górne odstępy, by tekst nie nakładał się na TextField
                ) {
                    items(sortedComments) { comment ->
                        CommentItem(comment = comment)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // TextField do dodania komentarza przytwierdzone do góry
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    // Row z TextField i przyciskiem "Save"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        // Pole tekstowe do wpisania komentarza
                        TextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            label = { Text("Add a comment...") },
                            modifier = Modifier
                                .fillMaxWidth(0.85f) // Zmniejszamy szerokość, żeby zostało miejsce na przycisk
                                .padding(end = 8.dp), // Odstęp między TextField a przyciskiem
                            singleLine = false,
                            maxLines = 3
                        )

                        IconButton(
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    // Dodaj komentarz do bazy danych i zaktualizuj UI
                                    homeViewModel.addComment(
                                        postId = post.id ?: "",
                                        content = newComment,
                                    )
                                }
                                onBackClicked()
                                onCommentClicked(post)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Save Comment"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "${comment.firstName} ${comment.lastName}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            comment.timestamp?.let {
                Text(
                    text = it.toReadableTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val firebaseStorage = FirebaseStorage.getInstance()
    val firebase = Firebase

    val localStorageRepository = remember { LocalStorageRepository(context) }
    val plantStorageRepository = remember { PlantStorageRepository(firebaseStorage) }
    val plantDatabaseRepository = remember { PlantDatabaseRepository(firebase) }
    val postDatabaseRepository = remember { PostDatabaseRepository(firebase) }
    val postStorageRepository = remember { PostStorageRepository(firebaseStorage) }
    val userStorageRepository = remember { UserStorageRepository(firebaseStorage) }
    val userDatabaseRepository = remember { UserDatabaseRepository(firebase) }
    val likeDatabaseRepository = remember { LikeDatabaseRepository(firebase) }
    val commentDatabaseRepository = remember { CommentDatabaseRepository(firebase) }

    val homeViewModel = remember {
        HomeViewModel(
            localStorageRepository = localStorageRepository,
            plantDatabaseRepository = plantDatabaseRepository,
            plantStorageRepository = plantStorageRepository,
            postDatabaseRepository = postDatabaseRepository,
            postStorageRepository = postStorageRepository,
            userDatabaseRepository = userDatabaseRepository,
            userStorageRepository = userStorageRepository,
            likeDatabaseRepository = likeDatabaseRepository,
            commentDatabaseRepository = commentDatabaseRepository
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


