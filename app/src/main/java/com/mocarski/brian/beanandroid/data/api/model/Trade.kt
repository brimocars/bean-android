package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class Trade(
    @SerializedName("tradeId")
    val tradeId: String,
    @SerializedName("trader")
    val trader: String,
    @SerializedName("tradee")
    val tradee: String,
    @SerializedName("cardsToGive")
    val cardsToGive: CardsToGive,
    @SerializedName("cardsToReceive")
    val cardsToReceive: List<String>,
)