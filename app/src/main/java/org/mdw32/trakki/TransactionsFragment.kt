package org.mdw32.trakki

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TransactionsFragment : Fragment() {
    private lateinit var bottom : LinearLayout;
    private lateinit var wrapper : LinearLayout;

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


        // Get the current date

        wrapper.post {
            val height = wrapper.height // Get the height of the wallet layout
            val translationY = -height / 2f // Calculate half the height
            bottom.translationY = translationY // Apply translation
        }
    }


}