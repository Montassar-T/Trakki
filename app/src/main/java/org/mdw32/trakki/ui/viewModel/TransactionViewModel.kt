package org.mdw32.trakki.ui.viewModel

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
import java.text.SimpleDateFormat
import java.util.*

class TransactionViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // LiveData for all transactions
    private val _allTransactions = MutableLiveData<List<Transaction>>()
    val allTransactions: LiveData<List<Transaction>> get() = _allTransactions

    // LiveData for the last 5 transactions (for Home screen)
    private val _latestTransactions = MutableLiveData<List<Transaction>>()
    val latestTransactions: LiveData<List<Transaction>> get() = _latestTransactions

    // LiveData to track syncing errors
    private val _syncError = MutableLiveData<String?>()
    val syncError: LiveData<String?> get() = _syncError

    // LiveData for last sync time
    private val _lastSyncTime = MutableLiveData<String>()
    val lastSyncTime: LiveData<String> get() = _lastSyncTime

    /**
     * Fetch all transactions for the specific user.
     * Retrieves all transactions and updates the latest 5 transactions for the Home screen.
     */
    fun fetchUserTransactions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            _syncError.postValue("User is not authenticated.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch all transactions for the user
                val transactions = firestore.collection("transactions")
                    .whereEqualTo("userId", userId) // Filter by user ID
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents.map { it.toObject(Transaction::class.java)!! }

                // Update LiveData for all transactions
                _allTransactions.postValue(transactions)

                // Update LiveData for the last 5 transactions
                _latestTransactions.postValue(transactions.take(5))

                // Update the last sync time
                updateLastSyncTime()
            } catch (exception: Exception) {
                _syncError.postValue("Failed to fetch transactions: ${exception.message}")
            }
        }
    }

    /**
     * Add a new transaction for the user and refresh the data.
     */
    fun addTransaction(transaction: Transaction) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            _syncError.postValue("User is not authenticated.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Add the transaction with the userId
                firestore.collection("transactions")
                    .add(transaction.apply { this.userId = userId }) // Assign the userId to the transaction
                    .await()

                // Refresh the transactions after adding a new one
                fetchUserTransactions()
            } catch (exception: Exception) {
                _syncError.postValue("Error adding transaction: ${exception.message}")
            }
        }
    }

    /**
     * Update the last sync time to a human-readable format.
     */
    private fun updateLastSyncTime() {
        val currentTime = System.currentTimeMillis()
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        _lastSyncTime.postValue("Last sync: ${formatter.format(Date(currentTime))}")
    }
}
