package com.mocarski.brian.beanandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mocarski.brian.beanandroid.ui.theme.BeanAndroidTheme

enum class ShownScreen {
    CREATE,
    JOIN,
    GAME,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var shownScreen by remember { mutableStateOf(ShownScreen.CREATE) }
            var name by remember { mutableStateOf("") }

            BeanAndroidTheme {
                when (shownScreen) {
                    ShownScreen.CREATE -> {
                        CreateView(name)
                    }

                    ShownScreen.JOIN -> {
                        JoinView()
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
fun CreateView(name: String) {
    Column() {
        Text(text = "Bean Game!")
        TextField(value = "z", onValueChange = {}, label = {
            Text(text = "Name")
        })
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Create Game")
        }
        Row() {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Join Game:")
            }
            TextField(value = "1", onValueChange = {}, label = {
                Text(text = "Game Code")
            })
        }//row
    }//column
}//createview

@Composable
fun JoinView() {
    Column() {
        Text(text = "Game Setup!")
        Text(text = "Game Code: ")
        //List of players on gameObject
        Button(onClick = { /*TODO*/ }) {
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
