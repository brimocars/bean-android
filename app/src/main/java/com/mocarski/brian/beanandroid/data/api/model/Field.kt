package com.mocarski.brian.beanandroid.data.api.model


import com.google.gson.annotations.SerializedName

data class Field(
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("card")
    val card: Card,
)