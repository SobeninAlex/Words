package com.example.words.network

import com.example.words.network.Word
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface WordApi {

    @Headers("X-Api-Key: +wJpjHeDVvlz4hjRWkz79g==2LDyl1sC31QhzLhq")
    @GET("v1/randomword")
    suspend fun getRandomWord(): Response<Word>

}