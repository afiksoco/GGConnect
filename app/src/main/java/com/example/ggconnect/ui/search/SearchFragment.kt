// SearchFragment.kt
package com.example.ggconnect.ui.search

import SearchResultAdapter
import SearchResultAdapterCallback
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.data.firebase.AuthService
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.databinding.FragmentSearchBinding
import com.example.ggconnect.ui.chat.RealtimeDatabaseService
import com.google.firebase.auth.FirebaseAuth

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val firestoreService = FirestoreService()
    private val realtimeDatabaseService = RealtimeDatabaseService()
    private val authService = AuthService()
    private val searchResultAdapter = SearchResultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchInput()
        return binding.root
    }

    private fun setupRecyclerView() {

        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())

            searchResultAdapter.searchResultAdapterCallback = object : SearchResultAdapterCallback {
                override fun onAddFriendClick(targetUserId: String) {
                    firestoreService.addFriend(targetUserId) { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "Friend added", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to add friend.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onRemoveFriendClick(targetUserId: String) {
                    firestoreService.removeFriend(targetUserId) { success ->
                        if (success) {
                            Toast.makeText(requireContext(), "Friend removed", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to remove friend.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onLikeGameClick(gameId: String) {

                    firestoreService.likeGame(gameId) { success ->
                        if (success) {
                            realtimeDatabaseService.addUserToGameChannel(
                                gameId,
                                authService.getCurrentUser()?.uid
                            )

                            Toast.makeText(requireContext(), "Game liked!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to like game.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onUnlikeGameClick(gameId: String) {
                    firestoreService.unlikeGame(gameId) { success ->
                        if (success) {
                            realtimeDatabaseService.removeUserFromGameChannel(
                                gameId,
                                authService.getCurrentUser()?.uid
                            )

                            Toast.makeText(requireContext(), "Game unliked!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to unlike game.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onMessageClick(targetUserId: String) {
                    val currentUserId = FirebaseAuth.getInstance().uid ?: return

                    // Get or create a chat room with the selected user
                    realtimeDatabaseService.getOrCreateChatRoom(
                        currentUserId,
                        targetUserId
                    ) { chatRoomId ->
                        // Fetch the chat room type from the database
                        realtimeDatabaseService.getChatRoomType(chatRoomId) { chatRoomType ->
                            // Fetch the display name of the target user
                            firestoreService.getUserDisplayName(targetUserId) { userName ->
                                // Navigate to ChatRoomFragment with all necessary data
                                val action = SearchFragmentDirections.actionSearchToChatRoom(
                                    chatRoomId = chatRoomId,
                                    chatRoomName = userName,
                                    chatRoomType = chatRoomType
                                )
                                findNavController().navigate(action)
                            }
                        }
                    }
                }



            }
            adapter = searchResultAdapter
        }
    }

    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handleSearchInput(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun handleSearchInput(query: String) {
        if (query.isBlank()) {
            searchResultAdapter.updateItems(emptyList(), emptyList(), emptyList())
            return
        }
        // Fetch FRIENDS first
        firestoreService.fetchFriends { friendlist ->
            // fetch games
            firestoreService.fetchLikedGames { likedGames ->
                // Then fetch search results and pass liked games to the adapter
                firestoreService.searchUsersAndGames(query) { results ->
                    searchResultAdapter.updateItems(results, likedGames, friendlist)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
