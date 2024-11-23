package org.mdw32.trakki.models

data class Transaction(
    val name: String,       // The name of the transaction (e.g., Salary, Electricity Bill)
    val amount: Double,     // The amount of the transaction
    val type: String,       // The type of transaction (e.g., "income" or "expense")
    val date: String        // The date of the transaction
)