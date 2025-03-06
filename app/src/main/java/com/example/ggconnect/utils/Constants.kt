package com.example.ggconnect.utils


class Constants {
    object DB {
        // Firestore Collections
        const val USERS_COLLECTION = "users"
        const val GAMES_COLLECTION = "games"
        const val CHAT_ROOMS_COLLECTION = "chat_rooms"
        const val FRIEND_COLLECTION = "friends"
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
        fun getChatRoomPath(chatRoomId: String) = "${DB.CHAT_ROOMS_COLLECTION}/$chatRoomId"
    }

    object SearchSuffixes {
        const val USER_SUFFIX = "/u"
        const val GAME_SUFFIX = "/g"
    }
}








