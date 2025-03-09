package com.example.ggconnect.utils

class Constants {
    object DB {
        // Firestore Collections
        const val USERS_COLLECTION = "users"
        const val GAMES_COLLECTION = "games"
        const val CHAT_ROOMS_COLLECTION = "chat_rooms"
        const val FRIEND_COLLECTION = "friends"
        const val FAV_GAMES_COLLECTION = "favoriteGames"
        const val PROFILE_PIC_COLLECTION = "profilePicUrl"

        // Realtime Database Paths
        const val CHATS = "chats"
        const val MESSAGES = "messages"
        const val MEMBERS = "members"
        const val LAST_MESSAGE = "lastMessage"
        const val TIMESTAMP = "timestamp"
        const val TYPING = "typing"
    }

    object Storage {
        // Storage Paths
        const val PROFILE_PICS_FOLDER = "profile_pics"
        const val GAME_IMAGES_FOLDER = "game_images"
    }

    object Pathes {
        fun getProfilePicPath(userId: String) = "${Storage.PROFILE_PICS_FOLDER}/$userId.jpg"
        fun getGameImagePath(gameId: String) = "${Storage.GAME_IMAGES_FOLDER}/$gameId.jpg"

        // Firebase Realtime Database Paths
        fun getChatRoomPath(chatRoomId: String) = "${DB.CHATS}/$chatRoomId"
        fun getMessagesPath(chatRoomId: String) = "${DB.CHATS}/$chatRoomId/${DB.MESSAGES}"
        fun getMembersPath(chatRoomId: String) = "${DB.CHATS}/$chatRoomId/${DB.MEMBERS}"
        fun getLastMessagePath(chatRoomId: String) = "${DB.CHATS}/$chatRoomId/${DB.LAST_MESSAGE}"
        fun getTimestampPath(chatRoomId: String) = "${DB.CHATS}/$chatRoomId/${DB.TIMESTAMP}"
        fun getTypingStatusPath(chatRoomId: String, userId: String) =
            "${DB.CHATS}/$chatRoomId/${DB.TYPING}/$userId"
    }

    object SearchSuffixes {
        const val USER_SUFFIX = "/u"
        const val GAME_SUFFIX = "/g"
    }

    object ChatRoomType {
        const val PRIVATE_CHAT = 1
        const val CHANNEL = 2
    }

}
