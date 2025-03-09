package com.example.ggconnect.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.data.models.ChatRoom
import com.example.ggconnect.databinding.ItemChatRoomBinding
import com.example.ggconnect.interfaces.ChatRoomClickListener
import com.example.ggconnect.utils.ImageLoader
import java.util.Calendar

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
            binding.chatRoomTextView.text = chatRoom.name
            binding.lastMessageTextView.text = chatRoom.lastMessage

            // Reset to placeholder image immediately
            ImageLoader.getInstance().loadImage("", binding.chatImageView)

            // Load the correct image if available
            if (!chatRoom.roomImageUrl.isNullOrEmpty()) {
                ImageLoader.getInstance().loadImage(chatRoom.roomImageUrl!!, binding.chatImageView)
            }

            val formattedTime = if (isToday(chatRoom.timestamp)) {
                "Today ${DateFormat.format("hh:mm a", chatRoom.timestamp)}"
            } else {
                DateFormat.format("dd/MM/yyyy hh:mm a", chatRoom.timestamp).toString()
            }
            binding.timestampTextView.text = formattedTime

            binding.root.setOnClickListener {
                listener.onChatRoomClick(chatRoom.id, chatRoom.name)
            }
        }


        // Helper function to check if the timestamp is from today
        private fun isToday(timestamp: Long): Boolean {
            val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            val today = Calendar.getInstance()
            return messageDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    messageDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }
    }
}
