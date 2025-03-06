package com.example.ggconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.R
import com.example.ggconnect.data.models.User
import com.example.ggconnect.utils.ImageLoader

class UserAdapter(private val friends: List<User>) :
    RecyclerView.Adapter<UserAdapter.FriendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
    }

    override fun getItemCount() = friends.size

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val friendImageView: ImageView = itemView.findViewById(R.id.userImageView)
        private val friendNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)

        fun bind(user: User) {
            friendNameTextView.text = user.displayName
            ImageLoader.getInstance().loadImage(user.profilePicUrl, friendImageView)
        }
    }
}
