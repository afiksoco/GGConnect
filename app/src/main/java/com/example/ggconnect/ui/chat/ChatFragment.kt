package com.example.ggconnect.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.ggconnect.databinding.FragmentChatRoomBinding
class ChatFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private var chatRoomId: String? = null

    companion object {
        private const val ARG_CHAT_ROOM_ID = "chatRoomId"

        fun newInstance(chatRoomId: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CHAT_ROOM_ID, chatRoomId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        chatRoomId = arguments?.getString(ARG_CHAT_ROOM_ID)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
