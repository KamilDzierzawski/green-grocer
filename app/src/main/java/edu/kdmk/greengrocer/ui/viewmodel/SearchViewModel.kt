package edu.kdmk.greengrocer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import edu.kdmk.greengrocer.data.model.Comment
import edu.kdmk.greengrocer.data.model.Like
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

class SearchViewModel(
    private val localStorageRepository: LocalStorageRepository,
    private val postDatabaseRepository: PostDatabaseRepository,
    private val postStorageRepository: PostStorageRepository,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val userStorageRepository: UserStorageRepository,
    private val likeDatabaseRepository: LikeDatabaseRepository,
    private val commentDatabaseRepository: CommentDatabaseRepository
) {

    var currentUser = localStorageRepository.getUserData()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadPosts(
        searchText: String,
        searchTag: String
    ) {
        _loading.value = true
        getPosts(searchText, searchTag) { postList ->
            _posts.postValue(postList)
        }
        _loading.value = false
    }

    fun loadComments(postId: String) {
        commentDatabaseRepository.getComments(
            postId = postId,
            onSuccess = { comments ->
                _comments.postValue(comments)
            },
            onFailure = { exception ->
                Log.e("HomeViewModel", "Failed to retrieve comments: ${exception.message}")
            }
        )
    }

    private fun getPosts(
        searchText: String,
        searchTag: String,
        onSuccess: (List<Post>) -> Unit
    ) {
        postDatabaseRepository.getFilteredPosts(
            searchText = searchText,
            searchTag = searchTag,
            onSuccess = { posts ->
                processPostsSequentially(
                    posts = posts,
                    onSuccess = onSuccess,
                    onFailure = { exception ->
                        Log.e("HomeViewModel", "Error processing posts: ${exception.message}")
                        onSuccess(posts) // Zwraca listę bez dodatkowych danych w przypadku błędu
                    }
                )
            },
            onFailure = { exception ->
                Log.e("HomeViewModel", "Failed to fetch posts: ${exception.message}")
            }
        )
    }

    private fun processPostsSequentially(
        posts: List<Post>,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fetchPostImages(posts, { postsWithImages ->
            fetchPostUsers(postsWithImages, { postsWithUsers ->
                fetchPostLikes(postsWithUsers, { postsWithLikes ->
                    fetchPostComments(postsWithLikes, onSuccess, onFailure)
                }, onFailure)
            }, onFailure)
        }, onFailure)
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

    fun toggleLike(postId: String) {
        val currentPosts = _posts.value ?: return
        val post = currentPosts.find { it.id == postId } ?: return
        val currentUserId = currentUser?.id ?: return

        val userAlreadyLiked = post.likes?.any { it.userId == currentUserId } == true

        if (userAlreadyLiked) {
            val updatedLikes = post.likes?.filterNot { it.userId == currentUserId }
            val updatedPost = post.copy(likes = updatedLikes)

            likeDatabaseRepository.removeLike(
                userId = currentUserId,
                postId = post.id ?: "",
                onSuccess = {
                    Log.d("HomeViewModel", "Like removed successfully")
                    updatePostInList(updatedPost)
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to remove like: ${exception.message}")
                }
            )
        } else {
            val like = Like(userId = currentUserId, postId = post.id)
            val updatedLikes = (post.likes ?: emptyList()) + like
            val updatedPost = post.copy(likes = updatedLikes)

            likeDatabaseRepository.addLike(
                like = like,
                onSuccess = {
                    Log.d("HomeViewModel", "Like added successfully")
                    // Zaktualizuj listę postów
                    updatePostInList(updatedPost)
                },
                onFailure = { exception ->
                    Log.e("HomeViewModel", "Failed to add like: ${exception.message}")
                }
            )
        }
    }

    private fun updatePostInList(updatedPost: Post) {
        val currentPosts = _posts.value ?: return
        val updatedPosts = currentPosts.map { post ->
            if (post.id == updatedPost.id) updatedPost else post
        }
        _posts.postValue(updatedPosts)
    }

    fun addComment(
        postId: String,
        content: String
    ) {
        val currentPosts = _posts.value ?: return
        val post = currentPosts.find { it.id == postId } ?: return
        val currentUserId = currentUser?.id ?: return

        // Tworzymy nowy komentarz
        val comment = Comment(
            userId = currentUserId,
            postId = postId,
            content = content,
            timestamp = Timestamp.now(),
            firstName = currentUser?.fname ?: "",
            lastName = currentUser?.lname ?: ""
        )

        commentDatabaseRepository.addComment(
            comment = comment,
            onSuccess = {
                Log.d("HomeViewModel", "Comment added successfully")

                // Zaktualizuj listę komentarzy w poście
                val updatedComments = post.comments?.toMutableList() ?: mutableListOf()
                updatedComments.add(comment)

                val updatedPost = post.copy(comments = updatedComments)

                // Zaktualizuj posty w liście
                updatePostInList(updatedPost)
            },
            onFailure = { exception ->
                Log.e("HomeViewModel", "Failed to add comment: ${exception.message}")
            }
        )

        loadComments(postId)
    }
}