package com.example.ggconnect.ui.profile

import GameAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadProfilePicture(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        ImageLoader.init(requireContext()) // Initialize ImageLoader
        setupUI()
        loadUserProfile()
//        firestoreService.saveGamesToFirestore()
        return binding.root
    }

    private fun setupUI() {
        binding.profileImageView.setOnClickListener { pickImage.launch("image/*") }
        binding.mainBTNSignout.setOnClickListener { signOutUser() }
        binding.favoriteGamesRecyclerView
    }

    private fun loadUserProfile() {
        val userId = authService.getCurrentUser()?.uid ?: return

        firestoreService.getUserProfile(userId) { user ->
            user?.let { displayUserProfile(it) }
        }
//        loadAllGames() // Load all games instead of filtering by favorites

    }

    private fun displayUserProfile(user: User) {
        binding.textProfileName.text = user.displayName
        ImageLoader.getInstance().loadImage(
            source = user.profilePicUrl,
            imageView = binding.profileImageView
        )
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
            val updatedUser = user?.copy(profilePicUrl = imageUrl) ?: User(id = userId, profilePicUrl = imageUrl)
            firestoreService.saveUserProfile(updatedUser) { success ->
                if (success) {
                    loadUserProfile()
                    Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to save profile picture.", Toast.LENGTH_SHORT).show()
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


    private fun loadAllGames() {
        firestoreService.getGames { games ->
            binding.favoriteGamesRecyclerView.apply {
                adapter = GameAdapter(games)
                setHasFixedSize(true)
            }
        }
    }


    fun loadFriendsList(friendIds: List<String>) {
        firestoreService.getFriendsProfiles(friendIds) { friends ->
            binding.friendsRecyclerView.adapter = UserAdapter(friends)
        }
    }


}
