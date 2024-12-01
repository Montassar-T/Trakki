package org.mdw32.trakki.models

import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.Timestamp

data class Transaction(
    var userId: String = "", // The ID of the user who owns this transaction
    val name: String = "",   // The name of the transaction (e.g., Salary, Electricity Bill)
    val amount: Double = 0.0, // The amount of the transaction
    val type: String = "",   // The type of transaction (e.g., "income" or "expense")
    @ServerTimestamp val date: Timestamp? = null, // The date of the transaction as a Firestore Timestamp
    val currency: String = "" // The currency of the transaction (e.g., "USD", "EUR")
) {

    // Utility function to convert Timestamp to a readable date String
    fun getFormattedDate(): String {
        return date?.toDate()?.toString() ?: "No date"
    }
}
