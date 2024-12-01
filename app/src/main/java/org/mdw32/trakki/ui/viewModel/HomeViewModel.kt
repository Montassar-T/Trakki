package org.mdw32.trakki

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.mdw32.trakki.models.Transaction
import org.mdw32.trakki.network.CurrencyApiService
import org.mdw32.trakki.network.CurrencyResponse
import org.mdw32.trakki.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HomeViewModel : ViewModel() {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    private val firestore = FirebaseFirestore.getInstance()

    // LiveData for the last 5 transactions
    private val _latestTransactions = MutableLiveData<List<Transaction>>()
    val latestTransactions: LiveData<List<Transaction>> get() = _latestTransactions

    // LiveData for total spending this month in DNT
    private val _totalSpendingThisMonth = MutableLiveData<Double>()
    val totalSpendingThisMonth: LiveData<Double> get() = _totalSpendingThisMonth

    // LiveData for percentage change from the previous month in DNT
    private val _percentageChange = MutableLiveData<Double>()
    val percentageChange: LiveData<Double> get() = _percentageChange

    // LiveData for spending wallet amount in DNT (current balance)
    private val _walletAmount = MutableLiveData<Double>()
    val walletAmount: LiveData<Double> get() = _walletAmount

    // LiveData for any sync errors
    private val _syncError = MutableLiveData<String>()
    val syncError: LiveData<String> get() = _syncError


    // Function to fetch home data and process transactions
    fun fetchHomeData() {
        // Set up a real-time listener for changes in the transactions collection
        firestore.collection("transactions")
            .whereEqualTo("userId", userId) // Filter transactions for the current user
            .orderBy("date", Query.Direction.DESCENDING) // Order transactions by date
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HomeViewModel", "Error listening for transaction updates", error)
                    _syncError.postValue("Error fetching transactions: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Map documents to Transaction objects
                    val transactions = snapshot.documents.mapNotNull { it.toObject(Transaction::class.java) }
                    Log.d("HomeViewModel", "Fetched ${transactions.size} transactions")

                    // Process and update LiveData
                    getExchangeRates { exchangeRates ->
                        if (exchangeRates != null) {
                            processFinancialData(transactions, exchangeRates)
                        } else {
                            _syncError.postValue("Error fetching exchange rates")
                        }
                    }
                } else {
                    _latestTransactions.postValue(emptyList()) // Handle empty or deleted transactions
                }
            }
    }


    // Function to fetch exchange rates from the API
    public fun getExchangeRates(onResult: (CurrencyResponse?) -> Unit) {
        val service = RetrofitClient.getRetrofitInstance().create(CurrencyApiService::class.java)

        service.getExchangeRates().enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Failed to fetch exchange rates", t)
                onResult(null)
            }
        })
    }

    // Function to process financial data
    private fun processFinancialData(transactions: List<Transaction>, exchangeRates: CurrencyResponse) {
        // Fetch and post the total spending for this month
        _totalSpendingThisMonth.postValue(getTotalSpending(transactions, exchangeRates))

        // Fetch and post the percentage change in spending compared to the previous month
        _percentageChange.postValue(getPercentageChange(transactions, exchangeRates))

        // Fetch and post the wallet balance (income - expenses)
        _walletAmount.postValue(getSpendingWallet(transactions, exchangeRates))



        // Post the latest transactions
        _latestTransactions.postValue(getLatestTransactions(transactions))
    }

    // Helper methods

    private fun getTotalSpending(transactions: List<Transaction>, exchangeRates: CurrencyResponse): Double {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        return calculateTotalSpending(transactions, currentMonth, exchangeRates)
    }

    private fun getPercentageChange(transactions: List<Transaction>, exchangeRates: CurrencyResponse): Double {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val totalSpending = calculateTotalSpending(transactions, currentMonth, exchangeRates)
        val previousMonthSpending = calculateTotalSpending(transactions, currentMonth - 1, exchangeRates)
        return calculatePercentageChange(previousMonthSpending, totalSpending)
    }

    private fun getSpendingWallet(transactions: List<Transaction>, exchangeRates: CurrencyResponse): Double {
        val totalIncome = calculateTotalIncome(transactions, exchangeRates)
        val totalExpenses = calculateTotalExpenses(transactions, exchangeRates)
        return totalIncome - totalExpenses
    }

    private fun getLatestTransactions(transactions: List<Transaction>): List<Transaction> {
        return transactions.take(5)
    }

    private fun calculateTotalSpending(transactions: List<Transaction>, month: Int, exchangeRates: CurrencyResponse): Double {
        return transactions.filter {
            it.type == "Expense" && it.date?.toDate()?.let { date ->
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.get(Calendar.MONTH) == month
            } == true
        }.sumOf { convert(it.amount, it.currency, exchangeRates) }
    }

    private fun calculatePercentageChange(previousMonthSpending: Double, currentMonthSpending: Double): Double {
        return if (previousMonthSpending != 0.0) {
            ((currentMonthSpending - previousMonthSpending) / previousMonthSpending) * 100
        } else {
            0.0 // No previous month spending to compare with
        }
    }

    private fun calculateTotalIncome(transactions: List<Transaction>, exchangeRates: CurrencyResponse): Double {
        return transactions.filter { it.type == "Income" }.sumOf {
            convert(it.amount, it.currency, exchangeRates)
        }
    }

    private fun calculateTotalExpenses(transactions: List<Transaction>, exchangeRates: CurrencyResponse): Double {
        return transactions.filter { it.type == "Expense" }.sumOf {
            convert(it.amount, it.currency, exchangeRates)
        }
    }

    private fun convert(amount: Double, currency: String, exchangeRates: CurrencyResponse): Double {
        val rateToUsd = exchangeRates.conversion_rates[currency]
        return if (rateToUsd != null) {
            amount / rateToUsd
        } else {
            Log.w("HomeViewModel", "No conversion rate found for $currency, using original amount.")
            amount
        }
    }
}
