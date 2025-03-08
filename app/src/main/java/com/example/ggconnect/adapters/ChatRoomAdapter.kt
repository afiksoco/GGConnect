package com.example.ggconnect.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.data.models.ChatRoom
import com.example.ggconnect.databinding.ItemChatRoomBinding
import com.example.ggconnect.interfaces.ChatRoomClickListener

class ChatRoomAdapter(private val listener: ChatRoomClickListener) :
    RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    private var chatRooms: List<ChatRoom> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding = ItemChatRoomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }

    override fun getItemCount() = chatRooms.size

    fun updateChatRooms(newChatRooms: List<ChatRoom>) {
        chatRooms = newChatRooms
        notifyDataSetChanged()
    }

    inner class ChatRoomViewHolder(private val binding: ItemChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatRoom: ChatRoom) {
            val displayNameText = if (chatRoom.name.size > 1) {
                chatRoom.name.joinToString(", ")
            } else {
                chatRoom.name.firstOrNull() ?: "Unknown Chat"
            }
            binding.chatRoomTextView.text = displayNameText

            binding.root.setOnClickListener {
                listener.onChatRoomClick(chatRoom.id)
            }
        }
    }


}
