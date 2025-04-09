package com.mocarski.brian.beanandroid.data.api.model


import com.google.gson.annotations.SerializedName

data class Card(
    @SerializedName("amountInDeck")
    val amountInDeck: Int,
    @SerializedName("amountToMoney")
    val amountToMoney: List<Int>,
    @SerializedName("name")
    val name: String
)