package com.mocarski.brian.beanandroid.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocarski.apidemo2.data.api.model.GameObjectViewModel
import com.mocarski.brian.beanandroid.data.api.model.AcceptTradeRequest
import com.mocarski.brian.beanandroid.data.api.model.Card
import com.mocarski.brian.beanandroid.data.api.model.CardsToGive
import com.mocarski.brian.beanandroid.data.api.model.Field
import com.mocarski.brian.beanandroid.data.api.model.FieldIndex
import com.mocarski.brian.beanandroid.data.api.model.GameObject
import com.mocarski.brian.beanandroid.data.api.model.HarvestRequest
import com.mocarski.brian.beanandroid.data.api.model.OfferTradeRequest
import com.mocarski.brian.beanandroid.data.api.model.PlantFromPlantNowRequest
import com.mocarski.brian.beanandroid.data.api.model.Player
import com.mocarski.brian.beanandroid.data.api.model.Trade
import com.mocarski.brian.beanandroid.data.api.model.TradeId
import com.mocarski.brian.beanandroid.data.api.model.UniqueCardsInDeck
import kotlin.math.roundToInt
import kotlin.reflect.full.memberProperties

@Composable
fun ViewTrades(
    gameViewModel: GameObjectViewModel,
    playerName: String,
    setShowTrades: (Boolean) -> Unit
) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val player = findPlayer(gameObject, playerName)

    val (selectedHandIndexes, setSelectedHandIndexes) = remember { mutableStateOf(setOf<Int>()) }
    val (selectedTurnedCardIndexes, setSelectedTurnedCardIndexes) = remember { mutableStateOf(setOf<Int>()) }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(10.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            FilledIconButton(onClick = { setShowTrades(false) }) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
        }//close button row

        Text(text = "Select from your hand")
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            for ((index, card) in player.hand.withIndex()) {
                SelectableCard(card, isSelected = index in selectedHandIndexes, {
                    val newSelectedHand = selectedHandIndexes.toMutableSet()
                    if (index in newSelectedHand) newSelectedHand.remove(index) else newSelectedHand.add(
                        index
                    )
                    setSelectedHandIndexes(newSelectedHand)
                })
                Spacer(modifier = Modifier.width(10.dp))
            }
        }//hand row

        if (gameObject.activePlayerIndex == player.index) {
            Text(text = "Select from turned cards")
            Row() {
                for ((index, card) in gameObject.turnedCards!!.withIndex()) {
                    SelectableCard(card, isSelected = index in selectedTurnedCardIndexes, {
                        val newSelectedTurnedCards = selectedTurnedCardIndexes.toMutableSet()
                        if (index in newSelectedTurnedCards) newSelectedTurnedCards.remove(index) else newSelectedTurnedCards.add(
                            index
                        )
                        setSelectedTurnedCardIndexes(newSelectedTurnedCards)
                    })
                    Spacer(modifier = Modifier.width(10.dp))
                }//turnedCards
            }//row
        }//turned cards

        val tradesForPlayer = gameObject.activeTrades!!.filter { it.tradeeName == playerName }
        if (tradesForPlayer.isEmpty()) {
            setShowTrades(false)
        }
        for (trade in tradesForPlayer) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 30.dp)
                    .background(Color.LightGray)
            ) {
                val chosenCardsToReceive = CardsToGive(
                    hand = selectedHandIndexes.toList(),
                    turnedCards = selectedTurnedCardIndexes.toList()
                )

                val flatListOfChosenCardsToReceive = mutableListOf<String>()
                if (chosenCardsToReceive.turnedCards != null) {
                    for (cardIndex in chosenCardsToReceive.turnedCards) {
                        flatListOfChosenCardsToReceive.add(gameObject.turnedCards!![cardIndex].name)
                    }
                }
                if (chosenCardsToReceive.hand != null) {
                    for (cardIndex in chosenCardsToReceive.hand) {
                        flatListOfChosenCardsToReceive.add(player.hand[cardIndex].name)
                    }
                }
                flatListOfChosenCardsToReceive.sort()
                val cardsToReceive = trade.cardsToReceive.sorted()

                var chosenCardsAreCorrect = false
                if (flatListOfChosenCardsToReceive.size == cardsToReceive.size) {
                    chosenCardsAreCorrect = true
                    for (index in flatListOfChosenCardsToReceive.indices) {
                        if (flatListOfChosenCardsToReceive[index] != cardsToReceive[index]) {
                            chosenCardsAreCorrect = false
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Column {
                    Button(
                        onClick = {
                            gameViewModel.acceptTrade(
                                AcceptTradeRequest(
                                    trade.tradeId,
                                    chosenCardsToReceive
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = if (chosenCardsAreCorrect) Color.Blue
                            else Color(0.75f, 0.75f, 0.75f),
                            containerColor = if (chosenCardsAreCorrect) Color(
                                0.98f,
                                0.843f,
                                0.686f
                            ) else Color(0.5f, 0.5f, 0.5f),
                        )
                    ) {
                        Text(
                            text = "Accept Trade",
                            fontSize = 20.sp
                        )
                    }//accept button
                    Button(
                        onClick = {
                            gameViewModel.denyTrade(TradeId(trade.tradeId))
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Red,
                            containerColor = Color(0.98f, 0.843f, 0.686f),
                        )
                    ) {
                        Text(
                            text = "Decline Trade",
                            fontSize = 20.sp
                        )
                    }//deny button
                }//buttons
                Spacer(Modifier.weight(1f))

                Column() {
                    Text("Give:")
                    // this is backwards because the trade is from the perspective of the offerer
                    for (cardToGive in trade.cardsToReceive) {
                        Text(cardToGive)
                    }
                }//give column
                Spacer(Modifier.weight(1f))
                Column() {
                    Text("Receive:")

                    val cardNamesThatWillBeReceived = mutableListOf<String>()

                    val trader = findPlayer(gameObject, trade.traderName)
                    if (trade.cardsToGive.hand != null) {
                        for (index in trade.cardsToGive.hand) {
                            cardNamesThatWillBeReceived.add(trader.hand[index].name)
                        }
                    }
                    if (trade.cardsToGive.turnedCards != null && isPlayerActive(
                            trader,
                            gameObject.activePlayerIndex!!
                        )
                    ) {
                        for (index in trade.cardsToGive.turnedCards) {
                            cardNamesThatWillBeReceived.add(gameObject.turnedCards!![index].name)
                        }
                    }

                    for (cardToReceive in cardNamesThatWillBeReceived) {
                        Text(cardToReceive)
                    }
                }//receive column
                Spacer(Modifier.weight(1f))
            }//traderow
        }//for trade
    }//column
}//viewtrades

@Composable
fun OfferTrade(
    gameViewModel: GameObjectViewModel,
    playerName: String,
    tradeeName: String,
    setOfferTradeTarget: (String) -> Unit
) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val player = findPlayer(gameObject, playerName)
    val uniqueCardsInDeck = gameObject.uniqueCardsInDeck
    val cardsInTheGame = remember(uniqueCardsInDeck) {
        UniqueCardsInDeck::class.memberProperties
            .mapNotNull { it.getter.call(uniqueCardsInDeck) as Card? }
    }
    val selectedCardQuantities = remember { mutableStateMapOf<Card, Int>() }
    val (selectedHandIndexes, setSelectedHandIndexes) = remember { mutableStateOf(setOf<Int>()) }
    val (selectedTurnedCardIndexes, setSelectedTurnedCardIndexes) = remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            FilledIconButton(onClick = { setOfferTradeTarget("") }) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
        }//close button row

        Row(

        ) {
            Text(
                text = "Cards to request",
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 5.dp),
            )
        }//text row

        Spacer(Modifier.height(10.dp))
        Row (
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 5.dp),
        ) {
            Spacer(Modifier.width(10.dp))
            for (card in cardsInTheGame) {
                val quantity = selectedCardQuantities[card] ?: 0
                CardRequestSelector(
                    card,
                    quantity,
                    { newAmount -> selectedCardQuantities[card] = newAmount })
                Spacer(Modifier.width(10.dp))
            }
        }// request cards row

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Cards to give",
            fontSize = 30.sp,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Select from your hand",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp),
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 5.dp),
        ) {
            for ((index, card) in player.hand.withIndex()) {
                SelectableCard(card, isSelected = index in selectedHandIndexes, {
                    val newSelectedHand = selectedHandIndexes.toMutableSet()
                    if (index in newSelectedHand) newSelectedHand.remove(index) else newSelectedHand.add(
                        index
                    )
                    setSelectedHandIndexes(newSelectedHand)
                })
                Spacer(modifier = Modifier.width(10.dp))
            }
        }//hand row

        if (gameObject.activePlayerIndex == player.index) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Select from turned cards",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 5.dp),
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 5.dp),
            ) {
                for ((index, card) in gameObject.turnedCards!!.withIndex()) {
                    SelectableCard(card, isSelected = index in selectedTurnedCardIndexes, {
                        val newSelectedTurnedCards = selectedTurnedCardIndexes.toMutableSet()
                        if (index in newSelectedTurnedCards) newSelectedTurnedCards.remove(index) else newSelectedTurnedCards.add(
                            index
                        )
                        setSelectedTurnedCardIndexes(newSelectedTurnedCards)
                    })
                    Spacer(modifier = Modifier.width(10.dp))
                }//turnedCards
            }//row
        }//turned cards

        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = {
                val chosenCardsToGive = CardsToGive(
                    hand = selectedHandIndexes.toList(),
                    turnedCards = selectedTurnedCardIndexes.toList()
                )
                val chosenCardsToReceive = mutableListOf<String>()
                val actualMap = selectedCardQuantities.toMap()
                for ((key, value) in actualMap.entries) {
                    for (i in 1..value) {
                        chosenCardsToReceive.add(key.name)
                    }
                }
                gameViewModel.offerTrade(
                    OfferTradeRequest(playerName, tradeeName, chosenCardsToGive, chosenCardsToReceive )
                )
                setOfferTradeTarget("")
            },
            content = {
                Text(
                    text = "Offer Trade",
                    fontSize = 20.sp
                )
            },
            colors = ButtonDefaults.buttonColors(
                contentColor = Color(red = 0.1f, green = 0.6f, blue = 0.3f),
                containerColor = Color(0.85f, 0.85f, 0.85f),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp),

        )//offer button
    }//column

}//offertrade

