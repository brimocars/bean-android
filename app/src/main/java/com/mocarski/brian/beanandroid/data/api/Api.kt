package com.mocarski.brian.beanandroid.data.api

import com.mocarski.brian.beanandroid.data.api.model.GameObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "https://brimocars-bean-game-13b7ef772680.herokuapp.com/"

interface Api {
    @GET("game")
    suspend fun getGame(gameId: String): GameObject

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