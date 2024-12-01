package org.mdw32.trakki.network

data class CurrencyResponse(
    val conversion_rates: Map<String, Double>  // A map holding the currency rates
)
