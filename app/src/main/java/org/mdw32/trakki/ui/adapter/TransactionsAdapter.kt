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
class TransactionAdapter(private val transactionList: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        // Set transaction name and date
        holder.name.text = transaction.name
        holder.date.text = transaction.date

        // Set the amount
        holder.amount.text = "${transaction.amount} DT"

        // Set the circle icon color based on income or expense
        if (transaction.type == "income") {
            holder.circleIcon.setBackgroundResource(R.drawable.circle_green)
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green)) // Green for income
        } else {
            holder.circleIcon.setBackgroundResource(R.drawable.circle_red)
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red)) // Red for expense
        }

        // Keep the arrow image as it is

    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val circleIcon: View = itemView.findViewById(R.id.circle_icon)
        val name: TextView = itemView.findViewById(R.id.name)
        val date: TextView = itemView.findViewById(R.id.date)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val arrow: ImageView = itemView.findViewById(R.id.arrow_income)
    }
}
