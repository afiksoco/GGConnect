package com.example.ggconnect.ui.profile

import GameAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.LoginActivity
import com.example.ggconnect.adapters.UserAdapter
import com.example.ggconnect.data.firebase.AuthService
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.data.firebase.StorageService
import com.example.ggconnect.data.models.User
import com.example.ggconnect.databinding.FragmentProfileBinding
import com.example.ggconnect.utils.ImageLoader

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authService = AuthService()
    private val firestoreService = FirestoreService()
    private val storageService = StorageService()

    private val gameAdapter = GameAdapter() // Game adapter for favorite games
    private val userAdapter = UserAdapter() // Adapter for friends list

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadProfilePicture(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setupUI()
        loadUserProfile()
//        firestoreService.saveGamesToFirestore()
        return binding.root
    }

    private fun setupUI() {
        binding.profileImageView.setOnClickListener { pickImage.launch("image/*") }
        binding.mainBTNSignout.setOnClickListener { signOutUser() }

        // Setup RecyclerViews
        binding.favoriteGamesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = gameAdapter
        }

        binding.friendsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = userAdapter
        }
    }

    private fun loadUserProfile() {
        val userId = authService.getCurrentUser()?.uid ?: return

        firestoreService.getUserProfile(userId) { user ->
            user?.let { displayUserProfile(it) }
        }
    }

    private fun displayUserProfile(user: User) {
        binding.textProfileName.text = user.displayName
        ImageLoader.getInstance().loadImage(user.profilePicUrl, binding.profileImageView)

        // Load only favorite games
        loadFavoriteGames(user.favoriteGames)

        // Load friends list
        loadFriendsList(user.friends)

        // Update stats
        binding.gamesCount.text = "Liked Games\n${user.favoriteGames.size}"
        binding.friendsCount.text = "Friends\n${user.friends.size}"
    }

    private fun loadFavoriteGames(favoriteGameIds: List<String>) {
        firestoreService.getGames { games ->

            // Log the game IDs and the favorite IDs
            games.forEach { game ->
                Log.d("GameAdapter", "Game ID: ${game.id}")
            }
            favoriteGameIds.forEach { id ->
                Log.d("GameAdapter", "Favorite Game ID: $id")
            }

// Filter games to show only favorite ones
            val favoriteGames = games.filter { favoriteGameIds.contains(it.id) }
            Log.d("GameAdapter", "Filtered favorite games size: ${favoriteGames.size}")

            // Filter games to show only favorite ones
            gameAdapter.updateGames(favoriteGames)
        }
    }

    private fun loadFriendsList(friendIds: List<String>) {
        firestoreService.getFriendsProfiles(friendIds) { friends ->
            userAdapter.updateUsers(friends)
        }
    }

    private fun uploadProfilePicture(imageUri: Uri) {
        val userId = authService.getCurrentUser()?.uid ?: return

        storageService.uploadProfilePicture(userId, imageUri) { imageUrl ->
            if (imageUrl != null) {
                updateUserProfilePicture(imageUrl)
            } else {
                Toast.makeText(requireContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserProfilePicture(imageUrl: String) {
        val userId = authService.getCurrentUser()?.uid ?: return

        firestoreService.getUserProfile(userId) { user ->
            val updatedUser =
                user?.copy(profilePicUrl = imageUrl) ?: User(id = userId, profilePicUrl = imageUrl)
            firestoreService.saveUserProfile(updatedUser) { success ->
                if (success) {
                    loadUserProfile()
                    Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save profile picture.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun signOutUser() {
        authService.logout()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
