package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.mdw32.trakki.models.Transaction
import org.mdw32.trakki.ui.adapter.TransactionAdapter
import java.util.Locale

class IncomeFragment : Fragment() {


    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    private lateinit var back : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_income, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        back = view.findViewById(R.id.back)



        back.setOnClickListener {


            (activity as BaseActivity).replaceFragment(TransactionsFragment())

            // Update the bottom navigation to select the HomeFragment
            (activity as BaseActivity).bottomNavigationView.selectedItemId = R.id.nav_transactions
        }


    }


}