package com.example.ggconnect.ui.chat
import MessageAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ggconnect.databinding.FragmentChatRoomBinding

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!

    private var chatRoomId: String? = null
    private val databaseService = RealtimeDatabaseService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatRoomId?.let { loadChatData(it) }
    }

    fun loadChatData(chatRoomId: String) {
        this.chatRoomId = chatRoomId
        binding.chatRoomTitle.text = "Chat Room: $chatRoomId"

        // Load messages from Realtime Database
//        databaseService.getMessages(chatRoomId) { messages ->
//            // Update the RecyclerView or UI with the messages
//            binding.chatMessagesRecyclerView.adapter = MessageAdapter(messages)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
