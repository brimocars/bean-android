package com.mocarski.brian.beanandroid.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mocarski.apidemo2.data.api.model.GameObjectViewModel
import com.mocarski.brian.beanandroid.data.api.model.AcceptTradeRequest
import com.mocarski.brian.beanandroid.data.api.model.Card
import com.mocarski.brian.beanandroid.data.api.model.CardsToGive
import com.mocarski.brian.beanandroid.data.api.model.Field
import com.mocarski.brian.beanandroid.data.api.model.GameObject
import com.mocarski.brian.beanandroid.data.api.model.Player
import com.mocarski.brian.beanandroid.data.api.model.TradeId
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun ViewTrades(
    gameViewModel: GameObjectViewModel,
    playerName: String,
    setShowTrades: (Boolean) -> Unit
) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val (selectedCardsToReceive, setSelectedCardsToReceive) = remember {
        mutableStateOf(
            CardsToGive(
                null,
                null
            )
        )
    }
    val player = findPlayer(gameObject, playerName)

    Column() {
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

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {
            for (card in player.hand) {
                SelectableCard(cardToSelectableCard(card))
            }
        }//hand row

        if (gameObject.activePlayerIndex == player.index) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
            ) {

            }//turned row
        }//turned cards

        val tradesForPlayer = gameObject.activeTrades!!.filter { it.tradee == playerName }
        if (tradesForPlayer.isEmpty()) {
            setShowTrades(false)
        }
        for (trade in tradesForPlayer) {
            Row {
                Column {
                    Button(
                        onClick = {
                            gameViewModel.acceptTrade(
                                AcceptTradeRequest(
                                    trade.tradeId,
                                    selectedCardsToReceive
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Blue
                        )
                    ) {
                        Text("Accept Trade")
                    }//accept button
                    Button(
                        onClick = {
                            gameViewModel.denyTrade(TradeId(trade.tradeId))
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Deny Trade")
                    }//deny button
                }//buttons
            }//traderow
        }//for trade
    }//column
}//viewtrades

@Composable
fun OtherPlayer(otherPlayer: Player, gameViewModel: GameObjectViewModel) {
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
        ) {
            if (otherPlayer.index == gameObject.activePlayerIndex) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Green
                )//icon
            }
            Text(text = otherPlayer.name)
        }//toprow

        Row(

        ) {
            HiddenCardStack(otherPlayer.hand.size)
            Spacer(Modifier.width(30.dp))
            PlayerFields(otherPlayer.fields)
        }//Cards
    }
}

@Composable
fun PlayArea(
    gameViewModel: GameObjectViewModel,
    playerName: String,
    setShowTrades: (Boolean) -> Unit
) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    Row() {
        Column() {
            if (gameObject.phase == "trade") {
                OutlinedButton(onClick = {
                    gameViewModel.turn()
                }) {
                    Text("Finish Planting")
                }
                val tradesForPlayer = gameObject.activeTrades!!.filter { it.tradee == playerName }
                if (tradesForPlayer.isNotEmpty()) {
                    OutlinedButton(onClick = { setShowTrades(true) }) {
                        Text("View Trades")
                    }
                }
            }
        }//buttons

        Column {
            Text("Draw")
            HiddenCardStack(gameObject.draw!!.size)
        }
        Column {
            Text("Discard")
            HiddenCardStack(gameObject.discard!!.size)
        }
    }
}

@Composable
fun PlayerView(gameViewModel: GameObjectViewModel, playerName: String) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val player = findPlayer(gameObject, playerName)

    Row(
        modifier = Modifier
            .background(Color.Green)
            .fillMaxWidth()
    ) {
        PlayerFields(player.fields)
        Spacer(Modifier.width(30.dp))
        Column {
            Text("Money")
            HiddenCardStack(player.money)
        }
    }
}

@Composable
fun PlayerHandView(gameViewModel: GameObjectViewModel, playerName: String) {
    val gameObject: GameObject = gameViewModel.gameObject!!
    val player = findPlayer(gameObject, playerName)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        player.hand.forEachIndexed { index, card ->
            if (index == 0 && isPlayerActive(
                    player,
                    gameObject.activePlayerIndex!!
                ) && gameObject.phase == "plant"
            ) {
                Card(
                    card, Modifier
                        .animateContentSize()
                        .height(77.dp) //add animation thing here
                        .width(55.dp)
                )
            } else {
                Card(
                    card, Modifier
                        .animateContentSize()
                        .height(77.dp) //add animation thing here
                        .width(55.dp)
                )
            }
        }//foreachindexed
    }//row
}//playerhandview

@Composable
fun PlayerFields(fields: List<Field>) {
    for (field in fields) {
        PlantedCardView(field)
    }
}

@Composable
fun PlantedCardView(field: Field) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(5.dp)
    ) {
        Text(text = field.amount.toString())
        Card(
            field.card,
            Modifier
                .width(55.dp)
                .height(77.dp),
        )
    }
}

@Composable
fun Card(card: Card?, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFAAAAAA),
        contentColor = Color.Black
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
}//card

@Composable
fun SelectableCard(card: SelectableCard) {
    Surface(
        modifier = Modifier
            .width(55.dp)
            .height(77.dp)
            .clickable { card.isSelected = !card.isSelected },
        color = Color.Magenta,
        contentColor = Color.Black
    ) {
        InnerCardTexts(selectableCardToCard(card))
    }
}//selectablecard

@Composable
fun InnerCardTexts(card: Card) {
    Column {
        Text(text = card.name)
        Text(text = card.amountInDeck.toString())
        Text(text = card.amountToMoney.joinToString(separator = " "))
    }
}

@Composable
fun HiddenCardStack(amount: Int) {
    Surface(
        modifier = Modifier
            .width(55.dp)
            .height(77.dp),
        color = Color.DarkGray,
        contentColor = Color.Black
    ) {
        Text(
            text = amount.toString(),
            textAlign = TextAlign.Center
        )
    }
}//hiddenCardStack

fun cardToSelectableCard(card: Card): SelectableCard {
    return SelectableCard(card.amountInDeck, card.amountToMoney, card.name, false)
}

fun selectableCardToCard(card: SelectableCard): Card {
    return Card(card.amountInDeck, card.amountToMoney, card.name)
}

fun findPlayer(gameObject: GameObject, playerName: String): Player {
    return gameObject.players.find { it.name == playerName }!!
}

fun isPlayerActive(player: Player, activePlayerIndex: Int): Boolean {
    return player.index == activePlayerIndex
}