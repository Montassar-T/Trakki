package org.mdw32.trakki

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton

class LanguageActivity: BaseSettings() {


    private lateinit var back : ImageView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)


        back = findViewById(R.id.back)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        val radioButton1 = findViewById<RadioButton>(R.id.first)
        val radioButton2 = findViewById<RadioButton>(R.id.second)
        val radioButton3 = findViewById<RadioButton>(R.id.third)



        val selectedLanguage = sharedPreferences.getString("language", "English") // Default to "USD" if not found


        // Check the saved format and set the corresponding radio button
        when (selectedLanguage) {
            "French" -> radioButton2.isChecked = true
            "English" -> radioButton1.isChecked = true
            "العربية" -> radioButton3.isChecked = true
            else -> radioButton1.isChecked = true // Default to "MM/dd/yyyy"
        }
        setSingleRadioButtonSelection("language", listOf(radioButton1, radioButton2, radioButton3))




        back.setOnClickListener{



            var intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}