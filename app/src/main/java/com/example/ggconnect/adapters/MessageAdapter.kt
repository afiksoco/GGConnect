import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.R
import com.example.ggconnect.data.models.Message
import com.example.ggconnect.databinding.ItemMessageBinding
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<Message>()
    private val currentUserId = FirebaseAuth.getInstance().uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.messageTextView.text = message.text

            val layoutParams = binding.messageTextView.layoutParams as ConstraintLayout.LayoutParams

            if (message.senderId == currentUserId) {
                // light blue, aligned to the right
                binding.messageTextView.setBackgroundResource(R.drawable.bg_message_sent)
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            } else {
                // light white, aligned to the left
                binding.messageTextView.setBackgroundResource(R.drawable.bg_message_received)
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            }

            binding.messageTextView.layoutParams = layoutParams
        }


    }
}
