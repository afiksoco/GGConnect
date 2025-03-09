package com.example.ggconnect.ui.chat

import com.example.ggconnect.data.models.ChatRoom
import com.example.ggconnect.data.models.Message
import com.example.ggconnect.utils.Constants
import com.google.firebase.database.*

class RealtimeDatabaseService {

    private val database = FirebaseDatabase.getInstance()

    fun sendMessage(
        chatroomId: String,
        message: Message,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val messageId = database.getReference(Constants.Pathes.getMessagesPath(chatroomId)).push().key
        messageId?.let {
            val messageRef = database.getReference("${Constants.Pathes.getMessagesPath(chatroomId)}/$messageId")
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
        return database.getReference(Constants.Pathes.getChatRoomPath(chatRoomId))
    }


    fun getPrivateChatRoomId(user1Id: String, user2Id: String): String {
        val sortedIds = listOf(user1Id, user2Id).sorted()
        return "chat_${sortedIds[0]}_${sortedIds[1]}"
    }


    fun getOrCreateChatRoom(user1Id: String, user2Id: String, onResult: (String) -> Unit) {
        val chatRoomId = getPrivateChatRoomId(user1Id, user2Id)
        val chatRoomRef = database.getReference(Constants.Pathes.getChatRoomPath(chatRoomId))

        chatRoomRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Chat room already exists
                onResult(chatRoomId)
            } else {
                // Create a new chat room
                val members = mapOf(
                    user1Id to true,
                    user2Id to true
                )
                val chatRoomData = mapOf(
                    Constants.DB.MEMBERS to members,
                    Constants.DB.TYPE to Constants.ChatRoomType.PRIVATE_CHAT,
                )

                chatRoomRef.setValue(chatRoomData)
                    .addOnSuccessListener { onResult(chatRoomId) }
                    .addOnFailureListener { error ->
                        println("Failed to create chat room: ${error.message}")
                    }
            }
        }
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

    fun addUserToGameChannel(gameId: String, uid: String?) {
        if (uid == null) return

        val channelId = "${Constants.DB.CHANNEL_PREFIX}$gameId"
        val membersRef = database.getReference(Constants.Pathes.getMembersPath(channelId))

        membersRef.child(uid).setValue(true)
            .addOnSuccessListener {
                println("User $uid successfully added to channel $channelId")
            }
            .addOnFailureListener { error ->
                println("Failed to add user $uid to channel $channelId: ${error.message}")
            }
    }

    fun removeUserFromGameChannel(gameId: String, uid: String?) {
        if (uid == null) return

        val channelId = "${Constants.DB.CHANNEL_PREFIX}$gameId"
        val membersRef = database.getReference(Constants.Pathes.getMembersPath(channelId))

        membersRef.child(uid).removeValue()
            .addOnSuccessListener {
                println("User $uid successfully removed from channel $channelId")
            }
            .addOnFailureListener { error ->
                println("Failed to remove user $uid from channel $channelId: ${error.message}")
            }
    }

}
