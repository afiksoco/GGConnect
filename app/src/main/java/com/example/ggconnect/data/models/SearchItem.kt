sealed class SearchItem {
    data class UserItem(val documentId: String, val displayName: String, val profilePicUrl: String) : SearchItem()
    data class GameItem(val documentId: String, val title: String, val imageUrl: String) : SearchItem()
}


