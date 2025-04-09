package com.mocarski.brian.beanandroid.data.api.model


import com.google.gson.annotations.SerializedName

data class GameObject(
    @SerializedName("activePlayerIndex")
    val activePlayerIndex: Int,
    @SerializedName("activeTrades")
    val activeTrades: List<Trade>,
    @SerializedName("discard")
    val discard: List<Card>,
    @SerializedName("draw")
    val draw: List<Card>,
    @SerializedName("gameCode")
    val gameCode: String,
    @SerializedName("gameId")
    val gameId: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("isOver")
    val isOver: Boolean,
    @SerializedName("phase")
    val phase: String,
    @SerializedName("players")
    val players: List<Player>,
    @SerializedName("timesShuffled")
    val timesShuffled: Int,
    @SerializedName("turnedCards")
    val turnedCards: List<Card>,
    @SerializedName("updateId")
    val updateId: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)