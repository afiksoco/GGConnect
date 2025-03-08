//package com.example.ggconnect.ui.chat
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.ggconnect.adapters.FriendsAdapter
//import com.example.ggconnect.data.models.User
//import com.example.ggconnect.databinding.FragmentSelectFriendsBinding
//import com.example.ggconnect.interfaces.FreindClickListener
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//
//class SelectFriendsFragment : Fragment(), FreindClickListener {
//
//    private var _binding: FragmentSelectFriendsBinding? = null
//    private val binding get() = _binding!!
//    private val friendsAdapter = FriendsAdapter(this)
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentSelectFriendsBinding.inflate(inflater, container, false)
//        setupRecyclerView()
//        loadFriends()
//        return binding.root
//    }
//
//    private fun setupRecyclerView() {
//        binding.friendsRecyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = friendsAdapter
//        }
//    }
//
////    private fun loadFriends() {
////        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
////        val firestore = FirebaseFirestore.getInstance()
////
////        firestore.collection("users").document(currentUserId)
////            .get()
////            .addOnSuccessListener { document ->
////                val friendsIds = document.get("friends") as? List<String> ?: emptyList()
////                loadFriendsDetails(friendsIds)
////            }
////    }
//
////    private fun loadFriendsDetails(friendsIds: List<String>) {
////        val firestore = FirebaseFirestore.getInstance()
////
////        firestore.collection("users")
////            .whereIn("id", friendsIds)
////            .get()
////            .addOnSuccessListener { result ->
////                val friends = result.mapNotNull { it.toObject(User::class.java) }
////                friendsAdapter.updateFriends(friends)
////            }
////    }
//
//    override fun onFriendClick(friend: User) {
//        // Handle starting a new chat with the selected friend
////        (activity as? ChatListFragment.ChatListListener)?.onStartChatWithFriend(friend)
//        parentFragmentManager.popBackStack()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
