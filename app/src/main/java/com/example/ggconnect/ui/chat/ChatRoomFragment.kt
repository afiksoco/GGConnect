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
import com.example.ggconnect.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private var messageListener: ChildEventListener? = null
    private val args: ChatRoomFragmentArgs by navArgs()
    private val databaseService = RealtimeDatabaseService()

    private var chatRoomId: String? = null
    private var chatRoomName: String? = null
    private var chatRoomType: Int = Constants.ChatRoomType.PRIVATE_CHAT // Default to private chat
    private val senderNameCache = mutableMapOf<String, String>()

    private lateinit var messageAdapter: MessageAdapter

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
        chatRoomType = args.chatRoomType

        binding.chatRoomTitle.text = chatRoomName

        setupRecyclerView()
        setupSendButton()

        chatRoomId?.let {
            loadMessages(it)
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(chatRoomType) { senderId, callback ->
            getUserDisplayName(senderId, callback)
        }

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
        messageListener?.let {
            databaseService.removeMessageListener(chatRoomId, it)
        }

        messageListener = databaseService.listenForMessages(chatRoomId) { message ->
            if (_binding != null) {
                messageAdapter.addMessage(message)
                binding.chatMessagesRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
            } else {
                Log.e("ChatRoomFragment", "Binding is null, cannot update UI")
            }
        }
    }

    private fun getUserDisplayName(senderId: String, callback: (String) -> Unit) {
        if (senderNameCache.containsKey(senderId)) {
            callback(senderNameCache[senderId] ?: "Unknown User")
        } else {
            FirestoreService().getUserDisplayName(senderId) { displayName ->
                val name = displayName ?: "Unknown User"
                senderNameCache[senderId] = name
                callback(name)
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
