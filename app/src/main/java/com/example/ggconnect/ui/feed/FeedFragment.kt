package com.example.ggconnect.ui.feed

import FeedAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ggconnect.data.firebase.FirestoreService
import com.example.ggconnect.data.models.Post
import com.example.ggconnect.databinding.FragmentFeedBinding
import com.google.firebase.auth.FirebaseAuth

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var feedViewModel: FeedViewModel
    private val firestoreService = FirestoreService()

    private val feedAdapter = FeedAdapter(
        onLikeClick = { post -> handleLikeClick(post) },
        hasUserLikedPost = { post -> hasUserLikedPost(post) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        feedViewModel = ViewModelProvider(this)[FeedViewModel::class.java]

        setupRecyclerView()
        observeFeedPosts()
        fetchFriendsAndLoadFeed()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.feedRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = feedAdapter
        }
    }

    private fun observeFeedPosts() {
        feedViewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts != null && posts.isNotEmpty()) {
                feedAdapter.updatePosts(posts)
                binding.feedRecyclerView.visibility = View.VISIBLE
                binding.noPostsTextView.visibility = View.GONE
            } else {
                binding.feedRecyclerView.visibility = View.GONE
                binding.noPostsTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchFriendsAndLoadFeed() {
        val currentUserId = FirebaseAuth.getInstance().uid ?: return

        firestoreService.fetchFriends { friendIds ->
            val allIds = friendIds.toMutableList().apply { add(currentUserId) }
            feedViewModel.fetchFeedPosts(allIds)
        }
    }

    private fun handleLikeClick(post: Post) {
        val currentUserId = FirebaseAuth.getInstance().uid ?: return
        val isLiked = hasUserLikedPost(post)

        if (isLiked) {
            firestoreService.unlikePost(post.id, currentUserId) { success ->
                if (success) {
                    feedViewModel.removeLikeFromPost(post, currentUserId)
                }
            }
        } else {
            firestoreService.likePost(post.id, currentUserId) { success ->
                if (success) {
                    feedViewModel.addLikeToPost(post, currentUserId)
                }
            }
        }
    }

    private fun hasUserLikedPost(post: Post): Boolean {
        val currentUserId = FirebaseAuth.getInstance().uid ?: return false
        return post.likes.contains(currentUserId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
