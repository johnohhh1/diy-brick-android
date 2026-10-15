package com.johnohhh.diybrick

data class CardAction(
    val type: String,
    val label: String
)

data class Card(
    val uid: String,
    val action: CardAction
)
