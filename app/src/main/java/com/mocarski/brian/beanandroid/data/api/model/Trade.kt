package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class Trade(
    @SerializedName("tradeId")
    val tradeId: String,
    @SerializedName("traderName")
    val traderName: String,
    @SerializedName("tradeeName")
    val tradeeName: String,
    @SerializedName("cardsToGive")
    val cardsToGive: CardsToGive,
    @SerializedName("cardsToReceive")
    val cardsToReceive: List<String>,
)