package org.mdw32.trakki.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.mdw32.trakki.R
import org.mdw32.trakki.models.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val transactionList: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        // Set transaction name
        holder.name.text = transaction.name

        // Convert Timestamp to a formatted date string
        val formattedDate = formatDate(transaction.date)
        holder.date.text = formattedDate

        // Set the amount
        holder.amount.text = "${transaction.amount} ${transaction.currency}"

        // Set the circle icon color based on income or expense
        if (transaction.type == "Income") {
            holder.circleIcon.setBackgroundResource(R.drawable.circle_green)
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green)) // Green for income
        } else {
            holder.circleIcon.setBackgroundResource(R.drawable.circle_red)
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red)) // Red for expense
        }

        // Keep the arrow image as it is (if you need it)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    // Function to format the Firestore Timestamp to a readable date string
    private fun formatDate(timestamp: com.google.firebase.Timestamp?): String {
        return if (timestamp != null) {
            val date = timestamp.toDate() // Convert Timestamp to Date
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // You can change the date format as needed
            sdf.format(date) // Return formatted date as String
        } else {
            "Unknown Date"
        }
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleIcon: View = itemView.findViewById(R.id.circle_icon)
        val name: TextView = itemView.findViewById(R.id.name)
        val date: TextView = itemView.findViewById(R.id.date)
        val amount: TextView = itemView.findViewById(R.id.amount)
    }
}
