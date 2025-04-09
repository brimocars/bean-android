package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class CardsToGive(
    @SerializedName("hand")
    val hand: List<Int>,
    @SerializedName("turnedCards")
    val turnedCards: List<Int>,
)