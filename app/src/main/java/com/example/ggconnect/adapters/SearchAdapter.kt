import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.data.models.Game
import com.example.ggconnect.data.models.User
import com.example.ggconnect.databinding.ItemGameBinding
import com.example.ggconnect.databinding.ItemUserBinding
import com.example.ggconnect.utils.ImageLoader

sealed class SearchItem {
    data class UserItem(val user: User) : SearchItem()
    data class GameItem(val game: Game) : SearchItem()
}

class SearchAdapter(private var items: List<SearchItem> = emptyList()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_GAME = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SearchItem.UserItem -> VIEW_TYPE_USER
            is SearchItem.GameItem -> VIEW_TYPE_GAME
            else -> {
                Log.d("aaa","bad")
                return  0}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val binding = ItemUserBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                UserViewHolder(binding)
            }
            VIEW_TYPE_GAME -> {
                val binding = ItemGameBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                GameViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            when (val item = items[position]) {
                is SearchItem.UserItem -> {
                    Log.d("SearchAdapter", "Binding user: ${item.user.displayName}")
                    (holder as UserViewHolder).bind(item.user)
                }
                is SearchItem.GameItem -> {
                    Log.d("SearchAdapter", "Binding game: ${item.game.title}")
                    (holder as GameViewHolder).bind(item.game)
                }

                else -> {}
            }
        } catch (e: Exception) {
            Log.e("SearchAdapter", "Failed to bind view: ${e.message}")
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<SearchItem>) {
        Log.d("SearchAdapter", "Updating items with ${newItems.size} results")
        items = newItems
        notifyDataSetChanged()
    }

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.userNameTextView.text = "/u ${user.displayName}"
            ImageLoader.getInstance().loadImage(user.profilePicUrl, binding.userImageView)
        }
    }

    class GameViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            binding.gameTitleTextView.text = "/g ${game.title}"
            ImageLoader.getInstance().loadImage(game.imageUrl, binding.gameImageView)
        }
    }
}
