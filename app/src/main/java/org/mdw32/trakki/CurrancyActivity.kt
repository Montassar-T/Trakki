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





        val radioButton1 = findViewById<RadioButton>(R.id.first)
        val radioButton2 = findViewById<RadioButton>(R.id.second)
        val radioButton3 = findViewById<RadioButton>(R.id.third)


        var selectedCurrency = sharedPreferences.getString("currency", "TND") // Default to "USD" if not found



        when (selectedCurrency) {
            "USD" -> radioButton2.isChecked = true
            "TND" -> radioButton1.isChecked = true
            "EUR" -> radioButton3.isChecked = true
            else -> radioButton1.isChecked = true // Default to "MM/dd/yyyy"
        }

        setSingleRadioButtonSelection("currency",listOf(radioButton1, radioButton2, radioButton3))


        back.setOnClickListener {

            var intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}