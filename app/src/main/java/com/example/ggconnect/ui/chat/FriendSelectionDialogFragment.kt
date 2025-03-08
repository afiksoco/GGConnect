package com.example.ggconnect.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.R
import com.example.ggconnect.adapters.FriendsAdapter
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.data.models.User
import com.example.ggconnect.databinding.DialogFriendSelectionBinding
import com.example.ggconnect.interfaces.FreindClickListener



class FriendSelectionDialogFragment : DialogFragment(),FreindClickListener {

    private var _binding: DialogFriendSelectionBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirestoreService()
    private val friendAdapter = FriendsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFriendSelectionBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchFriends()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.friendRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = friendAdapter
        }
    }

    private fun fetchFriends() {
        firestore.fetchFriends{ friendList ->
            friendAdapter.updateFriends(friendList)
        }
    }

    override fun onFriendClick(friend: User) {
        dismiss()

        val fragmentManager = parentFragmentManager
        val chatRoomFragment = fragmentManager.findFragmentByTag("ChatRoomFragment") as? ChatRoomFragment
            ?: ChatRoomFragment()

        // Always use the same fragment but load the specific chat data
        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, chatRoomFragment, "ChatRoomFragment")
            .addToBackStack(null)
            .commit()

        // Load chat data based on the selected friend
        chatRoomFragment.loadChatData(friend.id)
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
