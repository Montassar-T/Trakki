package org.mdw32.trakki

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class ExpenseFragment : Fragment() {

    private lateinit var back : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expense, container, false)
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