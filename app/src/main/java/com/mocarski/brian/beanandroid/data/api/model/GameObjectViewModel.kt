package com.mocarski.apidemo2.data.api.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocarski.brian.beanandroid.data.api.Api
import com.mocarski.brian.beanandroid.data.api.model.GameObject
import kotlinx.coroutines.launch

class GameObjectViewModel: ViewModel() {
    private var _gameObject = mutableStateOf<GameObject?>(null)
    val gameObject: GameObject? get() = _gameObject.value

    var errorMessage: String by mutableStateOf("")
    var loading: Boolean by mutableStateOf(false)

    fun getGameObject(gameId: String) {
        viewModelScope.launch {
            val apiService = Api.getInstance()
            try {
                loading = true
                _gameObject.value = Api.getInstance().getGame(gameId)
            } catch (e: Exception) {
                errorMessage = e.message.toString()
            } finally {
                loading = false
            }
        }//launch
    }//getGameObject
}//gameobjectviewmodel