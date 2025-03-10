import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.R
import com.example.ggconnect.data.models.Post
import com.example.ggconnect.databinding.ItemFeedPostBinding
import com.example.ggconnect.utils.ImageLoader
import com.example.ggconnect.utils.TimeFormatter

class FeedAdapter(
    private val onLikeClick: (Post) -> Unit,
    private val hasUserLikedPost: (Post) -> Boolean
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    private val posts = mutableListOf<Post>()

    // Store a map of ViewHolder -> Post ID
    private val viewHolderPostMap = mutableMapOf<FeedViewHolder, String?>()

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    fun updateLikes(postId: String, likesCount: Int, isLiked: Boolean) {
        val index = posts.indexOfFirst { it.id == postId }
        if (index != -1) {
            val updatedPost = posts[index].copy(likes = List(likesCount) { "dummy" })
            posts[index] = updatedPost
            notifyItemChanged(index, "like_update")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val post = posts[position]
        val currentPostId = viewHolderPostMap[holder]

        // Check if this ViewHolder is showing a different post than before
        val needsImageReload = currentPostId != post.id

        holder.bind(post, needsImageReload)

        // Update the map with current post ID
        viewHolderPostMap[holder] = post.id
    }

    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads[0] == "like_update") {
            // Only update like-related UI elements
            holder.updateLikeSection(posts[position])
        } else {
            // Full bind with post ID checking
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = posts.size

    inner class FeedViewHolder(private val binding: ItemFeedPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, loadImages: Boolean) {
            // Always update text information
            binding.userNameTextView.text =
                "${post.userName} liked ${post.gameTitle} and joined the channel!"
            val likesText = if (post.likes.size == 1) "like" else "likes"
            binding.likeCountTextView.text = "${post.likes.size} ${likesText}"
            binding.timestampTextView.text = TimeFormatter.formatTimestamp(post.timestamp)

            // Only load images when post changes in this ViewHolder
            if (loadImages) {
                loadPostImages(post)
            }

            // Set like button state
            updateLikeButton(hasUserLikedPost(post))

            // Set click listener
            binding.likeButton.setOnClickListener {
                onLikeClick(post)
            }
        }

        private fun loadPostImages(post: Post) {
            ImageLoader.getInstance().loadImage(post.gameImageUrl, binding.gameImageView)
            ImageLoader.getInstance().loadImage(post.userProfileImage, binding.userProfileImageView)
        }

        fun updateLikeSection(post: Post) {
            // Only update the like count and button state
            binding.likeCountTextView.text = "${post.likes.size} likes"
            updateLikeButton(hasUserLikedPost(post))
        }

        private fun updateLikeButton(isLiked: Boolean) {
            val likeIcon = if (isLiked) {
                R.drawable.ic_heart_filled
            } else {
                R.drawable.ic_heart_empty
            }
            binding.likeButton.setImageDrawable(
                ContextCompat.getDrawable(binding.root.context, likeIcon)
            )
        }
    }
}