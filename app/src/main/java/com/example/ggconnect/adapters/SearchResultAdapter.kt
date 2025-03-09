import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.R
import com.example.ggconnect.databinding.ItemSearchResultBinding
import com.example.ggconnect.utils.Constants
import com.example.ggconnect.utils.ImageLoader

class SearchResultAdapter(
    private var items: List<SearchItem> = emptyList(),
    private var likedGames: List<String> = emptyList(),
    private var friendsList: List<String> = emptyList(),
    var searchResultAdapterCallback: SearchResultAdapterCallback? = null
) : RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateItems(
        newItems: List<SearchItem>,
        likedGames: List<String>,
        friendList: List<String>
    ) {
        items = newItems
        this.likedGames = likedGames
        this.friendsList = friendList
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchItem) {
            when (item) {
                is SearchItem.UserItem -> {
                    binding.textSearchResultTitle.text =
                        "${Constants.SearchSuffixes.USER_SUFFIX} ${item.user.displayName}"
                    ImageLoader.getInstance()
                        .loadImage(item.user.profilePicUrl, binding.imageSearchResult)

                    // Check if the user is already a friend
                    var isFriend = friendsList.contains(item.user.id)
                    val addIcon =
                        if (isFriend) android.R.drawable.ic_delete else android.R.drawable.ic_input_add

                    binding.buttonAction.setImageResource(addIcon)
                    binding.buttonActionMessage.setImageResource(R.drawable.chat_icon_search_frag)

                    binding.buttonAction.setOnClickListener {
                        if (isFriend) {
                            searchResultAdapterCallback?.onRemoveFriendClick(item.user.id)
                            binding.buttonAction.setImageResource(android.R.drawable.ic_input_add)
                        } else {
                            searchResultAdapterCallback?.onAddFriendClick(item.user.id)
                            binding.buttonAction.setImageResource(android.R.drawable.ic_delete)
                        }
                        isFriend = !isFriend
                    }

                    // New: Handle Message Button Click
                    binding.buttonActionMessage.setOnClickListener {
                        searchResultAdapterCallback?.onMessageClick(item.user.id)
                    }
                }

                is SearchItem.GameItem -> {
                    binding.textSearchResultTitle.text =
                        "${Constants.SearchSuffixes.GAME_SUFFIX} ${item.game.title}"
                    ImageLoader.getInstance()
                        .loadImage(item.game.imageUrl, binding.imageSearchResult)

                    var isLiked = likedGames.contains(item.game.id)
                    val starIcon =
                        if (isLiked) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star

                    binding.buttonAction.setImageResource(starIcon)
                    binding.buttonAction.setOnClickListener {
                        if (isLiked) {
                            searchResultAdapterCallback?.onUnlikeGameClick(item.game.id)
                            binding.buttonAction.setImageResource(android.R.drawable.btn_star)
                        } else {
                            searchResultAdapterCallback?.onLikeGameClick(item.game.id)
                            binding.buttonAction.setImageResource(android.R.drawable.btn_star_big_on)
                        }
                        isLiked = !isLiked
                    }

                    // Hide the message button for games
                    binding.buttonActionMessage.visibility = View.GONE
                }

                else -> {}
            }

            // Enables scrolling if the string is too long
            binding.textSearchResultTitle.isSelected = true
        }
    }
}


