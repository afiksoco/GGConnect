interface SearchResultAdapterCallback {
    fun onAddFriendClick(targetUserId: String)
    fun onRemoveFriendClick(targetUserId: String)
    fun onLikeGameClick(gameId: String, gameTitle: String, gameImageUrl: String)
    fun onUnlikeGameClick(gameId: String)
    fun onMessageClick(targetUserId: String)
}
