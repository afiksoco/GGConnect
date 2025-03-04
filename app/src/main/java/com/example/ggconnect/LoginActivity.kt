package com.example.ggconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Activity Result Launcher for Firebase Auth UI
    private val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Reload the user to check if the account still exists
            currentUser.reload().addOnCompleteListener { task ->
                if (task.isSuccessful && FirebaseAuth.getInstance().currentUser != null) {
                    transactToNextScreen()
                } else {
                    // User no longer exists or reload failed, sign in again
                    signIn()
                }
            }
        } else {
            // No user signed in, start sign-in flow
            signIn()
        }
    }


    // Launch the FirebaseUI Authentication flow
    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
//            .setLogo(R.drawable.movie) // Optional logo
//            .setTheme(R.style.Theme_GGConnect) // Optional theme
            .build()

        signInLauncher.launch(signInIntent)
    }

    // Handle the result from the FirebaseUI Authentication
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            transactToNextScreen()
        } else {
            // Sign in failed
            Toast.makeText(this, "Error: Failed logging in.", Toast.LENGTH_LONG).show()
            signIn()
        }
    }

    // Move to the main activity
    private fun transactToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
