import com.example.ggconnect.data.models.Message
import com.google.firebase.database.*

class RealtimeDatabaseService {

    private val database = FirebaseDatabase.getInstance()

    // Function to send a message to a specific chatroom
    fun sendMessage(chatroomId: String, message: Message) {
        val messageId = database.getReference("chats/$chatroomId/messages").push().key
        messageId?.let {
            database.getReference("chats/$chatroomId/messages/$messageId").setValue(message)
        }
    }

    // Function to listen for new messages in a chatroom
    fun listenForMessages(chatroomId: String, onMessageReceived: (Message) -> Unit) {
        val messagesRef = database.getReference("chats/$chatroomId/messages")

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let { onMessageReceived(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }
}
