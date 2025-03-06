package com.example.ggconnect.ui.chatrooms

import GameAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.databinding.FragmentChatroomsBinding

class ChatroomsFragment : Fragment() {

    private var _binding: FragmentChatroomsBinding? = null
    private val binding get() = _binding!!

    private val firestoreService = FirestoreService()
    private val gameAdapter = GameAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatroomsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        loadGames()

        return root
    }

    private fun setupRecyclerView() {
        binding.GamesRecyclerView.apply {
            adapter = gameAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            setHasFixedSize(true)
        }
    }

    private fun loadGames() {
        firestoreService.getGames { games ->
            if (games.isNotEmpty()) {
                gameAdapter.updateGames(games) // Use the update method
            } else {
                binding.textChatrooms.text = "No games available"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
