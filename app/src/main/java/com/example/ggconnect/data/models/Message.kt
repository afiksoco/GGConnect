package com.example.ggconnect.data.models

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
){
    constructor() : this("", "",0L)

}