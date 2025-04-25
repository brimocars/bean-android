package com.mocarski.brian.beanandroid.data.api.model

import com.google.gson.annotations.SerializedName

data class UniqueCardsInDeck(
    @SerializedName("wax")
    val wax: Card? = null,
    @SerializedName("blue")
    val blue: Card? = null,
    @SerializedName("chili")
    val chili: Card? = null,
    @SerializedName("stink")
    val stink: Card? = null,
    @SerializedName("green")
    val green: Card? = null,
    @SerializedName("soy")
    val soy: Card? = null,
    @SerializedName("black")
    val black: Card? = null,
    @SerializedName("red")
    val red: Card? = null,
    @SerializedName("coffee")
    val coffee: Card? = null,
    @SerializedName("garden")
    val garden: Card? = null,
    @SerializedName("cocoa")
    val cocoa: Card? = null,
)
