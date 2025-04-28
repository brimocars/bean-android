package com.mocarski.apidemo2.data.api.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocarski.brian.beanandroid.data.api.Api
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
import com.mocarski.brian.beanandroid.data.api.model.PlayerWithName
import com.mocarski.brian.beanandroid.data.api.model.Trade
import com.mocarski.brian.beanandroid.data.api.model.TradeId
import com.mocarski.brian.beanandroid.data.api.model.UniqueCardsInDeck
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ShownScreen {
    CREATE,
    JOIN,
    GAME,
}

class GameObjectViewModel: ViewModel() {
    private var _gameObject = mutableStateOf<GameObject?>(
        null)
//        GameObject(
//        gameId = "2a226e0c-f61c-457d-b767-0d121a85a643",
//        gameCode = "1",
//        players = listOf<Player>(
//            Player(
//                name = "z",
//                maxFields = 3,
//                hand = listOf<Card>(
//                    Card(name = "black", amountToMoney = listOf<Int>(2, 4, 5, 6), amountInDeck = 10),
//                    Card(name = "red", amountToMoney = listOf<Int>(2, 3, 4, 5), amountInDeck = 8),
//                    Card(name = "coffee", amountToMoney = listOf<Int>(4, 7, 10, 12), amountInDeck = 24),
//                    Card(name = "coffee", amountToMoney = listOf<Int>(4, 7, 10, 12), amountInDeck = 24),
//                    Card(name = "red", amountToMoney = listOf<Int>(2, 3, 4, 5), amountInDeck = 8)
//                ),
//                money = 0,
//                fields = listOf(
//                    Field(0, null),
//                    Field(0, null),
//                    Field(0, null)
//                ),
//                cardsToPlantNow = emptyList(),
//                index = 0,
//                plantedThisTurn = 1,
//            ),
//            Player(
//                name = "a",
//                maxFields = 3,
//                hand = listOf(
//                    Card(name = "black", amountToMoney = listOf<Int>(2, 4, 5, 6), amountInDeck = 10),
//                    Card(name = "red", amountToMoney = listOf<Int>(2, 3, 4, 5), amountInDeck = 8),
//                    Card(name = "coffee", amountToMoney = listOf<Int>(4, 7, 10, 12), amountInDeck = 24),
//                    Card(name = "coffee", amountToMoney = listOf<Int>(4, 7, 10, 12), amountInDeck = 24),
//                    Card(name = "red", amountToMoney = listOf<Int>(2, 3, 4, 5), amountInDeck = 8)
//                ),
//                money = 0,
//                fields = listOf(
//                    Field(0, null),
//                    Field(0, null),
//                    Field(0, null)
//                ),
//                cardsToPlantNow = emptyList(),
//                index = 1,
//                plantedThisTurn = 0,
//            ),
//            Player(
//                name = "c",
//                maxFields = 3,
//                hand = listOf(
//                    Card(name = "black", amountToMoney = listOf<Int>(2, 4, 5, 6), amountInDeck = 10),
//                    Card(name = "red", amountToMoney = listOf<Int>(2, 3, 4, 5), amountInDeck = 8),
//                    Card(name = "coffee", amountToMoney = listOf<Int>(4, 7, 10, 12), amountInDeck = 24),
//                    Card(name = "coffee", amountToMoney = listOf<Int>(4, 7, 10, 12), amountInDeck = 24),
//                    Card(name = "red", amountToMoney = listOf<Int>(2, 3, 4, 5), amountInDeck = 8)
//                ),
//                money = 0,
//                fields = listOf(
//                    Field(0, null),
//                    Field(0, null),
//                    Field(0, null)
//                ),
//                cardsToPlantNow = emptyList(),
//                index = 2,
//                plantedThisTurn = 0,
//            )
//        ),
//        activePlayerIndex = 0,
//        discard = listOf<Card>(),
//        draw = listOf<Card>(),
//        activeTrades = listOf<Trade>(),
//        isOver = false,
//        phase = "plant",
//        timesShuffled = 0,
//        turnedCards = listOf<Card>(),
//        uniqueCardsInDeck = UniqueCardsInDeck(),
//        updateId = "a2b8287d-ea48-424b-8cca-99432fc6b50a",
//        winner = null,
//    ))

    val gameObject: GameObject? get() = _gameObject.value
    var isRunning = false;

    val shownScreen: ShownScreen get() =
        if (gameObject == null) {
            ShownScreen.CREATE
        } else if (gameObject!!.phase == null) {
            ShownScreen.JOIN
        } else {
            ShownScreen.GAME
        }


    var errorMessage: String by mutableStateOf("")

    fun startGamePolling() {
        if (isRunning) {
            return
        }
        isRunning = true
        viewModelScope.launch {
            val apiService = Api.getInstance()
            while (true) {
                try {
                    println("Getting game")
                    _gameObject.value = apiService.getGame(gameObject?.gameId!!).gameObject
                } catch (e: Exception) {
                    println(e.message.toString())
                    errorMessage = e.message.toString()
                }
                delay(5000)
            }
        }//launch
    }//startGamePolling

    fun createGame (name: String) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Creating game")
                _gameObject.value = apiService.createGame(PlayerWithName(name)).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()

            }
        }//launch
    }//createGame

    fun joinGame (gameCode: String, name: String) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Joining game")
                _gameObject.value = apiService.joinGame(gameCode, PlayerWithName(name)).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//joinGame

    fun startGame () {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Starting game")
                _gameObject.value = apiService.startGame(gameObject!!.gameId).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//startGame

    fun leaveGame ( name: String) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Leaving Game")
                _gameObject.value = apiService.leaveGame(gameObject!!.gameId, PlayerWithName(name)).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//leavegame

    fun deleteGame () {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Deleting game")
                _gameObject.value = apiService.deleteGame(gameObject!!.gameId).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//deletegame

    fun plantFromHand (fieldIndex: FieldIndex) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Planting from hand")
                _gameObject.value = apiService.plantFromHand(gameObject!!.gameId, fieldIndex).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//plantfromhand

    fun turn () {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Turning")
                _gameObject.value = apiService.turn(gameObject!!.gameId).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//turn

    fun offerTrade (trade: OfferTradeRequest) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Offer trade")
                _gameObject.value = apiService.offerTrade(gameObject!!.gameId, trade).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//offer

    fun acceptTrade (acceptTradeRequest: AcceptTradeRequest) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Accept trade")
                _gameObject.value = apiService.acceptTrade(gameObject!!.gameId, acceptTradeRequest).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//accepttrade

    fun denyTrade (tradeId: TradeId) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Deny trade")
                _gameObject.value = apiService.denyTrade(gameObject!!.gameId, tradeId).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//deny

    fun endTrading () {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("End trading")
                _gameObject.value = apiService.endTrading(gameObject!!.gameId).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//endTrading

    fun harvest (harvestRequest: HarvestRequest) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Harvesting")
                _gameObject.value = apiService.harvest(gameObject!!.gameId, harvestRequest).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//harvest

    fun plantFromPlantNow (plantFromPlantNowRequest: PlantFromPlantNowRequest) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Plant from plant now")
                _gameObject.value = apiService.plantFromPlantNow(gameObject!!.gameId, plantFromPlantNowRequest).gameObject
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//plantfromplantnow

}//gameobjectviewmodel