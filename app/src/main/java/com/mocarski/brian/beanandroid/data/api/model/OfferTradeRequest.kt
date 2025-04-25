package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class OfferTradeRequest(
    @SerializedName("trader")
    val trader: String,
    @SerializedName("tradee")
    val tradeee: String,
    @SerializedName("cardsToGive")
    val cardsToGive: CardsToGive,
    @SerializedName("cardsToReceive")
    val cardsToReceive: List<String>,
)