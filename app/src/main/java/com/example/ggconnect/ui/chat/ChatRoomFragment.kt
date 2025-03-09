package com.example.ggconnect.ui.chat

import MessageAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.data.models.Message
import com.example.ggconnect.databinding.FragmentChatRoomBinding
import com.google.firebase.auth.FirebaseAuth

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!

    private val args: ChatRoomFragmentArgs by navArgs()
    private val databaseService = RealtimeDatabaseService()
    private val messageAdapter = MessageAdapter()

    private var chatRoomId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRoomId = args.chatRoomId
        setupRecyclerView()
        setupSendButton()
        chatRoomId?.let {
            loadMessages(it)
            loadChatRoomName(it)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true // Enable scrolling to the bottom
        binding.chatMessagesRecyclerView.layoutManager = layoutManager
        binding.chatMessagesRecyclerView.adapter = messageAdapter
    }


    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.messageInput.setText("")
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessage(messageText: String) {
        val currentUserId = FirebaseAuth.getInstance().uid ?: return
        val message = Message(
            senderId = currentUserId,
            text = messageText,
            timestamp = System.currentTimeMillis()
        )

        chatRoomId?.let {
            databaseService.sendMessage(it, message,
                onSuccess = {
                    // Auto-scroll to the bottom when a message is sent
                    binding.chatMessagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Failed to send message: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun loadMessages(chatRoomId: String) {
        databaseService.listenForMessages(chatRoomId) { message ->
            messageAdapter.addMessage(message)
            // Scroll to the latest message
            binding.chatMessagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private fun loadChatRoomName(chatRoomId: String) {
        databaseService.getChatRoomMembers(chatRoomId) { memberIds ->
            FirestoreService().getUserDisplayNames(memberIds) { displayNames ->
                val title = displayNames.joinToString(", ")
                binding.chatRoomTitle.text = title
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
