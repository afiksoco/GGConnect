// SearchAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.data.models.Game
import com.example.ggconnect.data.models.User
import com.example.ggconnect.databinding.ItemSearchResultBinding
import com.example.ggconnect.utils.ImageLoader

class SearchResultAdapter(
    private var items: List<SearchItem> = emptyList()
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

    fun updateItems(newItems: List<SearchItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchItem) {
            when (item) {
                is SearchItem.UserItem -> {
                    binding.textSearchResultTitle.text = "/u ${item.user.displayName}"
                    ImageLoader.getInstance().loadImage(item.user.profilePicUrl, binding.imageSearchResult)
                }
                is SearchItem.GameItem -> {
                    binding.textSearchResultTitle.text = "/g ${item.game.title}"
                    ImageLoader.getInstance().loadImage(item.game.imageUrl, binding.imageSearchResult)
                }

                else -> {}
            }
            binding.buttonAction.visibility = View.GONE // Hide action button for now
        }
    }
}
