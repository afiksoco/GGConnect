import com.example.ggconnect.data.models.Game
import com.example.ggconnect.data.models.User

sealed class SearchItem {
    data class UserItem(val user: User) : SearchItem()
    data class GameItem(val game: Game) : SearchItem()
}