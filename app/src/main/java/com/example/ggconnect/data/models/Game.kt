package com.example.ggconnect.data.models


data class Game(
    val id: String = "",
    val title: String = "",
    val genre: String = "",
    val imageUrl: String = "",  // URL to the game image in Firebase Storage
    val description: String = ""
)
