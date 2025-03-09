package com.example.ggconnect.data.firebase

import SearchItem
import android.util.Log
import com.example.ggconnect.data.DataManager
import com.example.ggconnect.data.models.Game
import com.example.ggconnect.data.models.User
import com.example.ggconnect.utils.Constants
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val authService = AuthService()

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
            // Generate a new document ID
            val documentRef = gamesCollection.document()
            val documentId = documentRef.id

            // Update the Game object with the document ID
            val updatedGame = game.copy(id = documentId)

            // Save the Game object with the ID set
            documentRef.set(updatedGame)
                .addOnSuccessListener {
                    Log.d("FirestoreService", "Game saved with ID: $documentId")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreService", "Failed to save game: $documentId", e)
                }
        }
    }


    // Add a friend by user ID
    fun addFriend(targetUserId: String, onResult: (Boolean) -> Unit) {
        val currentUserID = authService.getCurrentUser()?.uid ?: return
        val user = firestore.collection(Constants.DB.USERS_COLLECTION).document(currentUserID)
        user.update(Constants.DB.FRIEND_COLLECTION, FieldValue.arrayUnion(targetUserId))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
        val targetUser = firestore.collection(Constants.DB.USERS_COLLECTION).document(targetUserId)
        targetUser.update(Constants.DB.FRIEND_COLLECTION, FieldValue.arrayUnion(currentUserID))

    }

    // Remove a friend by user ID
    fun removeFriend(targetUserId: String, onResult: (Boolean) -> Unit) {
        val currentUserID = authService.getCurrentUser()?.uid ?: return
        val user = firestore.collection(Constants.DB.USERS_COLLECTION).document(currentUserID)
        user.update(Constants.DB.FRIEND_COLLECTION, FieldValue.arrayRemove(targetUserId))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
        val targetUser = firestore.collection(Constants.DB.USERS_COLLECTION).document(targetUserId)
        targetUser.update(Constants.DB.FRIEND_COLLECTION, FieldValue.arrayRemove(currentUserID))

    }

    fun likeGame(gameId: String, onResult: (Boolean) -> Unit) {
        val currentUserID = authService.getCurrentUser()?.uid ?: return
        val user = firestore.collection(Constants.DB.USERS_COLLECTION).document(currentUserID)
        user.update(Constants.DB.FAV_GAMES_COLLECTION, FieldValue.arrayUnion(gameId))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun unlikeGame(gameId: String, onResult: (Boolean) -> Unit) {
        val currentUserID = authService.getCurrentUser()?.uid ?: return
        val userRef = firestore.collection(Constants.DB.USERS_COLLECTION).document(currentUserID)
        userRef.update(Constants.DB.FAV_GAMES_COLLECTION, FieldValue.arrayRemove(gameId))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


    fun fetchLikedGames(onResult: (List<String>) -> Unit) {
        val userId = authService.getCurrentUser()?.uid ?: return
        firestore.collection(Constants.DB.USERS_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val likedGames =
                    document.get(Constants.DB.FAV_GAMES_COLLECTION) as? List<String> ?: emptyList()
                onResult(likedGames)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun fetchFriends(onResult: (List<String>) -> Unit) {
        val userId = authService.getCurrentUser()?.uid ?: return

        firestore.collection(Constants.DB.USERS_COLLECTION)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val friendsList =
                    document.get(Constants.DB.FRIEND_COLLECTION) as? List<String> ?: emptyList()
                onResult(friendsList)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getUserProfileImageUrl(userId: String, onResult: (String?) -> Unit) {
        val userRef = FirebaseFirestore.getInstance().collection(Constants.DB.USERS_COLLECTION)
            .document(userId)
        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.contains(Constants.DB.PROFILE_PIC_COLLECTION)) {
                val imageUrl = document.getString(Constants.DB.PROFILE_PIC_COLLECTION)
                onResult(imageUrl)
            } else {
                onResult(null)
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }


    fun getUserDisplayNames(userIds: List<String>, callback: (List<String>) -> Unit) {
        firestore.collection("users")
            .whereIn(FieldPath.documentId(), userIds)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val displayNames = querySnapshot.documents.mapNotNull {
                    it.getString("displayName")
                }
                callback(displayNames)
            }
            .addOnFailureListener {
                callback(emptyList()) // Return an empty list on failure
            }
    }

}
