package com.mocarski.brian.beanandroid.data.api

import com.mocarski.brian.beanandroid.data.api.model.AcceptTradeRequest
import com.mocarski.brian.beanandroid.data.api.model.FieldIndex
import com.mocarski.brian.beanandroid.data.api.model.GameObject
import com.mocarski.brian.beanandroid.data.api.model.GameResponse
import com.mocarski.brian.beanandroid.data.api.model.HarvestRequest
import com.mocarski.brian.beanandroid.data.api.model.OfferTradeRequest
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
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

const val BASE_URL = "https://brimocars-bean-game-13b7ef772680.herokuapp.com/"

interface Api {
    @GET("game")
    suspend fun getGame(
        @Query("gameId") gameId: String
    ): GameResponse

    //setup
    @POST("setup")
    suspend fun createGame(
        @Body body: PlayerWithName
    ): GameResponse

    @POST("setup")
    suspend fun joinGame(
        @Query("gameCode") gameCode: String,
        @Body body: PlayerWithName
    ): GameResponse

    @POST("setup/start")
    suspend fun startGame(
        @Query("gameId") gameId: String,
    ): GameResponse

    @DELETE("setup/leave")
    suspend fun leaveGame(
        @Query("gameId") gameId: String,
        @Body body: PlayerWithName
    ): GameResponse

    @DELETE("setup")
    suspend fun deleteGame(
        @Query("gameId") gameId: String,
    ): GameResponse

    //play
    @POST("play/plantFromHand")
    suspend fun plantFromHand(
        @Query("gameId") gameId: String,
        @Body body: FieldIndex
    ): GameResponse

    @POST("play/turn")
    suspend fun turn(
        @Query("gameId") gameId: String,
    ): GameResponse

    @POST("play/trade/offer")
    suspend fun offerTrade(
        @Query("gameId") gameId: String,
        @Body body: OfferTradeRequest
    ): GameResponse

    @POST("play/trade/accept")
    suspend fun acceptTrade(
        @Query("gameId") gameId: String,
        @Body body: AcceptTradeRequest
    ): GameResponse

    // retrofit is dumb and doesn't let me send a body in a delete method. This is a workaround.
    @HTTP(method = "DELETE", path = "play/trade", hasBody = true)
    suspend fun denyTrade(
        @Query("gameId") gameId: String,
        @Body body: TradeId
    ): GameResponse

    @POST("play/trade/end")
    suspend fun endTrading(
        @Query("gameId") gameId: String,
    ): GameResponse

    @POST("play/harvest")
    suspend fun harvest(
        @Query("gameId") gameId: String,
        @Body body: HarvestRequest
    ): GameResponse

    @POST("play/plantFromPlantNow")
    suspend fun plantFromPlantNow(
        @Query("gameId") gameId: String,
        @Body body: PlantFromPlantNowRequest
    ): GameResponse

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