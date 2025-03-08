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
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.data.models.ChatRoom
import com.example.ggconnect.databinding.FragmentChatListBinding
import com.example.ggconnect.interfaces.ChatRoomClickListener
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
        setupAddChatButton()
//        addTestChatToDatabase()
        return binding.root
    }


    private fun addTestChatToDatabase() {
        val database = FirebaseDatabase.getInstance()
        val chatRoomId = "chatRoom1"
        val chatRoomRef = database.getReference("chats/$chatRoomId")

        // Add members
        val members = mapOf(
            "SNnOGpAXi2UrGpSBvVosjztJYYr1" to true,
            "qRJFbGf1HeWu5u7N7AyMKeQ7hlv1" to true
        )
        chatRoomRef.child("members").setValue(members)

        // Add a test message
        val message = mapOf(
            "senderId" to "userId1",
            "text" to "Hello, this is a test message!",
            "timestamp" to System.currentTimeMillis()
        )
        chatRoomRef.child("messages").child("messageId1").setValue(message)
    }

    private fun setupRecyclerView() {
        binding.chatRoomsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatRoomAdapter
        }
    }

    private fun loadChatRooms() {
        val currentUserId = FirebaseAuth.getInstance().uid ?: return
        val chatRoomsRef = FirebaseDatabase.getInstance().getReference("chats")
        val firestoreService = FirestoreService()

        chatRoomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRooms = mutableListOf<ChatRoom>()

                for (chatSnapshot in snapshot.children) {
                    val chatRoom = chatSnapshot.getValue(ChatRoom::class.java)
                    chatRoom?.let { room ->
                        val userIds = room.members.keys.filter { it != currentUserId }

                        // Fetch display names of all members asynchronously
                        firestoreService.getUserDisplayNames(userIds) { displayNames ->
                            room.name = displayNames
                            chatRooms.add(room)
                            chatRoomAdapter.updateChatRooms(chatRooms)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load chats: ${error.message}")
            }
        })
    }




    private fun setupAddChatButton() {
        binding.addChatButton.setOnClickListener {
            // Fetch friend IDs first
            FirestoreService().fetchFriends { friendIds ->
                if (friendIds.isNotEmpty()) {
                    // Fetch detailed profiles for these friend IDs
                    FirestoreService().getFriendsProfiles(friendIds) { friends ->
                        if (friends.isNotEmpty()) {
//                            val selectFriendDialog = FriendSelectionDialogFragment.newInstance(friends) { selectedFriend ->
//                                createChatRoom(selectedFriend.id)
//                            }
                            FriendSelectionDialogFragment().show(childFragmentManager, "SelectFriendDialog")
                        } else {
                            showToast("No friends available.")
                        }
                    }
                } else {
                    showToast("No friends available.")
                }
            }
        }
    }

    private fun createChatRoom(friendId: String) {
//        val chatRoomId = "chat_${FirebaseAuth.getInstance().uid}_$friendId"
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.nav_host_fragment_activity_main, ChatRoomFragment.newInstance(chatRoomId))
//            .addToBackStack(null)
//            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChatRoomClick(chatRoomId: String) {
        val action = ChatListFragmentDirections.actionChatListToChatRoom()
        view?.findNavController()?.navigate(action)
    }

}
