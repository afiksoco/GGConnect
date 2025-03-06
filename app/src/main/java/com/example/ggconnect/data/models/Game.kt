package com.example.ggconnect.data.models


data class Game private constructor(
    val title: String,
    var genre: String,
    val imageUrl: String,  // URL to the game image in Firebase Storage
    var releaseDate: String,
    val description: String,
) {
    constructor() : this("", "", "", "", "")

    class Builder(
        var title: String = "",
        var genre: String = "",
        var imageUrl: String = "",
        var releaseDate: String = "",
        var description: String = "",
    ) {

        fun setTitle(title: String) = apply { this.title = title }
        fun setGenre(genre: String) = apply { this.genre = genre }
        fun setImageUrl(imageUrl: String) = apply { this.imageUrl = imageUrl }
        fun setReleaseDate(releaseDate: String) = apply { this.releaseDate = releaseDate }
        fun setDescription(description: String) = apply { this.description = description }

        fun build() = Game(
            title = title,
            genre = genre,
            imageUrl = imageUrl,
            releaseDate = releaseDate,
            description = description,
        )
    }
}

