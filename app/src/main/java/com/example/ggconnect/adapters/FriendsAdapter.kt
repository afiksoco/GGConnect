package com.example.ggconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.R
import com.example.ggconnect.data.models.User
import com.example.ggconnect.databinding.ItemFriendBinding
import com.example.ggconnect.interfaces.FreindClickListener
import com.google.firebase.firestore.FirebaseFirestore

class FriendsAdapter(private val listener: FreindClickListener) :
    RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    private var friendIds: List<String> = emptyList()
    private val friendsMap = mutableMapOf<String, User?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friendId = friendIds[position]

        // Check if we already fetched this user's details
        val cachedFriend = friendsMap[friendId]
        if (cachedFriend != null) {
            holder.bind(cachedFriend)
        } else {
            // Fetch user data from Firestore if not already in the cache
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(friendId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            friendsMap[friendId] = user
                            holder.bind(user)
                        }
                    }
                }
        }
    }

    override fun getItemCount() = friendIds.size

    fun updateFriends(newFriends: List<String>) {
        friendIds = newFriends
        friendsMap.clear() // Clear cache to avoid displaying outdated data
        notifyDataSetChanged()
    }

    inner class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: User) {
            binding.friendName.text = friend.displayName
            binding.friendProfileImage.setImageResource(R.drawable.unavailable_photo)
//            ImageLoader.getInstance().loadImage(friend.profilePicUrl, binding.friendProfileImage)
//            binding.friendProfileImage.visibility = View.VISIBLE

            binding.root.setOnClickListener {
                listener.onFriendClick(friend)
            }
        }
    }
}
