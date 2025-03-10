package com.example.ggconnect.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.data.models.Post

class FeedViewModel : ViewModel() {

    private val firestoreService = FirestoreService()

    // LiveData to observe posts
    private val _posts = MutableLiveData<List<Post>?>()
    val posts: MutableLiveData<List<Post>?> = _posts

    fun fetchFeedPosts(friendIds: List<String>) {
        firestoreService.fetchFeedPosts(friendIds) { posts ->
            _posts.value = posts
        }
    }

    fun removeLikeFromPost(post: Post, currentUserId: String) {
        val updatedPosts = _posts.value?.map { existingPost ->
            if (existingPost.id == post.id) {
                existingPost.copy(likes = existingPost.likes.filter { it != currentUserId })
            } else {
                existingPost
            }
        }
        _posts.value = updatedPosts
    }

    fun addLikeToPost(post: Post, currentUserId: String) {
        val updatedPosts = _posts.value?.map { existingPost ->
            if (existingPost.id == post.id) {
                existingPost.copy(likes = existingPost.likes + currentUserId)
            } else {
                existingPost
            }
        }
        _posts.value = updatedPosts
    }
}
