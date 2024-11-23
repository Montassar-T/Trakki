package org.mdw32.trakki

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.mdw32.trakki.models.Transaction
import org.mdw32.trakki.ui.adapter.TransactionAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var bottom : LinearLayout;
    private lateinit var wallet : LinearLayout;
    private lateinit var empty : LinearLayout;
    private lateinit var date : TextView;
    private lateinit var settings : ImageView;
    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private var transactionList: MutableList<Transaction> = mutableListOf()



    // Firebase Firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    // Get the current user
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottom = view.findViewById(R.id.bottom)
        wallet = view.findViewById(R.id.wallet)
        date = view.findViewById(R.id.date)
        settings = view.findViewById(R.id.settings)
        empty = view.findViewById(R.id.empty)


        transactionRecyclerView = view.findViewById(R.id.rvTransactions)
        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchTransactions()

//        if(transactionList.size >0){
//            empty.visibility = View.GONE
//            transactionRecyclerView.visibility = View.VISIBLE
//        }else{
//            empty.visibility = View.VISIBLE
//            transactionRecyclerView.visibility = View.GONE
//
//        }


        // Set the adapter
        transactionAdapter = TransactionAdapter(transactionList)
        transactionRecyclerView.adapter = transactionAdapter

        // Get the current date
        val currentDate = LocalDate.now()

        // Define the desired format (e.g., "EEE, dd MMM")
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH)

        // Format the current date and assign it to the TextView
        date.text = currentDate.format(formatter)

        wallet.post {
            val walletHeight = wallet.height // Get the height of the wallet layout
            val translationY = -walletHeight / 2f // Calculate half the height
            bottom.translationY = translationY // Apply translation
        }


        settings.setOnClickListener{
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchTransactions() {
        user?.let { currentUser ->
            val userTransactionsRef = firestore.collection("transactions")
                .whereEqualTo("userId", currentUser.uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(5) // Retrieve the latest 5 transactions

            userTransactionsRef.get()
                .addOnSuccessListener { documents ->
                    transactionList.clear()
                    for (document in documents) {
                        val transaction = document.toObject(Transaction::class.java)
                        transactionList.add(transaction)
                    }
                    // Set the adapter
                    transactionAdapter = TransactionAdapter(transactionList)
                    transactionRecyclerView.adapter = transactionAdapter
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching transactions: $exception", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
