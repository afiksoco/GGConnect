package com.example.ggconnect.ui.chat
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

    fun sendMessage(chatroomId: String, message: Message, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val messageId = database.getReference("chats/$chatroomId/messages").push().key
        messageId?.let {
            database.getReference("chats/$chatroomId/messages/$messageId").setValue(message)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }
    }

    fun setTypingStatus(chatroomId: String, userId: String, isTyping: Boolean) {
        val typingRef = database.getReference("chats/$chatroomId/typing/$userId")
        typingRef.setValue(isTyping)
    }


    fun updateLastMessage(chatroomId: String, message: Message) {
        val chatRoomRef = database.getReference("chats/$chatroomId")
        chatRoomRef.child("lastMessage").setValue(message.text)
        chatRoomRef.child("timestamp").setValue(System.currentTimeMillis())
    }


    fun getOrCreateChatRoom(user1Id: String, user2Id: String, onResult: (String) -> Unit) {
        val chatRoomsRef = database.getReference("chats")

        // Query for an existing chat room between the two users
        chatRoomsRef.orderByChild("members")
            .equalTo("$user1Id,$user2Id")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // If a chat room exists, return its ID
                        val chatRoomId = snapshot.children.first().key
                        chatRoomId?.let { onResult(it) }
                    } else {
                        // If not, create a new chat room
                        val newChatRoomId = chatRoomsRef.push().key
                        newChatRoomId?.let {
                            chatRoomsRef.child(it).child("members").setValue(listOf(user1Id, user2Id))
                            onResult(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
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
