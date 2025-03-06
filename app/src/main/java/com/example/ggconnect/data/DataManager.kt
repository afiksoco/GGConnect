package com.example.ggconnect.data

import com.example.ggconnect.data.models.Game

object DataManager {

    fun generateGameList(): List<Game> {
        val games = mutableListOf<Game>()

        games.add(
            Game.Builder()
                .setTitle("World of Warcraft")
                .setGenre("MMORPG")
                .setImageUrl("gs://ggconnect-a2554.firebasestorage.app/games_pics/wow_logo.jpg")
                .setReleaseDate("2004-11-23")
                .setDescription("An epic adventure in the vast world of Azeroth, where players can choose their own paths as heroes.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("League of Legends")
                .setGenre("MOBA")
                .setImageUrl("gs://ggconnect-a2554.firebasestorage.app/games_pics/lol_image.jpg")
                .setReleaseDate("2009-10-27")
                .setDescription("A fast-paced, competitive online game blending real-time strategy with RPG elements.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("Fortnite")
                .setGenre("Battle Royale")
                .setImageUrl("gs://ggconnect-a2554.firebasestorage.app/games_pics/fornite.jpg")
                .setReleaseDate("2017-07-21")
                .setDescription("Build, battle, and survive in this massively popular free-to-play game.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("Apex Legends")
                .setGenre("Battle Royale")
                .setImageUrl("https://example.com/apex_legends.jpg")
                .setReleaseDate("2019-02-04")
                .setDescription("A hero shooter battle royale where teams compete to be the last squad standing.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("Valorant")
                .setGenre("FPS")
                .setImageUrl("https://example.com/valorant.jpg")
                .setReleaseDate("2020-06-02")
                .setDescription("A tactical shooter where players engage in intense 5v5 team-based combat.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("Overwatch 2")
                .setGenre("FPS")
                .setImageUrl("https://example.com/overwatch2.jpg")
                .setReleaseDate("2022-10-04")
                .setDescription("Team up as iconic heroes in this dynamic and fast-paced multiplayer shooter.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("Counter-Strike: Global Offensive")
                .setGenre("FPS")
                .setImageUrl("https://example.com/csgo.jpg")
                .setReleaseDate("2012-08-21")
                .setDescription("A highly tactical first-person shooter known for its competitive gameplay.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("Dota 2")
                .setGenre("MOBA")
                .setImageUrl("https://example.com/dota2.jpg")
                .setReleaseDate("2013-07-09")
                .setDescription("A complex and strategic battle arena game with millions of daily players.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("PUBG: Battlegrounds")
                .setGenre("Battle Royale")
                .setImageUrl("https://example.com/pubg.jpg")
                .setReleaseDate("2017-12-20")
                .setDescription("An intense battle royale where 100 players fight to be the last person standing.")
                .build()
        )

        games.add(
            Game.Builder()
                .setTitle("Minecraft")
                .setGenre("Sandbox")
                .setImageUrl("https://example.com/minecraft.jpg")
                .setReleaseDate("2011-11-18")
                .setDescription("An open-ended game where you can create, explore, and survive with friends.")
                .build()
        )

        return games
    }
}
