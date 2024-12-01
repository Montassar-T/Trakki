package org.mdw32.trakki.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object RetrofitClient {

    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/041a11f9bf30e833c8bd2248/"

    // Create a Retrofit instance
    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // For converting JSON to Kotlin objects
            .build()
    }
}
