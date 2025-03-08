package com.example.ggconnect.ui.chatrooms

import MessageAdapter
import RealtimeDatabaseService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.databinding.FragmentChatroomsBinding
import com.example.ggconnect.data.models.Message

import com.google.firebase.auth.FirebaseAuth

class ChatroomsFragment : Fragment() {

    private var _binding: FragmentChatroomsBinding? = null
    private val binding get() = _binding!!

    private val messageAdapter = MessageAdapter(mutableListOf())
    private val databaseService = RealtimeDatabaseService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatroomsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSendButton()
        listenForMessages()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.chatMessagesRecyclerView.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // Start from the bottom of the chat
            }
        }
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotBlank()) {
                val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                val message = Message(senderId, messageText, System.currentTimeMillis())
                databaseService.sendMessage("exampleChatroomId", message)
                binding.messageInput.text.clear()
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun listenForMessages() {
        databaseService.listenForMessages("exampleChatroomId") { message ->
            messageAdapter.addMessage(message)
            binding.chatMessagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
