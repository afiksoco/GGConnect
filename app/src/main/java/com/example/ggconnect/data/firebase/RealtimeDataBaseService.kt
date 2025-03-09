package com.example.ggconnect.ui.chat

import com.example.ggconnect.data.models.ChatRoom
import com.example.ggconnect.data.models.Message
import com.example.ggconnect.utils.Constants
import com.google.firebase.database.*

class RealtimeDatabaseService {

    private val database = FirebaseDatabase.getInstance()

    // Function to send a message to a specific chatroom
    fun sendMessage(
        chatroomId: String,
        message: Message,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val messageId = database.getReference("${Constants.DB.CHATS}/$chatroomId/${Constants.DB.MESSAGES}").push().key
        messageId?.let {
            val messageRef = database.getReference("${Constants.DB.CHATS}/$chatroomId/${Constants.DB.MESSAGES}/$messageId")
            messageRef.setValue(message)
                .addOnSuccessListener {
                    onSuccess()
                    updateLastMessage(chatroomId, message)
                }
                .addOnFailureListener { onFailure(it) }
        }
    }

    fun setTypingStatus(chatroomId: String, userId: String, isTyping: Boolean) {
        val typingRef = database.getReference(Constants.Pathes.getTypingStatusPath(chatroomId, userId))
        typingRef.setValue(isTyping)
    }

    fun updateLastMessage(chatroomId: String, message: Message) {
        val chatRoomRef = database.getReference(Constants.Pathes.getChatRoomPath(chatroomId))
        chatRoomRef.child(Constants.DB.LAST_MESSAGE).setValue(message.text)
        chatRoomRef.child(Constants.DB.TIMESTAMP).setValue(message.timestamp)
    }


    fun getChatRoomReference(chatRoomId: String): DatabaseReference {
        return database.getReference("chats/$chatRoomId")
    }


    fun getOrCreateChatRoom(userIds: List<String>, onResult: (String) -> Unit) {
        val chatRoomsRef = database.getReference(Constants.DB.CHATS)

        chatRoomsRef.orderByChild(Constants.DB.MEMBERS).equalTo(userIds.joinToString(","))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val chatRoomId = snapshot.children.first().key
                        chatRoomId?.let { onResult(it) }
                    } else {
                        val newChatRoomId = chatRoomsRef.push().key
                        newChatRoomId?.let {
                            val membersMap = userIds.associateWith { true }
                            chatRoomsRef.child(it).child(Constants.DB.MEMBERS).setValue(membersMap)
                            onResult(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getChatRooms(currentUserId: String, onResult: (List<ChatRoom>) -> Unit) {
        val chatRoomsRef = database.getReference(Constants.DB.CHATS)

        chatRoomsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRooms = mutableListOf<ChatRoom>()
                for (chatSnapshot in snapshot.children) {
                    val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                    chatRoom?.let { room ->
                        room.id = chatSnapshot.key ?: ""
                        room.lastMessage = chatSnapshot.child(Constants.DB.LAST_MESSAGE).getValue(String::class.java) ?: ""
                        room.timestamp = chatSnapshot.child(Constants.DB.TIMESTAMP).getValue(Long::class.java) ?: 0L
                        chatRooms.add(room)
                    }
                }
                onResult(chatRooms)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun listenForMessages(
        chatroomId: String,
        onMessageReceived: (Message) -> Unit
    ): ChildEventListener {
        val messagesRef = database.getReference(Constants.Pathes.getMessagesPath(chatroomId))

        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let { onMessageReceived(it) }
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        }

        messagesRef.addChildEventListener(listener)
        return listener
    }

    fun removeMessageListener(chatroomId: String, listener: ChildEventListener) {
        val messagesRef = database.getReference(Constants.Pathes.getMessagesPath(chatroomId))
        messagesRef.removeEventListener(listener)
    }

    fun getChatRoomMembers(chatRoomId: String, onResult: (List<String>) -> Unit) {
        val chatRoomRef = database.getReference(Constants.Pathes.getMembersPath(chatRoomId))

        chatRoomRef.get().addOnSuccessListener { snapshot ->
            val memberIds = snapshot.children.mapNotNull { it.key }
            onResult(memberIds)
        }.addOnFailureListener {
            onResult(emptyList())
        }
    }
}
