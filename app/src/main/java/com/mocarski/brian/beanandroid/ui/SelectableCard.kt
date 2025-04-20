package com.mocarski.brian.beanandroid.ui

import com.mocarski.brian.beanandroid.data.api.model.Card

data class SelectableCard(
    val card: Card,
    var isSelected: Boolean
)