@Composable
fun CardRequestSelector(card: Card, amount: Int, setAmount: (Int) -> Unit) {
    Column() {
        CardComposable(
            card = card, Modifier
                .height(105.dp)
                .width(75.dp)
        )
        Row(
            modifier = Modifier.width(75.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp)
                    .background(Color.Red)
                    .clickable {
                        if (amount > 0) {
                            setAmount(amount - 1)
                        }
                    }
            ) {
                Text(
                    text = "-",
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                )
            }// decrease

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp)
            ) {
                Text(
                    text = "$amount",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                )
            }// amount

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(25.dp)
                    .height(25.dp)
                    .background(Color.Green)
                    .clickable {
                        setAmount(amount + 1)
                    }
            ) {
                Text(
                    text = "+",
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                )
            }//increase
        }//amount control row
    }//column
}//cardrequestselector

@Composable
fun OtherPlayer(
    otherPlayer: Player,
    gameViewModel: GameObjectViewModel,
    thisPlayer: Player,
    setOfferTradeTarget: (String) -> Unit
) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.LightGray),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            if (otherPlayer.index == gameObject.activePlayerIndex) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Green
                )//icon
            }
            Text(
                text = otherPlayer.name,
                fontSize = 30.sp,
            )
        }//toprow

        Row(

        ) {
            Spacer(Modifier.width(1.dp))
            HiddenCardStack(otherPlayer.hand.size)
            Spacer(Modifier.weight(1f))
            PlayerFields(otherPlayer.fields, null, null, {}, gameViewModel)
            Spacer(Modifier.weight(1f))
            CardsToPlantNow(otherPlayer.cardsToPlantNow, null, null, { }, gameObject)
            Spacer(Modifier.width(1.dp))

        }//Cards

        val canOfferTrade =
            (isPlayerActive(otherPlayer, gameObject.activePlayerIndex!!) || isPlayerActive(
                thisPlayer,
                gameObject.activePlayerIndex!!
            )) && gameObject.phase == "trade"
        if (canOfferTrade) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .offset { IntOffset(0, -120) }
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                Spacer(modifier = Modifier.width(5.dp))
                OutlinedButton(
                    onClick = {
                        println(otherPlayer.name)
                        setOfferTradeTarget(otherPlayer.name)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                    ),
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Offer trade",
                        textAlign = TextAlign.Center
                    )
                }//button
            }//row
        }//canoffertrade
    }//column
}//otherplayer

