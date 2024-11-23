package org.mdw32.trakki

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton

class CurrancyActivity: BaseSettings() {
    private lateinit var back: ImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)



        back = findViewById(R.id.back)
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()





        val radioButton1 = findViewById<RadioButton>(R.id.first)
        val radioButton2 = findViewById<RadioButton>(R.id.second)
        val radioButton3 = findViewById<RadioButton>(R.id.third)


        var selectedCurrency = sharedPreferences.getString("currency", "DNT") // Default to "USD" if not found

        if(selectedCurrency == "USD"){
            radioButton2.isChecked = true
        }else if(selectedCurrency == "DNT"){
            radioButton1.isChecked = true

        }else{
            radioButton3.isChecked = true

        }

        setSingleRadioButtonSelection(radioButton1, radioButton2, radioButton3)


        back.setOnClickListener {

             selectedCurrency = when {
                radioButton1.isChecked -> "DNT"    // You can change the currency values accordingly
                radioButton2.isChecked -> "USD"
                radioButton3.isChecked -> "EUR"
                else -> "DNT" // Default value in case none is selected
            }

            // Save the selected currency to SharedPreferences
            editor.putString("currency", selectedCurrency)
            editor.apply()
            var intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}