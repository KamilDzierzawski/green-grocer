package edu.kdmk.greengrocer.ui.view.screen

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import edu.kdmk.greengrocer.data.model.Comment
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
import edu.kdmk.greengrocer.ui.viewmodel.SearchViewModel
import java.io.File

@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val firebaseStorage = FirebaseStorage.getInstance()
    val firebase = Firebase

    val localStorageRepository = remember { LocalStorageRepository(context) }
    val postDatabaseRepository = remember { PostDatabaseRepository(firebase) }
    val postStorageRepository = remember { PostStorageRepository(firebaseStorage) }
    val userStorageRepository = remember { UserStorageRepository(firebaseStorage) }
    val userDatabaseRepository = remember { UserDatabaseRepository(firebase) }
    val likeDatabaseRepository = remember { LikeDatabaseRepository(firebase) }
    val commentDatabaseRepository = remember { CommentDatabaseRepository(firebase) }

    val searchViewModel = remember {
        SearchViewModel(
            localStorageRepository,
            postDatabaseRepository,
            postStorageRepository,
            userDatabaseRepository,
            userStorageRepository,
            likeDatabaseRepository,
            commentDatabaseRepository
        )
    }

    val posts by searchViewModel.posts.observeAsState(emptyList())
    val loading by searchViewModel.loading.observeAsState(false)

    var searchText by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("") }
    val tagOptions = listOf("Need Help", "Event", "Question")

    var isCommentScreen by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }

    val listState = rememberLazyListState()

    val textFieldHeight = 56.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isCommentScreen) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search...") },
                        modifier = Modifier
                            .weight(1f)
                            .height(textFieldHeight)
                            .padding(end = 8.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                    )

                    Box(
                        modifier = Modifier
                            .height(textFieldHeight)
                            .padding(end = 8.dp)
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        TextField(
                            value = selectedTag,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tag") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown Icon",
                                    modifier = Modifier.clickable { expanded = !expanded }
                                )
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .height(textFieldHeight),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            tagOptions.forEach { tag ->
                                DropdownMenuItem(
                                    text = { Text(tag) },
                                    onClick = {
                                        selectedTag = tag
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (searchText.isNotBlank() || selectedTag.isNotBlank()) {
                                searchViewModel.loadPosts(searchText, selectedTag)
                            }
                        },
                        modifier = Modifier.height(textFieldHeight)
                    ) {
                        Text("Search")
                    }
                }

                if (loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    PostSearchList(
                        posts = posts,
                        searchViewModel = searchViewModel,
                        listState = listState,
                        onCommentClicked = { post ->
                            selectedPost = post  // Ustawiamy wybrany post
                            isCommentScreen = true  // Przełączamy na ekran komentarzy
                        }
                    )
                }
            }
        } else {
            // Jeśli jest wybrany post, przekazujemy go do CommentsScreen
            selectedPost?.let { post ->
                CommentsSearchScreen(
                    post = post,
                    onBackClicked = { isCommentScreen = false },
                    searchViewModel = searchViewModel
                )
            }
        }
    }
}

@Composable
fun PostSearchList(
    posts: List<Post>,
    searchViewModel: SearchViewModel,
    listState: LazyListState,
    onCommentClicked: (Post) -> Unit,  // Zmieniony typ argumentu na Post
    modifier: Modifier = Modifier
) {
    val sortedPosts = posts.sortedByDescending { it.timestamp?.toDate()?.time ?: 0L }


    if (sortedPosts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No posts to display")
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = modifier
        ) {
            items(sortedPosts.size) { index ->
                PostSearchItem(
                    post = sortedPosts[index],
                    searchViewModel = searchViewModel,
                    onCommentClicked = onCommentClicked  // Przekazanie funkcji do PostItem
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PostSearchItem(
    post: Post,
    onCommentClicked: (Post) -> Unit,  // Zmieniony typ argumentu na Post
    searchViewModel: SearchViewModel
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
                    contentDescription = "User Image",
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
                    contentDescription = "Post Image",
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
                    searchViewModel.toggleLike(post.id ?: "")
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSearchScreen(
    post: Post,
    onBackClicked: () -> Unit,
    searchViewModel: SearchViewModel
) {
    val comments by searchViewModel.comments.observeAsState(emptyList())

    LaunchedEffect(post.id) {
        searchViewModel.loadComments(post.id ?: "")
    }

    val sortedComments = comments.sortedByDescending { it.timestamp?.toDate()?.time } ?: emptyList()

    var newComment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Comments",
                        style = androidx.compose.ui.text.TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onBackClicked() },
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
                            searchViewModel.loadComments(post.id ?: "")
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh Comments"
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    label = { Text("Add a comment...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = false,
                    maxLines = 3,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                )

                IconButton(
                    onClick = {
                        if (newComment.isNotBlank()) {
                            searchViewModel.addComment(
                                postId = post.id ?: "",
                                content = newComment,
                            )
                            newComment = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save Comment"
                    )
                }
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(sortedComments) { comment ->
                    CommentSearchItem(comment = comment)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CommentSearchItem(
    comment: Comment
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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