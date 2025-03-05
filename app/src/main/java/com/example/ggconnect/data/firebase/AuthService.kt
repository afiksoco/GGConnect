package com.example.ggconnect.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthService(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    // Returns the current user if logged in, otherwise null
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Check if a user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Log out the current user
    fun logout() {
        auth.signOut()
    }
}
