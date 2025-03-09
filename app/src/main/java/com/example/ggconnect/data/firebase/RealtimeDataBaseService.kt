package com.example.ggconnect.ui.chat

import com.example.ggconnect.data.models.ChatRoom
import com.example.ggconnect.data.models.Message
import com.google.firebase.database.*

class RealtimeDatabaseService {

    private val database = FirebaseDatabase.getInstance()

    // Function to send a message to a specific chatroom
    fun sendMessage(chatroomId: String, message: Message, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val messageId = database.getReference("chats/$chatroomId/messages").push().key
        messageId?.let {
            val messageRef = database.getReference("chats/$chatroomId/messages/$messageId")
            messageRef.setValue(message)
                .addOnSuccessListener {
                    onSuccess()
                    updateLastMessage(chatroomId, message)
                }
                .addOnFailureListener { onFailure(it) }
        }
    }

    // Function to set the typing status of a user in a chatroom
    fun setTypingStatus(chatroomId: String, userId: String, isTyping: Boolean) {
        val typingRef = database.getReference("chats/$chatroomId/typing/$userId")
        typingRef.setValue(isTyping)
    }

    // Function to update the last message and timestamp of a chatroom
    fun updateLastMessage(chatroomId: String, message: Message) {
        val chatRoomRef = database.getReference("chats/$chatroomId")
        chatRoomRef.child("lastMessage").setValue(message.text)
        chatRoomRef.child("timestamp").setValue(message.timestamp)
    }

    // Function to get or create a chat room, now supporting group chats
    fun getOrCreateChatRoom(userIds: List<String>, onResult: (String) -> Unit) {
        val chatRoomsRef = database.getReference("chats")

        chatRoomsRef.orderByChild("members").equalTo(userIds.joinToString(","))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Chat room exists, return its ID
                        val chatRoomId = snapshot.children.first().key
                        chatRoomId?.let { onResult(it) }
                    } else {
                        // Create a new chat room for the provided user IDs
                        val newChatRoomId = chatRoomsRef.push().key
                        newChatRoomId?.let {
                            val membersMap = userIds.associateWith { true }
                            chatRoomsRef.child(it).child("members").setValue(membersMap)
                            onResult(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error (e.g., log or show a message)
                }
            })
    }


    // Function to get all chat rooms for the current user
    fun getChatRooms(currentUserId: String, onResult: (List<ChatRoom>) -> Unit) {
        val chatRoomsRef = database.getReference("chats")

        chatRoomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRooms = mutableListOf<ChatRoom>()
                for (chatSnapshot in snapshot.children) {
                    val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                    chatRoom?.let { room ->
                        room.id = chatSnapshot.key ?: ""
                        chatRooms.add(room)
                    }
                }
                onResult(chatRooms)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if necessary
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

    // Function to get all members of a chat room
    fun getChatRoomMembers(chatRoomId: String, onResult: (List<String>) -> Unit) {
        val chatRoomRef = database.getReference("chats/$chatRoomId/members")

        chatRoomRef.get().addOnSuccessListener { snapshot ->
            val memberIds = snapshot.children.mapNotNull { it.key }
            onResult(memberIds)
        }.addOnFailureListener {
            onResult(emptyList())
        }
    }

    // Clean up listeners to avoid memory leaks
    fun removeMessageListener(chatroomId: String, listener: ChildEventListener) {
        val messagesRef = database.getReference("chats/$chatroomId/messages")
        messagesRef.removeEventListener(listener)
    }
}