@Composable
fun PlayArea(
    gameViewModel: GameObjectViewModel,
    playerName: String,
    setShowTrades: (Boolean) -> Unit
) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val player = findPlayer(gameObject, playerName)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Spacer(modifier = Modifier.width(5.dp))
        Column(
            modifier = Modifier
                .width(70.dp)
        ) {
            if (gameObject.phase == "plant" && isPlayerActive(
                    player,
                    gameObject.activePlayerIndex!!
                ) && (player.plantedThisTurn == null || player.plantedThisTurn > 0 || player.hand.size == 0)
            ) {
                OutlinedButton(
                    onClick = {
                        gameViewModel.turn()
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Finish Planting",
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (gameObject.phase == "trade" && isPlayerActive(
                    player,
                    gameObject.activePlayerIndex!!
                ) && (player.plantedThisTurn == null || player.plantedThisTurn > 0)
            ) {
                OutlinedButton(
                    onClick = {
                        gameViewModel.endTrading()
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Finish Trading",
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (gameObject.phase == "trade") {
                val tradesForPlayer =
                    gameObject.activeTrades!!.filter { it.tradeeName == playerName }
                if (tradesForPlayer.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { setShowTrades(true) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "View Trades",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }//buttons
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Draw")
            HiddenCardStack(gameObject.draw!!.size)
        }//draw

        Spacer(modifier = Modifier.width(10.dp))
        Column() {
            Text("Discard")
            HiddenCardStack(gameObject.discard!!.size)
        }//discard
        Spacer(modifier = Modifier.width(20.dp))

        val turnedCards = gameObject.turnedCards
        if (turnedCards != null && turnedCards.isNotEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Turned Cards")
                Row() {
                    turnedCards.forEach { card ->
                        CardComposable(
                            card, Modifier
                                .height(101.dp)
                                .width(71.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }//row
            }//column
        }//turnedcards
    }//row
}//playarea

@Composable
fun PlayerView(gameViewModel: GameObjectViewModel, playerName: String) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val player = findPlayer(gameObject, playerName)
    val (selectedCardIndex, setSelectedCardIndex) = remember { mutableStateOf<Int?>(null) }

    Row(
        modifier = Modifier
            .background(Color(0.7f, 0.9f, 0.7f))
            .height(200.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerFields(player.fields, player, selectedCardIndex, setSelectedCardIndex, gameViewModel)
        Spacer(Modifier.width(15.dp))
        CardsToPlantNow(
            player.cardsToPlantNow,
            player,
            selectedCardIndex,
            setSelectedCardIndex,
            gameObject,
        )
        Spacer(Modifier.width(1.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Money")
            HiddenCardStack(player.money)
        }
    }
}

@Composable
fun PlayerHandView(gameViewModel: GameObjectViewModel, playerName: String) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val player = findPlayer(gameObject, playerName)

    val animatedWidth = remember { Animatable(1f) }
    val (shouldAnimate, setShouldAnimate) = remember { mutableStateOf(true) }

    // from chatGPT
    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            while (true) {
                animatedWidth.animateTo(
                    targetValue = 8f,
                    animationSpec = tween(600, easing = LinearEasing)
                )
                animatedWidth.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(600, easing = LinearEasing)
                )
            }
        } else {
            animatedWidth.snapTo(1f)
        }
    }

    Row(
        modifier = Modifier
            .background(Color(0.95f, 0.95f, 0.95f))
            .fillMaxWidth()
            .height(150.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        player.hand.forEachIndexed { index, card ->
            val isPlantable = (index == 0 && isPlayerActive(
                player,
                gameObject.activePlayerIndex!!
            ) && gameObject.phase == "plant" && (player.plantedThisTurn == null || player.plantedThisTurn < 2))
            Box(
                modifier = Modifier
                    .width(75.dp)
                    .height(105.dp)
                    .drawBehind {
                        if (isPlantable) {
                            val strokeWidth = animatedWidth.value
                            val halfStroke = strokeWidth / 2
                            drawRoundRect(
                                color = Color.Red,
                                topLeft = Offset(halfStroke, halfStroke),
                                size = Size(
                                    size.width - strokeWidth,
                                    size.height - strokeWidth
                                ),
                                style = Stroke(width = strokeWidth)
                            )
                        }//isplantable
                    },
                contentAlignment = Alignment.Center
            ) {
                if (index == 0 && isPlayerActive(
                        player,
                        gameObject.activePlayerIndex!!
                    ) && gameObject.phase == "plant"
                ) {
                    CardComposable(
                        card, Modifier
                            .height(101.dp)
                            .width(71.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                } else {
                    CardComposable(
                        card, Modifier
                            .height(101.dp)
                            .width(71.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }//box
        }//foreachindexed
        Spacer(modifier = Modifier.width(10.dp))
    }//row
}//playerhandview

@Composable
fun PlayerFields(
    fields: List<Field>,
    player: Player?,
    selectedCardsToPlantNowIndexes: Int?,
    setSelectedCardsToPlantNowIndexes: (Int?) -> Unit,
    gameViewModel: GameObjectViewModel
) {
    for ((index, field) in fields.withIndex()) {
        PlantedCardView(
            field,
            index,
            player,
            selectedCardsToPlantNowIndexes,
            setSelectedCardsToPlantNowIndexes,
            gameViewModel
        )
    }
}

@Composable
fun CardsToPlantNow(
    cardsToPlantNow: List<Card>,
    player: Player?,
    selectedCardsToPlantNowIndexes: Int?,
    setSelectedCardIndex: (Int) -> Unit,
    gameObject: GameObject
) {
    val isThisPlayer = player != null

    Column(
        modifier = Modifier
            .width(100.dp)
            .height(180.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        for ((index, cardToPlantNow) in cardsToPlantNow.withIndex()) {
            Row {
                if (isThisPlayer && gameObject.phase == "end") {
                    SelectableCard(
                        cardToPlantNow,
                        isSelected = selectedCardsToPlantNowIndexes == index,
                        onClick = { setSelectedCardIndex(index) },
                        Modifier.rotate(-90.0f),
                    )
                } else {
                    CardComposable(card = cardToPlantNow, Modifier.rotate(-90.0f))
                }
            }
        }
    }
}

@Composable
fun PlantedCardView(
    field: Field,
    index: Int,
    player: Player?,
    selectedCardsToPlantNowIndex: Int?,
    setSelectedCardsToPlantNowIndex: (Int?) -> Unit,
    gameViewModel: GameObjectViewModel
) {
    val animatedWidth = remember { Animatable(1f) }
    val (shouldAnimate, setShouldAnimate) = remember { mutableStateOf(true) }
    var (cardOffset, setCardOffset) = remember { mutableStateOf(0f) }


    val gameObject = gameViewModel.gameObject!!
    val isThisPlayer = player != null

    // from chatGPT
    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            while (true) {
                animatedWidth.animateTo(
                    targetValue = 8f,
                    animationSpec = tween(600, easing = LinearEasing)
                )
                animatedWidth.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(600, easing = LinearEasing)
                )
            }
        } else {
            animatedWidth.snapTo(1f)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(0.dp)
    ) {
        Text(text = field.amount.toString())
        val isFirstCardFromHandPlantable = isThisPlayer && (isPlayerActive(
            player!!,
            gameObject.activePlayerIndex!!
        ) && gameObject.phase == "plant"
                && (field.card?.name == player.hand[0].name || field.card == null)
                && (player.plantedThisTurn == null || player.plantedThisTurn < 2)
                )
        val isSelectedCardToPlantNowPlantable = isThisPlayer && gameObject.phase == "end"
                && (selectedCardsToPlantNowIndex != null && (field.card?.name == player!!.cardsToPlantNow[selectedCardsToPlantNowIndex!!].name || field.card == null))
        val isPlantable = isFirstCardFromHandPlantable || isSelectedCardToPlantNowPlantable

        Box(
            modifier = Modifier
                .width(75.dp)
                .height(105.dp)
                .clickable {
                    if (isPlantable) {
                        if (gameObject.phase == "plant") {
                            gameViewModel.plantFromHand(FieldIndex(index))
                        } else if (gameObject.phase == "end") {
                            gameViewModel.plantFromPlantNow(
                                PlantFromPlantNowRequest(
                                    index,
                                    player!!.name,
                                    player.cardsToPlantNow[selectedCardsToPlantNowIndex!!].name
                                )
                            )
                            setSelectedCardsToPlantNowIndex(null)
                        }
                    }
                }
                .drawBehind {
                    if (isPlantable) {
                        val strokeWidth = animatedWidth.value
                        val halfStroke = strokeWidth / 2
                        drawRoundRect(
                            color = Color.Red,
                            topLeft = Offset(halfStroke, halfStroke),
                            size = Size(
                                size.width - strokeWidth,
                                size.height - strokeWidth
                            ),
                            style = Stroke(width = strokeWidth)
                        )
                    }//isplantable
                },
            contentAlignment = Alignment.Center
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(70.dp)
                        .height(30.dp)
                        .background(Color(red = 0.9f, green = 0.61f, blue = 0.19f))
                        .clickable {
                            if (field.card != null && player != null) {
                                gameViewModel.harvest(HarvestRequest(player.name, index))
                                setCardOffset(0f)
                            }
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Harvest")
                }
                CardComposable(
                    field.card,
                    Modifier
                        .width(70.dp)
                        .height(98.dp)
                        .offset { IntOffset(0, cardOffset.roundToInt()) }
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                var protectedBeanClause = false
                                if (field.amount == 1) {
                                    player!!.fields.forEachIndexed { thisIndex, thisField ->
                                        if (thisIndex != index && thisField.amount > 1) {
                                            protectedBeanClause = true
                                        }
                                    }
                                }
                                if (field.card != null && !protectedBeanClause) {
                                    if (cardOffset + delta < 0) {
                                        setCardOffset(0f)
                                    } else if (cardOffset + delta > 100) {
                                        setCardOffset(100f)
                                    } else {
                                        setCardOffset(cardOffset + delta)
                                    }
                                }
                            }//state
                        ),//draggable
                )//cardcomposable
            }//box for harvest
        }//box
    }//column
}//plantedcardview

@Composable
fun CardComposable(card: Card?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFAAAAAA)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (card == null) {
            Text(
                text = "Empty",
                textAlign = TextAlign.Center
            )
        } else {
            InnerCardTexts(card)
        }
    }
}//CardComposable

@Composable
fun SelectableCard(
    card: Card,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(70.dp)
            .height(98.dp)
            .clickable {
                onClick()
            },
        color = if (isSelected) Color.Magenta else Color.LightGray,
        contentColor = Color.Black
    ) {
        InnerCardTexts(card)
    }
}//selectablecard

@Composable
fun InnerCardTexts(card: Card) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(70.dp)
            .height(98.dp)
    ) {
        Text(text = card.name, textAlign = TextAlign.Center)
        Text(text = card.amountInDeck.toString(), textAlign = TextAlign.Center)
        Text(
            text = card.amountToMoney.joinToString(separator = " "),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(0.dp)
        )
    }
}

@Composable
fun HiddenCardStack(amount: Int) {
    Column(
        modifier = Modifier
            .width(70.dp)
            .height(98.dp)
            .background(Color(0.5f, 0.5f, 0.5f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = amount.toString(),
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
        )
    }
}//hiddenCardStack

fun findPlayer(gameObject: GameObject, playerName: String): Player {
    return gameObject.players.find { it.name == playerName }!!
}

fun isPlayerActive(player: Player, activePlayerIndex: Int): Boolean {
    return player.index == activePlayerIndex
}