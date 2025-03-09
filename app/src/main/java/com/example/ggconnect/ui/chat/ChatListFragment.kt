package com.example.ggconnect.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.adapters.ChatRoomAdapter
import com.example.ggconnect.data.firebase.AuthService
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.data.models.ChatRoom
import com.example.ggconnect.databinding.FragmentChatListBinding
import com.example.ggconnect.interfaces.ChatRoomClickListener
import com.example.ggconnect.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatListFragment : Fragment(), ChatRoomClickListener {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val chatRoomAdapter = ChatRoomAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadChatRooms()
//        addTestChatsAndChannelsToDatabase()
        return binding.root
    }


    fun addTestChatsAndChannelsToDatabase() {
        val database = FirebaseDatabase.getInstance()
        val chatRoomsRef = database.getReference(Constants.DB.CHATS)

        val currentUserId = "SNnOGpAXi2UrGpSBvVosjztJYYr1" // Example current user ID

        // Define test private chats
        val privateChats = listOf(
            "chatRoom1" to mapOf(
                "name" to "User 2",
                "lastMessage" to "Hello, this is a test message!",
                "timestamp" to System.currentTimeMillis(),
                "members" to mapOf(
                    currentUserId to true,
                    "qRJFbGf1HeWu5u7N7AyMKeQ7hlv1" to true
                ),
                "type" to Constants.ChatRoomType.PRIVATE_CHAT
            ),
            "asdsadsa" to mapOf(
                "name" to "User 3",
                "lastMessage" to "This is another test message!",
                "timestamp" to System.currentTimeMillis(),
                "members" to mapOf(
                    "LvZiniOj5LbpYjWexWznSx0OPll2" to true,
                    currentUserId to true
                ),
                "type" to Constants.ChatRoomType.PRIVATE_CHAT
            )
        )

        // Define game channels with members list and static image URLs
        val gameChannels = listOf(
            "MwLTK83QTTCAgTygzrSB" to Triple(
                "League of Legends",
                "https://example.com/league_of_legends.png",
                mapOf(currentUserId to true)
            ),
            "xw0HCy0NAhGOjD0ANiMI" to Triple(
                "Valorant",
                "https://example.com/valorant.png",
                mapOf(currentUserId to true)
            ),
            "2rnNCJaKxPRQubohLChB" to Triple(
                "Minecraft",
                "https://example.com/minecraft.png",
                mapOf(currentUserId to true)
            ),
            "3SrrCbkma9szghb1xmGG" to Triple(
                "Fortnite",
                "https://example.com/fortnite.png",
                mapOf(currentUserId to true)
            ),
            "79Mkrsv5T2nQwT8djZwQ" to Triple(
                "Apex Legends",
                "https://example.com/apex_legends.png",
                mapOf(currentUserId to true)
            ),
            "zSIH0Q8zxY2iZpku1r0E" to Triple(
                "CS: GO",
                "https://example.com/cs_go.png",
                mapOf(currentUserId to true)
            ),
            "JGWjtpXYUmDD3HnxGmyp" to Triple(
                "Overwatch 2",
                "https://example.com/overwatch_2.png",
                mapOf(currentUserId to true)
            ),
            "bFzqBbil682v9d87BABk" to Triple(
                "PUBG",
                "https://example.com/pubg.png",
                mapOf(currentUserId to true)
            ),
            "oBiwvn49mKBSFx9pkyR2" to Triple(
                "Dota 2",
                "https://example.com/dota_2.png",
                mapOf(currentUserId to true)
            ),
            "I7FeSaUwl8mqdzEpqWZr" to Triple(
                "World of Warcraft",
                "https://example.com/world_of_warcraft.png",
                mapOf(currentUserId to true)
            )
        )

        // Add private chats to the database
        for ((chatRoomId, chatData) in privateChats) {
            val chatRoomRef = chatRoomsRef.child(chatRoomId)
            chatRoomRef.setValue(chatData)
                .addOnSuccessListener {
                    println("Private chat room $chatRoomId added successfully")
                }
                .addOnFailureListener { error ->
                    println("Failed to add private chat room $chatRoomId: ${error.message}")
                }
        }

        // Add game channels to the database with static images
        for ((gameId, gameInfo) in gameChannels) {
            val (gameName, imageUrl, members) = gameInfo
            val channelId = "channel_$gameId"
            val channelRef = chatRoomsRef.child(channelId)

            // Set up the channel structure
            val channelData = mapOf(
                "name" to gameName,
                "lastMessage" to "Welcome to $gameName channel!",
                "timestamp" to System.currentTimeMillis(),
                "members" to members, // Add the current user as a member
                "roomImageUrl" to imageUrl, // Static image URL
                "type" to Constants.ChatRoomType.CHANNEL
            )

            channelRef.setValue(channelData)
                .addOnSuccessListener {
                    println("Channel created: $gameName")
                }
                .addOnFailureListener {
                    println("Failed to create channel: ${it.message}")
                }
        }

        // Add a test message to a private chat
        val message = mapOf(
            "senderId" to "qRJFbGf1HeWu5u7N7AyMKeQ7hlv1",
            "text" to "Hello, this is a test message!",
            "timestamp" to System.currentTimeMillis()
        )
        chatRoomsRef.child("chatRoom1").child(Constants.DB.MESSAGES).child("messageId1")
            .setValue(message)
    }


    private fun setupRecyclerView() {
        binding.chatRoomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatRoomAdapter
        }
    }

    private fun loadChatRooms() {
        val currentUserId = FirebaseAuth.getInstance().uid ?: return
        val chatRoomsRef = FirebaseDatabase.getInstance().getReference(Constants.DB.CHATS)
        val firestoreService = FirestoreService()

        chatRoomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRooms = mutableListOf<ChatRoom>()
                var pendingProfileImages = 0

                for (chatSnapshot in snapshot.children) {
                    val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                    chatRoom?.let { room ->
                        room.id = chatSnapshot.key.orEmpty()
                        room.lastMessage =
                            chatSnapshot.child("lastMessage").getValue(String::class.java).orEmpty()
                        room.timestamp =
                            chatSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                        room.roomImageUrl =
                            chatSnapshot.child("roomImageUrl").getValue(String::class.java)
                        room.type = chatSnapshot.child("type").getValue(Int::class.java)
                            ?: Constants.ChatRoomType.PRIVATE_CHAT

                        if (room.members.containsKey(currentUserId)) {
                            if (room.type == Constants.ChatRoomType.PRIVATE_CHAT) {
                                // Fetch the display name of the other user in private chats
                                val otherUserId =
                                    room.members.keys.firstOrNull { it != currentUserId }
                                if (otherUserId != null) {
                                    pendingProfileImages++
                                    firestoreService.getUserProfileImageUrl(otherUserId) { imageUrl ->
                                        room.roomImageUrl = imageUrl
                                        pendingProfileImages--
                                        if (pendingProfileImages == 0) {
                                            updateChatList(chatRooms)
                                        }
                                    }

                                    firestoreService.getUserDisplayName(otherUserId) { displayName ->
                                        room.name = displayName
                                        chatRooms.add(room)
                                        if (pendingProfileImages == 0) {
                                            updateChatList(chatRooms)
                                        }
                                    }
                                }
                            } else if (room.type == Constants.ChatRoomType.CHANNEL) {
                                // For channels, use the name from Firebase Realtime Database
                                room.name = chatSnapshot.child("name").getValue(String::class.java)
                                    .orEmpty()
                                chatRooms.add(room)
                            }
                        }
                    }
                }

                if (pendingProfileImages == 0) {
                    updateChatList(chatRooms)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load chats: ${error.message}")
            }
        })
    }


    // Helper function to sort and update the chat list
    private fun updateChatList(chatRooms: MutableList<ChatRoom>) {
        chatRooms.sortByDescending { it.timestamp }
        chatRoomAdapter.updateChatRooms(chatRooms)
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChatRoomClick(chatRoomId: String, chatRoomName: String) {
        val action = ChatListFragmentDirections.actionChatListToChatRoom(chatRoomId, chatRoomName)
        view?.findNavController()?.navigate(action)
    }

}
