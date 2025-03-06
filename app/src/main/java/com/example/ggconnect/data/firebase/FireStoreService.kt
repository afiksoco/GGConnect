package com.example.ggconnect.data.firebase

import SearchItem
import android.util.Log
import com.example.ggconnect.data.DataManager
import com.example.ggconnect.data.models.Game
import com.example.ggconnect.data.models.User
import com.example.ggconnect.utils.Constants
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import kotlin.uuid.Uuid

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



    // Retrieve all games
    fun getGames(onResult: (List<Game>) -> Unit) {
        firestore.collection(Constants.DB.GAMES_COLLECTION).get()
            .addOnSuccessListener { result ->
                val games = result.mapNotNull {
                    try {
                        it.toObject(Game::class.java)
                    } catch (e: Exception) {
                        Log.e("FirestoreService", "Error converting document to Game: ${e.message}")
                        null
                    }
                }
                Log.d("FirestoreService", "Successfully fetched ${games.size} games.")
                onResult(games)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreService", "Failed to fetch games: ${e.message}")
                onResult(emptyList())
            }
    }



    // FirestoreService.kt
    fun searchUsersAndGames(query: String, onResult: (List<SearchItem>) -> Unit) {
        val usersCollection = firestore.collection("users")
        val gamesCollection = firestore.collection("games")

        usersCollection
            .whereGreaterThanOrEqualTo("displayName", query)
            .whereLessThanOrEqualTo("displayName", query + "\uf8ff")
            .get()
            .addOnSuccessListener { userResult ->
                val userItems = userResult.mapNotNull { document ->
                    document.toObject(User::class.java).copy(id = document.id)
                }.map { SearchItem.UserItem(it) }

                gamesCollection
                    .whereGreaterThanOrEqualTo("title", query)
                    .whereLessThanOrEqualTo("title", query + "\uf8ff")
                    .get()
                    .addOnSuccessListener { gameResult ->
                        val gameItems = gameResult.mapNotNull { document ->
                            document.toObject(Game::class.java).copy(id = document.id)
                        }.map { SearchItem.GameItem(it) }

                        val combinedResults = userItems + gameItems
                        onResult(combinedResults)
                    }
            }
    }



    fun getFriendsProfiles(friendIds: List<String>, onResult: (List<User>) -> Unit) {
        if (friendIds.isEmpty()) {
            onResult(emptyList())
            return
        }

        firestore.collection(Constants.DB.USERS_COLLECTION)
            .whereIn("id", friendIds)
            .get()
            .addOnSuccessListener { result ->
                val friends = result.mapNotNull { it.toObject(User::class.java) }
                onResult(friends)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun saveGamesToFirestore() {
        val gamesCollection = firestore.collection(Constants.DB.GAMES_COLLECTION)
        DataManager.generateGameList().forEach { game ->
            val documentId = UUID.randomUUID().toString()

            gamesCollection.add(game)
                .addOnSuccessListener { Log.d("games to db", "game $documentId") }
                .addOnFailureListener { e ->Log.d("failed games", "game $documentId") }
        }
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
