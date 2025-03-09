package com.example.ggconnect.data.models

import com.example.ggconnect.utils.Constants

data class ChatRoom(
    var id: String = "",
    var name: String = "",
    val members: Map<String, Boolean> = emptyMap(),
    var lastMessage: String = "",
    var timestamp: Long = 0L,
    var roomImageUrl: String? = null, // Add this field for the profile image
    var type: Int = Constants.ChatRoomType.PRIVATE_CHAT // Default to private chat


) {
    constructor() : this("", "", emptyMap(), "", 0L)
}

