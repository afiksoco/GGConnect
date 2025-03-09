package com.example.ggconnect.data.models

data class ChatRoom(
    var id: String = "",
    var name: List<String> = emptyList(),
    val members: Map<String, Boolean> = emptyMap(),
    val lastMessage: String = "",
    val timestamp: Long = 0L
) {
    constructor() : this("", emptyList(), emptyMap(), "", 0L)
}
