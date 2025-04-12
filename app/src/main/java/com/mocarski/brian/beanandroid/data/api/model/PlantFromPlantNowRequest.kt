package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class PlantFromPlantNowRequest(
    @SerializedName("fieldIndex")
    val fieldIndex: Int,
    @SerializedName("playerName")
    val playerName: String,
    @SerializedName("cardName")
    val cardName: String,
)
