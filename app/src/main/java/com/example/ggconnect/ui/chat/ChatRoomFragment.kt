package com.example.ggconnect.ui.chat

import MessageAdapter
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.ChildEventListener

class ChatRoomFragment : Fragment() {


    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private var messageListener: ChildEventListener? = null
    private val args: ChatRoomFragmentArgs by navArgs()
    private val databaseService = RealtimeDatabaseService()
    private val messageAdapter = MessageAdapter()

    private var chatRoomId: String? = null
    private var chatRoomName: String? = null
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
        chatRoomName = args.chatRoomName
        binding.chatRoomTitle.text = chatRoomName
        setupRecyclerView()
        setupSendButton()
        chatRoomId?.let {
            loadMessages(it)
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
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT)
                    .show()
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
                    Toast.makeText(
                        requireContext(),
                        "Failed to send message: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun loadMessages(chatRoomId: String) {
        // Remove any existing listener to avoid duplicate triggers
        messageListener?.let {
            databaseService.removeMessageListener(chatRoomId, it)
        }

        // Add a new listener and keep a reference to it
        messageListener = databaseService.listenForMessages(chatRoomId) { message ->
            if (_binding != null) {
                messageAdapter.addMessage(message)
                binding.chatMessagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            } else {
                Log.e("ChatRoomFragment", "Binding is null, cannot update UI")
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()

        messageListener?.let {
            chatRoomId?.let { chatId ->
                databaseService.removeMessageListener(chatId, it)
            }
        }

        _binding = null
    }

}
