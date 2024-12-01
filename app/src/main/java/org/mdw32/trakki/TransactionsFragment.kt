package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TransactionsFragment : Fragment() {
    private lateinit var bottom : LinearLayout;
    private lateinit var wrapper : LinearLayout;
    private lateinit var settings : ImageView;
    private lateinit var arrow_income : ImageView;
    private lateinit var arrow_expense : ImageView;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottom = view.findViewById(R.id.bottom)
        wrapper = view.findViewById(R.id.income)
        settings = view.findViewById(R.id.settings)
        arrow_expense = view.findViewById(R.id.arrow_expense)
        arrow_income = view.findViewById(R.id.arrow_income)


        // Get the current date

        wrapper.post {
            val height = wrapper.height // Get the height of the wallet layout
            val translationY = -height / 2f // Calculate half the height
            bottom.translationY = translationY // Apply translation
        }


        settings.setOnClickListener{
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }

        arrow_expense.setOnClickListener {
            // Replace with FragmentExpense
            val fragmentExpense = ExpenseFragment()

            // Begin the transaction using parentFragmentManager
            val transaction = parentFragmentManager.beginTransaction()

            // Replace current fragment with FragmentExpense and add to back stack
            transaction.replace(R.id.fragment_container, fragmentExpense)
            transaction.addToBackStack(null)  // Add to back stack to allow back navigation

            // Commit the transaction
            transaction.commit()
        }

        arrow_income.setOnClickListener {
            // Replace with FragmentIncome
            val fragmentIncome = IncomeFragment()

            // Begin the transaction using parentFragmentManager
            val transaction = parentFragmentManager.beginTransaction()

            // Replace current fragment with FragmentIncome and add to back stack
            transaction.replace(R.id.fragment_container, fragmentIncome)
            transaction.addToBackStack(null)  // Add to back stack to allow back navigation

            // Commit the transaction
            transaction.commit()
        }

    }


}