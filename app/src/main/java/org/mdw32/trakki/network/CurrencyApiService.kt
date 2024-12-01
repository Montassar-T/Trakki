package org.mdw32.trakki.network

import retrofit2.Call
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("latest/USD")  // API endpoint for fetching exchange rates with USD as the base
    fun getExchangeRates(): Call<CurrencyResponse>
}
