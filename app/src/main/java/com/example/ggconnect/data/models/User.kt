package com.example.ggconnect.data.models

data class User  constructor(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val profilePicUrl: String = "",
    val favoriteGames: List<String> = emptyList(),  // List of favorite game IDs
    val chatList: List <String> = emptyList(), // Chat list for each user
    val friends: List<String> = emptyList()  // List of friend user IDs
){
    constructor() : this("", "", "", "")

}

