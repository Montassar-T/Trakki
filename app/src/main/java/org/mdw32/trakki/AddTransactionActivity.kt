package org.mdw32.trakki

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.mdw32.trakki.ui.adapter.CustomAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionActivity: ComponentActivity() {
    private lateinit var bottom : LinearLayout;
    private lateinit var wrappper : LinearLayout;
    private lateinit var cat_wrapper : LinearLayout;
    private lateinit var add : Button;
    private lateinit var loadingSpinner : ProgressBar;
    private val db = FirebaseFirestore.getInstance()

    private lateinit var pattern: String
    private lateinit var name: EditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var nameLabel: TextView
    private lateinit var amount: EditText
    private lateinit var curr: EditText
    private lateinit var seperator: View
    private lateinit var amountLayout: TextInputLayout
    private lateinit var amountLabel: TextView
    private lateinit var amountErrorMessage: TextView
    private lateinit var back: ImageView
    private var walletAmount: Double? = null
    private val homeViewModel: HomeViewModel by viewModels() // ViewModel scoped to this activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        bottom = findViewById(R.id.bottom)
        back = findViewById(R.id.back)

        name = findViewById(R.id.name)
        nameLayout = findViewById(R.id.nameLayout)
        nameLabel = findViewById(R.id.nameLabel)
        amount = findViewById(R.id.amount)
        amountLayout = findViewById(R.id.amountLayout)
        amountLabel = findViewById(R.id.amountLabel)
        amountErrorMessage = findViewById(R.id.amountErrorMessage)
        curr = findViewById(R.id.curr)
        seperator = findViewById(R.id.seperator)
        wrappper = findViewById(R.id.wrappper)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        cat_wrapper = findViewById(R.id.cat_wrapper)

        add = findViewById(R.id.add)
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        homeViewModel.fetchHomeData()
        homeViewModel.walletAmount.observe(this) { walletAmountValue ->
            walletAmount = walletAmountValue


        }


       val  currency = sharedPreferences.getString("currency", "USD")
        curr.setText(currency)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val dateLayout = findViewById<TextInputLayout>(R.id.dateLayout)
        val dateEditText = findViewById<EditText>(R.id.date)

        // Data for the dropdown
        val titles = listOf("Income", "Expense")
        val catIcons = listOf(R.drawable.money_send, R.drawable.cake , R.drawable.bus , R.drawable.hospital , R.drawable.music , R.drawable.shopping_cart , R.drawable.box)
        val catTitles = listOf("Bills & utilites", "Food","Transport","Health", "Entertainment","Shopping","Other")
        val icons = listOf(R.drawable.money_recive, R.drawable.money_send)

        // Create the custom adapter
        val adapter = CustomAdapter(this, titles, icons)
        val catAdapter = CustomAdapter(this, catTitles, catIcons)

        // Get the AutoCompleteTextView
        val dropdown: MaterialAutoCompleteTextView = findViewById(R.id.dropdown)
        val catDropdown: MaterialAutoCompleteTextView = findViewById(R.id.catDropdown)

        // Set the custom adapter to the dropdown
        dropdown.setAdapter(adapter)
        catDropdown.setAdapter(catAdapter)



        // Set default selected value
        dropdown.setText(titles[0], false)
        catDropdown.setText(catTitles[0], false)




        if(dropdown.text.toString() == "Income"){
            cat_wrapper.visibility = View.GONE
        }

        // Set an OnItemClickListener to update visibility when the dropdown selection changes
        dropdown.setOnItemClickListener { parent, view, position, id ->
            if (dropdown.text.toString() == "Income") {
                cat_wrapper.visibility = View.GONE  // Hide the category wrapper if "Income" is selected
            } else {
                cat_wrapper.visibility = View.VISIBLE  // Show the category wrapper for other types
            }
        }




            back.setOnClickListener {
               finish()
            }


        add.setOnClickListener {
            resetStyles()

            val typeText = dropdown.text.toString().trim()
            val categoryText = catDropdown.text.toString().trim()
            val dateText = dateEditText.text.toString().trim() // Get the date input
            val nameText = name.text.toString().trim()
            val amountText = amount.text.toString().trim()
            var hasError = false

            println(typeText)
            println(categoryText)
            println(dateText)
            println(amountText)
            println(nameText)
            println(userId)

            if (nameText.isEmpty() ) {
                applyErrorStyle(name, nameLabel, nameLayout)
                nameLayout.error = "Required Field."
                hasError = true
            }

            if (amountText.isEmpty()) {
                amount.setHintTextColor(Color.RED)

                amount.setTextColor(Color.RED)
                amountLabel.setTextColor(Color.RED)
                amountLayout.error = "salem khouya"

                amountLayout.errorIconDrawable?.setTint(Color.RED)
                amountErrorMessage.setTextColor(Color.RED)
                curr.setTextColor(Color.RED)
                amountErrorMessage.visibility = View.VISIBLE
                wrappper.setBackgroundResource(R.drawable.edittext_red_border)
                seperator.setBackgroundColor(Color.parseColor("#C80000"))




                hasError = true
            }

            if (hasError) {
                return@setOnClickListener
            }

            loadingSpinner.visibility = View.VISIBLE
            add.visibility = View.GONE

            // Show Toast if it's an expense and wallet amount is 0
            if (typeText == "Expense" && walletAmount == 0.0) {
                loadingSpinner.visibility = View.GONE
                add.visibility = View.VISIBLE

                Toast.makeText(this, "Cannot add expense: Wallet amount is 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != null) {
                saveTransaction(nameText, amountText, typeText, categoryText, dateText, userId)
            }


        }

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(name,nameLabel,nameLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    nameLayout.error = null
                    nameLayout.isErrorEnabled = false
                }
            }
        })

        amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                amount.setHintTextColor(Color.parseColor("#666666"))
                amount.setTextColor(Color.parseColor("#666666"))


                amountLabel.setTextColor(Color.parseColor("#333333"))
                amountLayout.error = null



                curr.setTextColor(Color.parseColor("#333333"))
                amountErrorMessage.visibility = View.GONE
                wrappper.setBackgroundResource(R.drawable.edittext_border)
                seperator.setBackgroundColor(Color.parseColor("#aaaaaa"))

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    amountLayout.error = null
                    amountLayout.isErrorEnabled = false
                }
            }
        })



        // Initialize today's date
        val calendar = Calendar.getInstance()
         pattern = sharedPreferences.getString("timeFormat", "dd/MM/yyyy").toString()
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val todayDate = dateFormat.format(calendar.time)
        dateEditText.setText(todayDate)

        // Handle the end icon click (calendar icon)
        dateLayout.setEndIconOnClickListener {
            showDatePicker(dateEditText)
        }

        // Optional: Prevent manual typing by disabling EditText focus
        dateEditText.setOnClickListener {
            showDatePicker(dateEditText)
        }
        dateEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePicker(dateEditText)
            }
        }


    }

    // Function to save transaction data to Firestore
    private fun saveTransaction(name: String, amount: String, type: String, category: String, dateInput: String, userId: String) {
        // Parse the date string into a Date object using SimpleDateFormat
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault()) // Use the correct date format based on your app's preferences
        val parsedDate = dateFormat.parse(dateInput)
        val currency : String = curr.text.toString()
        // Prepare the transaction map with a Date object for "date"
        val transaction = hashMapOf(
            "name" to name,
            "amount" to amount.toDoubleOrNull(), // Convert amount to a Double if necessary
            "type" to type,

            "date" to parsedDate, // Use the parsed Date object here
            "userId" to userId,
            "currency" to currency,

        )

        if (type != "Income") {
            transaction["category"] = category
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("transactions")
            .add(transaction)
            .addOnSuccessListener {
                loadingSpinner.visibility = View.GONE
                add.visibility = View.VISIBLE
                this.name.text = null
                this.amount.text = null

                Toast.makeText(this, "Transaction added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Transaction", "Error adding document", e)
                loadingSpinner.visibility = View.GONE
                add.visibility = View.VISIBLE
            }
    }


    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()


        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Set the selected date into the EditText
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                editText.setText(pattern.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }


    private fun applyErrorStyle(editText: EditText, label: TextView, inputLayout: TextInputLayout) {
        editText.setBackgroundResource(R.drawable.edittext_red_border)
        editText.setHintTextColor(Color.RED)
        editText.setTextColor(Color.RED)
        label.setTextColor(Color.RED)
        inputLayout.errorIconDrawable?.setTint(Color.RED)
        inputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
    }

    private fun resetLayout(editText: EditText, label: TextView, inputLayout:TextInputLayout){
        label.setTextColor(Color.parseColor("#333333")) // Default label color
        editText.setTextColor(Color.parseColor("#333333"))
        editText.setHintTextColor(Color.parseColor("#333333"))
        editText.setBackgroundResource(R.drawable.edittext_border) // Normal border
        inputLayout.error = null
        inputLayout.isErrorEnabled = false

    }


    private fun resetStyles() {
        name.setBackgroundResource(R.drawable.edittext_border) // Normal border
        nameLayout.error = null
        nameLabel.setTextColor(Color.parseColor("#333333")) // D

        amountLayout.error = null
        amountLabel.setTextColor(Color.parseColor("#333333")) // D

    }


}