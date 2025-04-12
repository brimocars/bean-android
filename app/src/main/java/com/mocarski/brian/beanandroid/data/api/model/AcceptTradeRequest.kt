package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class AcceptTradeRequest(
    @SerializedName("tradeId")
    val tradeId: String,
    @SerializedName("choseCardsToReceive")
    val chosenCardsToReceive: CardsToGive
)
