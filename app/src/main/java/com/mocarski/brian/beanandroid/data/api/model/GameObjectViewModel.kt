package com.mocarski.apidemo2.data.api.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocarski.brian.beanandroid.data.api.Api
import com.mocarski.brian.beanandroid.data.api.model.GameObject
import com.mocarski.brian.beanandroid.data.api.model.PlayerWithName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameObjectViewModel: ViewModel() {
    private var _gameObject = mutableStateOf<GameObject?>(null)
    val gameObject: GameObject? get() = _gameObject.value

    var errorMessage: String by mutableStateOf("")

    fun getGameObject() {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            while (true) {
                try {
                    println("Getting game")
                    _gameObject.value = apiService.getGame(gameObject?.gameId!!)
                } catch (e: Exception) {
                    println(e.message.toString())
                    errorMessage = e.message.toString()
                }
                delay(5000)
            }
        }//launch
    }//getGameObject

    fun createGame (name: String) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("Creating game")
                _gameObject.value = apiService.createGame(PlayerWithName(name))
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
                _gameObject.value = apiService.joinGame(gameCode, PlayerWithName(name))
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//joinGame

    fun startGame (gameId: String) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                println("")
                _gameObject.value = apiService.startGame(gameId)
            } catch (e: Exception) {
                println(e.message.toString())
                errorMessage = e.message.toString()
            }
        }//launch
    }//

//    fun a (gameCode: String, name: String) {
//        viewModelScope.launch {
//            val apiService = Api.getInstance()
//            try {
//                println("")
//                _gameObject.value = apiService.
//            } catch (e: Exception) {
//                println(e.message.toString())
//                errorMessage = e.message.toString()
//            }
//        }//launch
//    }//

}//gameobjectviewmodel