package com.mocarski.brian.beanandroid.data.api

import com.mocarski.brian.beanandroid.data.api.model.AcceptTradeRequest
import com.mocarski.brian.beanandroid.data.api.model.FieldIndex
import com.mocarski.brian.beanandroid.data.api.model.GameObject
import com.mocarski.brian.beanandroid.data.api.model.HarvestRequest
import com.mocarski.brian.beanandroid.data.api.model.PlantFromPlantNowRequest
import com.mocarski.brian.beanandroid.data.api.model.PlayerWithName
import com.mocarski.brian.beanandroid.data.api.model.Trade
import com.mocarski.brian.beanandroid.data.api.model.TradeId
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val BASE_URL = "https://brimocars-bean-game-13b7ef772680.herokuapp.com/"

interface Api {
    @GET("game")
    suspend fun getGame(gameId: String): GameObject

    //setup
    @POST("setup")
    suspend fun createGame(
        @Body body: PlayerWithName
    ): GameObject

    @POST("setup")
    suspend fun joinGame(
        @Query("gameCode") gameCode: String,
        @Body body: PlayerWithName
    ): GameObject

    @POST("setup/start")
    suspend fun startGame(
        @Query("gameId") gameId: String,
    ): GameObject

    @DELETE("setup/leave")
    suspend fun leaveGame(
        @Query("gameId") gameId: String,
        @Body body: PlayerWithName
    ): GameObject

    @DELETE("setup")
    suspend fun deleteGame(
        @Query("gameId") gameId: String,
    ): GameObject

    //play
    @POST("play/plantFromHand")
    suspend fun plantFromHand(
        @Query("gameId") gameId: String,
        @Body body: FieldIndex
    ): GameObject

    @POST("play/turn")
    suspend fun turn(
        @Query("gameId") gameId: String,
    ): GameObject

    @POST("play/trade/offer")
    suspend fun offerTrade(
        @Query("gameId") gameId: String,
        @Body body: Trade
    ): GameObject

    @POST("play/trade/accept")
    suspend fun acceptTrade(
        @Query("gameId") gameId: String,
        @Body body: AcceptTradeRequest
    ): GameObject

    @DELETE("play/trade")
    suspend fun denyTrade(
        @Query("gameId") gameId: String,
        @Body body: TradeId
    ): GameObject

    @POST("play/trade/end")
    suspend fun endTrading(
        @Query("gameId") gameId: String,
    ): GameObject

    @POST("play/harvest")
    suspend fun harvest(
        @Query("gameId") gameId: String,
        @Body body: HarvestRequest
    ): GameObject

    @POST("play/plantFromPlantNow")
    suspend fun plantFromPlantNow(
        @Query("gameId") gameId: String,
        @Body body: PlantFromPlantNowRequest
    ): GameObject

    companion object {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()

        var apiService: Api? = null

        fun getInstance(): Api {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // for parsing json
                    .client(client) // for logging
                    .build().create(Api::class.java)
            }
            return apiService!!
        }//getinstance
    }//object
}//apiservice