import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.R

class ChatListAdapter(
    private val chatRooms: List<String>,
    private val onChatRoomClick: (String) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_room, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }

    override fun getItemCount() = chatRooms.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatRoomTextView: TextView = itemView.findViewById(R.id.chatRoomTextView)

        fun bind(chatRoomName: String) {
            chatRoomTextView.text = chatRoomName
            itemView.setOnClickListener {
                onChatRoomClick(chatRoomName)
            }
        }
    }
}
