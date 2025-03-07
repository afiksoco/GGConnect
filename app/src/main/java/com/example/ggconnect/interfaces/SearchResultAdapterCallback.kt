interface SearchResultAdapterCallback {
    fun onAddFriendClick(targetUserId: String)
    fun onRemoveFriendClick(targetUserId: String)
    fun onLikeGameClick(gameId: String)
    fun onUnlikeGameClick(gameId: String)
}
