package com.mocarski.brian.beanandroid.ui

import com.google.gson.annotations.SerializedName

data class SelectableCard(
    val amountInDeck: Int,
    val amountToMoney: List<Int>,
    val name: String,
    var isSelected: Boolean
)
