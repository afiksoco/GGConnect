import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ggconnect.data.models.Game
import com.example.ggconnect.databinding.ItemGameBinding
import com.example.ggconnect.utils.ImageLoader

class GameAdapter(private var games: List<Game> = emptyList()) :
    RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        try {
            holder.bind(games[position])
            Log.d("GameAdapter", "Binding game at position $position: ${games[position].title}")
        } catch (e: Exception) {
            Log.e("GameAdapter", "Error binding game at position $position: ${e.message}")
        }
    }


    override fun getItemCount() = games.size

    inner class GameViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            binding.gameTitleTextView.text = game.title
            ImageLoader.getInstance().loadImage(game.imageUrl, binding.gameImageView)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateGames(newGames: List<Game>?) {
        if (newGames.isNullOrEmpty()) {
            Log.e("GameAdapter", "Game list is empty or null")
            return
        }
        games = newGames
        notifyDataSetChanged()
    }

}
