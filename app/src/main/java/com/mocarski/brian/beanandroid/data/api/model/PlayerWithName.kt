package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class PlayerWithName(
    @SerializedName("playerName")
    val playerName: String
)
