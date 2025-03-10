package com.example.ggconnect.data.models

data class Post(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImage: String? = null,
    val gameId: String = "",
    val gameTitle: String = "",
    val gameImageUrl: String? = null,
    val timestamp: Long = 0L,
    val likes: List<String> = emptyList(), // List of user IDs who liked the post
    val type: String = "like" // Default type is "like"
) {
    constructor() : this("", "", "", "")

}
