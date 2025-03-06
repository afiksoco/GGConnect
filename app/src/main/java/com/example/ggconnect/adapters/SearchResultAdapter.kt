import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.databinding.ItemSearchResultBinding
import com.example.ggconnect.utils.ImageLoader

class SearchResultAdapter(
    private var items: List<SearchItem> = emptyList(),
    private val onAddFriendClick: (String) -> Unit,
    private val onLikeGameClick: (String) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
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
                    binding.resultTitleTextView.text = "/u ${item.displayName}"
                    ImageLoader.getInstance().loadImage(item.profilePicUrl, binding.resultImageView)
                    binding.resultActionButton.apply {
                        setImageResource(android.R.drawable.ic_input_add) // Add icon
                        setOnClickListener { onAddFriendClick(item.documentId) }
                    }
                }
                is SearchItem.GameItem -> {
                    binding.resultTitleTextView.text = "/g ${item.title}"
                    ImageLoader.getInstance().loadImage(item.imageUrl, binding.resultImageView)
                    binding.resultActionButton.apply {
                        setImageResource(android.R.drawable.btn_star) // Like icon
                        setOnClickListener { onLikeGameClick(item.documentId) }
                    }
                }

                else -> {}
            }
        }
    }
}
