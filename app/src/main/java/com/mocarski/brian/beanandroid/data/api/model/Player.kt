package com.mocarski.brian.beanandroid.data.api.model


import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("cardsToPlantNow")
    val cardsToPlantNow: List<Any>,
    @SerializedName("fields")
    val fields: List<Field>,
    @SerializedName("hand")
    val hand: List<Card>,
    @SerializedName("index")
    val index: Int,
    @SerializedName("maxFields")
    val maxFields: Int,
    @SerializedName("money")
    val money: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("plantedThisTurn")
    val plantedThisTurn: Int?,
)