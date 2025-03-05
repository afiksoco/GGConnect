package com.example.ggconnect.data.firebase

import com.example.ggconnect.data.models.Game
import com.example.ggconnect.data.models.User
import com.example.ggconnect.utils.Constants
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Save or update a user profile
    fun saveUserProfile(user: User, onResult: (Boolean) -> Unit) {
        firestore.collection(Constants.DB.USERS_COLLECTION).document(user.id).set(user)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Retrieve a user profile by ID
    fun getUserProfile(userId: String, onResult: (User?) -> Unit) {
        firestore.collection(Constants.DB.USERS_COLLECTION).document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onResult(user)
            }
            .addOnFailureListener { onResult(null) }
    }

    // Save or update a game in the database
    fun saveGame(game: Game, onResult: (Boolean) -> Unit) {
        firestore.collection(Constants.DB.GAMES_COLLECTION).document(game.id).set(game)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Retrieve all games
    fun getGames(onResult: (List<Game>) -> Unit) {
        firestore.collection(Constants.DB.GAMES_COLLECTION).get()
            .addOnSuccessListener { result ->
                val games = result.mapNotNull { it.toObject(Game::class.java) }
                onResult(games)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Add a friend by user ID
    fun addFriend(userId: String, friendId: String, onResult: (Boolean) -> Unit) {
        firestore.collection(Constants.DB.USERS_COLLECTION).document(userId)
            .update(Constants.DB.FRIEND_COLLECTION, FieldValue.arrayUnion(friendId))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Remove a friend by user ID
    fun removeFriend(userId: String, friendId: String, onResult: (Boolean) -> Unit) {
        firestore.collection(Constants.DB.USERS_COLLECTION).document(userId)
            .update(Constants.DB.FRIEND_COLLECTION, FieldValue.arrayRemove(friendId))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
