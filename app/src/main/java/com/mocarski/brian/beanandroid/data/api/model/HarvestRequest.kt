package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class HarvestRequest(
    @SerializedName("playerName")
    val playerName: String,
    @SerializedName("fieldIndex")
    val fieldIndex: Int,
)
