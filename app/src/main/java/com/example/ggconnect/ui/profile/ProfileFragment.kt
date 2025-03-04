package com.example.ggconnect.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ggconnect.LoginActivity
import com.example.ggconnect.databinding.FragmentProfileBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val realtimeDb = FirebaseDatabase.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        binding.textProfile.text = currentUser?.displayName ?: "Guest"

        binding.mainBTNSignout.setOnClickListener {
            signOutUser()
        }
        binding.mainBTNTestreal.setOnClickListener {
            testReal()
        }
        binding.mainBTNTestdb.setOnClickListener {
            testDB()
        }
        binding.mainBTNReadfromstore.setOnClickListener {
            readFromStore()
        }
        binding.mainBTNReadfromreal.setOnClickListener {
            readFromReal()
        }
        val textView: TextView = binding.textProfile
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    private fun testDB() {
        val userId = auth.currentUser?.uid ?: "testUser"
        val testUser = mapOf<String, Any>(
            "sender" to (auth.currentUser?.uid ?: "unknown"),
            "text" to "Hello from Realtime Database!",
            "timestamp" to System.currentTimeMillis(),
            "name" to (auth.currentUser?.displayName ?: "unkoner")

        )


        firestore.collection("user").document(userId).set(testUser)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User added to Firestore!", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Log.e("ProfileFragment", "Failed to add user to Firestore: ${e.message}")
            }
    }

    private fun readFromReal() {
        val chatRoomRef = realtimeDb.child("chatrooms").child("testChatRoom")

        chatRoomRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                for (childSnap in dataSnapshot.children) {
                    if (childSnap.exists()) {
                        val value = childSnap.getValue(String::class.java)
                        binding.mainBTNReadfromreal.text = value  ?: "Name not found"
                    } else {
                        binding.mainBTNReadfromreal.text = "No data found"
                        Log.d("readFromReal", "Data snapshot does not exist")
                    }                    }
                }
        }.addOnFailureListener { e ->
            binding.mainBTNReadfromreal.text = "Error: ${e.message}"
            Log.e("readFromReal", "Failed to read data: ${e.message}")
        }
    }


    private fun readFromStore() {

    }

    private fun testReal() {
        val chatRoomId = "testChatRoom"

        // Check if the user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            Log.e("testReal", "Current user is null")
            return
        }
        val testMessage = mapOf<String, Any>(
            "sender" to (currentUser.uid ?: "unknown"),
            "text" to "Hello from Realtime Database!",
            "timestamp" to System.currentTimeMillis(),
            "name" to (currentUser.displayName ?: "Unknown")
        )

        Log.d("testReal", "Prepared message: $testMessage")

        // Check the reference path
        val chatRef = realtimeDb.child("chatrooms").child(chatRoomId)
        Log.d("testReal", "Database reference: ${chatRef}")

        chatRef.setValue(testMessage)
            .addOnSuccessListener {
                Log.d("testReal", "Message sent successfully!")
                Toast.makeText(
                    requireContext(),
                    "Message sent to Realtime Database!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Log.e("testReal", "Failed to send message: ${e.message}")
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun signOutUser() {
        val auth = FirebaseAuth.getInstance()

        // Firebase sign out
        auth.signOut()

        // Google sign out to clear cached sign-in
        val googleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )

        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()

            // Navigate back to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // Clear FirebaseUI session
        AuthUI.getInstance().signOut(requireContext())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}