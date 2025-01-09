package edu.kdmk.greengrocer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.auth.User
import edu.kdmk.greengrocer.data.model.AuthUser
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
import java.io.File

class HomeViewModel(
    private val localStorageRepository: LocalStorageRepository,
    private val plantDatabaseRepository: PlantDatabaseRepository,
    private val plantStorageRepository: PlantStorageRepository,
    private val postDatabaseRepository: PostDatabaseRepository,
    private val postStorageRepository: PostStorageRepository,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val userStorageRepository: UserStorageRepository,
    private val likeDatabaseRepository: LikeDatabaseRepository,
    private val commentDatabaseRepository: CommentDatabaseRepository
) {

    private val _plants = MutableLiveData<List<Plant>>()
    val plants: LiveData<List<Plant>> get() = _plants

//    private val _currentUser = MutableLiveData<AuthUser>();
//    val currentUser: LiveData<AuthUser>? get() = _currentUser.value

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    var currentUser = localStorageRepository.getUserData()

    fun loadPlants() {
        getPlantsFromGarden { plantList ->
            _plants.postValue(plantList)
        }
    }

//    fun loadCurrentUser() {
//        _currentUser.postValue(localStorageRepository.getUserData())
//    }


    private fun getPlantsFromGarden(
        onSuccess: (List<Plant>) -> Unit,
    ) {
        plantDatabaseRepository.getPlants(
            userId = localStorageRepository.getUserData()?.id ?: "",
            onSuccess = { plants ->
                val updatedPlants = mutableListOf<Plant>()
                var processedCount = 0

                // Funkcja pomocnicza do zakończenia operacji
                fun checkCompletion() {
                    if (processedCount == plants.size) {
                        onSuccess(updatedPlants)
                    }
                }

                plants.forEach { plant ->
                    plantStorageRepository.getPlantImage(
                        plant,
                        onSuccess = { image ->
                            updatedPlants.add(plant.copy(image = image))
                            processedCount++
                            checkCompletion()
                        },
                        onFailure = { exception ->
                            Log.e("GardenViewModel", "Failed to retrieve plant image for ${plant.id}: ${exception.message}")
                            processedCount++
                            checkCompletion()
                        }
                    )
                }

                if (plants.isEmpty()) {
                    onSuccess(emptyList())
                }
            },
            onFailure = { exception ->
                Log.e("GardenViewModel", "Failed to retrieve plants from garden: ${exception.message}")
            }
        )
    }

    fun addPost(
        type: String,
        title: String,
        description: String,
        image: File?,
        timestamp: Timestamp
    ) {
        val post = Post(
            userId = currentUser?.id,
            type = type,
            title = title,
            description = description,
            image = image,
            timestamp = timestamp
        )

        postDatabaseRepository.addPost(
            post,
            onSuccess = { updatedPost ->
                if (image != null) {
                    postStorageRepository.addPostImage(
                        post = updatedPost,
                        onSuccess = {
                            Log.d("HomeViewModel", "Post added successfully")
                        },
                        onFailure = { exception ->
                            Log.e("HomeViewModel", "Failed to add post image: ${exception.message}")
                        }
                    )
                }
            },
            onFailure = { exception ->
                Log.e("HomeViewModel", "Failed to add post: ${exception.message}")
            }
        )
    }

    fun loadPosts() {
        getPosts { postList ->
            _posts.postValue(postList)
        }
    }

    private fun getPosts(
        onSuccess: (List<Post>) -> Unit
    ) {
        postDatabaseRepository.getAllPosts(
            onSuccess = { posts ->
                fetchPostImages(
                    posts = posts,
                    onSuccess = { postsWithImages ->
                        fetchPostUsers(
                            posts = postsWithImages,
                            onSuccess = { postsWithUsers ->
                                fetchPostLikes(
                                    posts = postsWithUsers,
                                    onSuccess = { postsWithLikes ->
                                        fetchPostComments(
                                            posts = postsWithLikes,
                                            onSuccess = { postsWithComments ->
                                                Log.d("HomeViewModel", "$postsWithComments posts fetched successfully")
                                                onSuccess(postsWithComments)
                                            },
                                            onFailure = { exception ->
                                                Log.e("HomeViewModel", "Failed to fetch post comments: ${exception.message}")
                                                onSuccess(postsWithLikes)
                                            }
                                        )
                                    },
                                    onFailure = { exception ->
                                        Log.e("HomeViewModel", "Failed to fetch post likes: ${exception.message}")
                                        onSuccess(postsWithUsers)
                                    }
                                )
                            },
                            onFailure = { exception ->
                                Log.e("HomeViewModel", "Failed to fetch post users: ${exception.message}")
                                onSuccess(postsWithImages)
                            }
                        )
                    },
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Failed to fetch post images: ${exception.message}")
                        onSuccess(posts)
                    }
                )
            },
            onFailure = { exception ->
                Log.e("HomeViewModel", "Failed to fetch posts: ${exception.message}")
            }
        )
    }

    private fun fetchPostImages(
        posts: List<Post>,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updatedPosts = mutableListOf<Post>()
        var processedCount = 0

        fun checkCompletion() {
            if (processedCount == posts.size) {
                onSuccess(updatedPosts)
            }
        }

        posts.forEach { post ->
            postStorageRepository.getPostImage(
                id = post.id ?: "",
                onSuccess = { image ->
                    updatedPosts.add(post.copy(image = image))
                    processedCount++
                    checkCompletion()
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to retrieve post image for ${post.id}: ${exception.message}")
                    updatedPosts.add(post) // Dodaj post bez zmiany, jeśli obraz nie zostanie znaleziony
                    processedCount++
                    checkCompletion()
                }
            )
        }

        if (posts.isEmpty()) {
            onSuccess(emptyList())
        }
    }


    private fun fetchPostUsers(
        posts: List<Post>,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updatedPosts = mutableListOf<Post>()
        var processedCount = 0

        fun checkCompletion() {
            if (processedCount == posts.size) {
                onSuccess(updatedPosts)
            }
        }

        posts.forEach { post ->
            val userId = post.userId ?: ""
            userDatabaseRepository.getPostUserFromDatabase(
                id = userId,
                onSuccess = { postUser ->
                    // Pobierz zdjęcie profilowe użytkownika
                    userStorageRepository.downloadUserProfileImage(
                        userId = userId,
                        onSuccess = { profileImage ->

                            val updatedPostUser = postUser.copy(image = profileImage)
                            updatedPosts.add(post.copy(postUser = updatedPostUser))
                            processedCount++
                            checkCompletion()
                        },
                        onFailure = { exception ->
                            Log.e("HomeViewModel", "Failed to download profile image for user $userId: ${exception.message}")
                            // Dodaj użytkownika bez zdjęcia
                            updatedPosts.add(post.copy(postUser = postUser))
                            processedCount++
                            checkCompletion()
                        }
                    )
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to retrieve post user for ${post.id}: ${exception.message}")
                    // Dodaj post bez użytkownika
                    updatedPosts.add(post)
                    processedCount++
                    checkCompletion()
                }
            )
        }

        if (posts.isEmpty()) {
            onSuccess(emptyList())
        }
    }

    private fun fetchPostLikes(
        posts: List<Post>,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updatedPosts = mutableListOf<Post>()
        var processedCount = 0

        fun checkCompletion() {
            if (processedCount == posts.size) {
                onSuccess(updatedPosts)
            }
        }

        posts.forEach { post ->
            likeDatabaseRepository.getLikes(
                postId = post.id ?: "",
                onSuccess = { likes ->
                    updatedPosts.add(post.copy(likes = likes))
                    processedCount++
                    checkCompletion()
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to retrieve likes for post ${post.id}: ${exception.message}")
                    updatedPosts.add(post.copy(likes = emptyList())) // Dodaj post z pustą listą lików w przypadku błędu
                    processedCount++
                    checkCompletion()
                }
            )
        }

        if (posts.isEmpty()) {
            onSuccess(emptyList())
        }
    }

    private fun fetchPostComments(
        posts: List<Post>,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val updatedPosts = mutableListOf<Post>()
        var processedCount = 0

        fun checkCompletion() {
            if (processedCount == posts.size) {
                onSuccess(updatedPosts)
            }
        }

        posts.forEach { post ->
            commentDatabaseRepository.getComments(
                postId = post.id ?: "",
                onSuccess = { comments ->
                    updatedPosts.add(post.copy(comments = comments))
                    processedCount++
                    checkCompletion()
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to retrieve comments for post ${post.id}: ${exception.message}")
                    updatedPosts.add(post.copy(comments = emptyList())) // Dodaj post z pustą listą komentarzy w przypadku błędu
                    processedCount++
                    checkCompletion()
                }
            )
        }

        if (posts.isEmpty()) {
            onSuccess(emptyList())
        }
    }
}