package com.mocarski.brian.beanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocarski.apidemo2.data.api.model.GameObjectViewModel
import com.mocarski.apidemo2.data.api.model.ShownScreen
import com.mocarski.brian.beanandroid.data.api.model.Card
import com.mocarski.brian.beanandroid.data.api.model.Field
import com.mocarski.brian.beanandroid.data.api.model.GameObject
import com.mocarski.brian.beanandroid.data.api.model.Player
import com.mocarski.brian.beanandroid.data.api.model.Trade
import com.mocarski.brian.beanandroid.ui.OfferTrade
import com.mocarski.brian.beanandroid.ui.OtherPlayer
import com.mocarski.brian.beanandroid.ui.PlayArea
import com.mocarski.brian.beanandroid.ui.PlayerHandView
import com.mocarski.brian.beanandroid.ui.PlayerView
import com.mocarski.brian.beanandroid.ui.ViewTrades
import com.mocarski.brian.beanandroid.ui.theme.BeanAndroidTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel by viewModels<GameObjectViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val (name, setName) = remember { mutableStateOf("z") }
            val (gameCode, setGameCode) = remember { mutableStateOf("") }

            BeanAndroidTheme {
                when (gameViewModel.shownScreen) {
                    ShownScreen.CREATE -> {
                        CreateView(gameViewModel, name, setName, gameCode, setGameCode)
                    }

                    ShownScreen.JOIN -> {
                        JoinView(gameViewModel)
                    }

                    ShownScreen.GAME -> {
                        GameView(gameViewModel, name)
                    }
                }//when
            }//beanandroidtheme
        }//setcontent
    }//oncreate
}//mainactivity

@Composable
fun CreateView(
    gameViewModel: GameObjectViewModel,
    name: String,
    setName: (String) -> Unit,
    gameCode: String,
    setGameCode: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(
            text = "Bean Game!",
            fontSize = 30.sp
        )
        TextField(value = name, onValueChange = setName, label = {
            Text(text = "Name")
        })
        Button(onClick = {
            gameViewModel.createGame(name)
        }) {
            Text(text = "Create Game")
        }
        Row(
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = {
                gameViewModel.joinGame(gameCode, name)
            }) {
                Text(text = "Join Game:")
            }
            TextField(
                value = gameCode,
                onValueChange = setGameCode,
                label = {
                    Text(text = "Game Code")
                },
                modifier = Modifier.padding()
            )
        }//row
    }//column
}//createview


@Composable
fun JoinView(gameViewModel: GameObjectViewModel) {
    gameViewModel.startGamePolling()
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Setup!",
            fontSize = 36.sp
        )
        Spacer(Modifier.height(30.dp))
        Text(
            text = "Game Code: ${gameViewModel.gameObject!!.gameCode}",
            fontSize = 30.sp
        )
        Spacer(Modifier.height(30.dp))


        for (player in gameViewModel.gameObject!!.players) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentColor = Color.Black,
                shape = RectangleShape,
                color = Color.LightGray,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = player.name,
                        fontSize = 30.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(30.dp))
        Button(onClick = {
            gameViewModel.startGame()
        }) {
            Text(text = "Start Game")
        }
    }//column
}//joinview

@Composable
fun GameView(gameViewModel: GameObjectViewModel, playerName: String) {
    gameViewModel.startGamePolling()
    val (showTrades, setShowTrades) = remember { mutableStateOf(false) }
    val (offerTradeTarget, setOfferTradeTarget) = remember { mutableStateOf("") }

    if (showTrades) {
        ViewTrades(gameViewModel, playerName, setShowTrades)
        return
    }//showTrades

    if (offerTradeTarget.isNotEmpty()) {
        OfferTrade(gameViewModel, playerName, offerTradeTarget, setOfferTradeTarget)
        return
    }//showOfferTrade

    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .height(400.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            val otherPlayers = gameViewModel.gameObject!!.players.filter { it.name != playerName }
            val thisPlayer = gameViewModel.gameObject!!.players.find { it.name == playerName }
            for (otherPlayer in otherPlayers) {
                OtherPlayer(otherPlayer, gameViewModel, thisPlayer!!, setOfferTradeTarget)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            PlayArea(gameViewModel, playerName, setShowTrades)
            Spacer(modifier = Modifier.height(10.dp))
            PlayerView(gameViewModel, playerName)
            Spacer(modifier = Modifier.height(10.dp))
            PlayerHandView(gameViewModel, playerName)
        }
    }
}