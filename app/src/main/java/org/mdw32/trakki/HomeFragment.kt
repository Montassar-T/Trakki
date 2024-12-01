package org.mdw32.trakki

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.mdw32.trakki.models.Transaction
import org.mdw32.trakki.ui.adapter.TransactionAdapter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var bottom: LinearLayout
    private lateinit var all: LinearLayout
    private lateinit var wallet: LinearLayout
    private lateinit var empty: LinearLayout
    private lateinit var date: TextView
    private lateinit var  selectedCurrency: String
    private lateinit var settings: ImageView
    private lateinit var walletS: ImageView
    private lateinit var refresh: ImageView
    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private var transactionList: MutableList<Transaction> = mutableListOf()

    private lateinit var totalSpendingTextView: TextView
    private lateinit var spending: TextView
    private lateinit var percent: TextView
    private var totalSpending: Double = 0.0
    private lateinit var sharedPreferences: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        bottom = view.findViewById(R.id.bottom)
        wallet = view.findViewById(R.id.wallet)
        date = view.findViewById(R.id.date)
        settings = view.findViewById(R.id.settings)
        empty = view.findViewById(R.id.empty)
        transactionRecyclerView = view.findViewById(R.id.rvTransactions)
        totalSpendingTextView = view.findViewById(R.id.totalSpendingTextView)
        spending = view.findViewById(R.id.spending)
        percent = view.findViewById(R.id.percent)
        all = view.findViewById(R.id.all)
        walletS = view.findViewById(R.id.walletS)
//        refresh = view.findViewById(R.id.refresh)

        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get SharedPreferences and currency setting
        sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
         selectedCurrency =
             sharedPreferences.getString("currency", "TND").toString() // Default to "TND" if not found

        // Observe transactions from ViewModel
        observeTransactions()

        // Fetch transactions when the fragment is created
        homeViewModel.fetchHomeData()

      val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.ENGLISH)
        date.text = currentDate.format(formatter)

        wallet.post {
            val walletHeight = wallet.height
            val translationY = -walletHeight / 2f
            bottom.translationY = translationY
        }


        walletS.setOnClickListener {
            // Call the replaceFragment method in BaseActivity to replace HomeFragment with IncomeFragment
            (activity as BaseActivity).replaceFragment(TransactionsFragment())

            // Optionally, update the bottom navigation item to selected
            (activity as BaseActivity).bottomNavigationView.selectedItemId = R.id.nav_transactions
        }

        settings.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }
        all.setOnClickListener {
            // Replace HomeFragment with TransactionFragment
            val transactionFragment = TransactionsFragment()

            // Start fragment transaction
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, transactionFragment)  // Replace with the ID of the container
            fragmentTransaction.addToBackStack(null)  // Optional: adds the transaction to the back stack, so user can press back to return
            fragmentTransaction.commit()
            (activity as BaseActivity).bottomNavigationView.selectedItemId = R.id.nav_transactions

        }
//        refresh.setOnClickListener {
//            observeTransactions()
//        }

    }

    @SuppressLint("SetTextI18n")
    private fun observeTransactions() {
        homeViewModel.latestTransactions.observe(viewLifecycleOwner, Observer { transactions ->
            transactionList.clear()
            transactionList.addAll(transactions)

            if (transactionList.isEmpty()) {
                empty.visibility = View.VISIBLE
                transactionRecyclerView.visibility = View.GONE
            } else {
                empty.visibility = View.GONE
                transactionRecyclerView.visibility = View.VISIBLE
                transactionAdapter = TransactionAdapter(transactionList)
                transactionRecyclerView.adapter = transactionAdapter


            }
        })
        // Calculate the total spending value using the viewModel's LiveData
        homeViewModel.totalSpendingThisMonth.observe(viewLifecycleOwner, Observer { totalSpending ->
            // Display the total spending value in the correct currency
            Toast.makeText(requireContext(), "Total Spending: $totalSpending", Toast.LENGTH_SHORT).show()
            println(convertCurrency(totalSpending, selectedCurrency))

            // Convert total spending to selected currency (USD or other)
            totalSpendingTextView.text = convertCurrency(totalSpending, selectedCurrency ) +" "+ selectedCurrency
        })

        // Observe the spending wallet value
        homeViewModel.walletAmount.observe(viewLifecycleOwner, Observer { walletAmount ->
            // Convert and display the wallet amount in the selected currency
println("zina " + walletAmount )
            spending.text = convertCurrency(walletAmount, selectedCurrency)+" " + selectedCurrency
            println("zina 222 " + convertCurrency(walletAmount, selectedCurrency) )

        })

        // Observe the percentage of the spending
        homeViewModel.percentageChange.observe(viewLifecycleOwner, Observer { percentage ->
            // Set the percentage to the corresponding TextView
            percent.text = "$percentage%"
        })
    }

    private fun convertCurrency(amount: Double, selectedCurrency: String): String {
        val decimalFormat = DecimalFormat("#.0") // Format with one decimal place
        decimalFormat.decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = '.' // Ensure proper decimal formatting
        }
        // Handle the case for 0 specifically
        if (amount == 0.0) {
            return "0"
        }
        var result = decimalFormat.format(amount) // Default to the formatted amount in the original currency


        homeViewModel.getExchangeRates { exchangeRates ->
            val conversionRate = when (selectedCurrency) {
                "TND" -> exchangeRates?.conversion_rates?.get("TND") ?: 1.0
                "EUR" -> exchangeRates?.conversion_rates?.get("EUR") ?: 1.0
                else -> 1.0
            }
            // Convert the amount to the selected currency
            val convertedAmount = amount * conversionRate
            val formattedAmount = decimalFormat.format(convertedAmount)
            // Remove any unnecessary ".0" if the number is an integer
            result = if (formattedAmount.endsWith(".0")) {
                formattedAmount.removeSuffix(".0")
            } else {
                formattedAmount
            }
        }

        return result
    }





}

