import com.example.ggconnect.data.models.Game
import com.example.ggconnect.data.models.User

interface SearchAdapterCallback {
    fun onAddFriendClick(userId: String)
    fun onLikeGameClick(gameId: String)
}
