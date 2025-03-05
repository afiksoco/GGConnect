package com.example.ggconnect.data.firebase

import android.net.Uri
import com.example.ggconnect.utils.Constants
import com.google.firebase.storage.FirebaseStorage

class StorageService(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    private val storageRef = storage.reference

    // Upload user profile picture
    fun uploadProfilePicture(userId: String, imageUri: Uri, onResult: (String?) -> Unit) {
        val profilePicRef = storageRef.child(Constants.Pathes.getProfilePicPath(userId))
        profilePicRef.putFile(imageUri)
            .addOnSuccessListener {
                profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                    onResult(uri.toString())
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // Upload game image
    fun uploadGameImage(gameId: String, imageUri: Uri, onResult: (String?) -> Unit) {
        val gameImageRef = storageRef.child(Constants.Pathes.getGameImagePath(gameId))
        gameImageRef.putFile(imageUri)
            .addOnSuccessListener {
                gameImageRef.downloadUrl.addOnSuccessListener { uri ->
                    onResult(uri.toString())
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}
