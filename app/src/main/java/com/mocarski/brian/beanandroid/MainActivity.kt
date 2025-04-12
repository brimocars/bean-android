package com.mocarski.brian.beanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mocarski.apidemo2.data.api.model.GameObjectViewModel
import com.mocarski.brian.beanandroid.ui.theme.BeanAndroidTheme

enum class ShownScreen {
    CREATE,
    JOIN,
    GAME,
}

class MainActivity : ComponentActivity() {
    private val gameViewModel by viewModels<GameObjectViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var shownScreen by remember { mutableStateOf(ShownScreen.CREATE) }
            val (name, setName) = remember { mutableStateOf("z") }
            val (gameCode, setGameCode) = remember { mutableStateOf("") }

            BeanAndroidTheme {
                when (shownScreen) {
                    ShownScreen.CREATE -> {
                        CreateView(gameViewModel, name, setName, gameCode, setGameCode)
                    }

                    ShownScreen.JOIN -> {
                        JoinView(gameViewModel)//, accessCode, name)
                    }

                    ShownScreen.GAME -> {
                        GameView()
                    }
                }//when
            }//beanandroidtheme
        }//setcontent
    }//oncreate
}//mainactivity

@Composable
fun CreateView(gameViewModel: GameObjectViewModel, name: String, setName: (String) -> Unit, gameCode: String, setGameCode: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(text = "Bean Game!")
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
    Column() {
        Text(text = "Game Setup!")
        Text(text = "Game Code: ")
        //List of players on gameObject
        Button(onClick = {
            gameViewModel.startGame("1")
        }) {
            Text(text = "Start Game:")
        }
    }//column
}

@Composable
fun GameView() {
    // loop
//    OtherPlayer()
//    Play()
//    Player()
//    PlayerHand()
}
