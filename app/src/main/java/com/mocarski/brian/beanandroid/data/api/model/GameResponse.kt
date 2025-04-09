package com.mocarski.brian.beanandroid.data.api.model


import com.google.gson.annotations.SerializedName

data class GameResponse(
    @SerializedName("gameObject")
    val gameObject: GameObject,
    @SerializedName("message")
    val message: String
)