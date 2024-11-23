package org.mdw32.trakki

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton

class TimeFormatActivity: BaseSettings() {


    private lateinit var back : ImageView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_format)


        back = findViewById(R.id.back)

        val sharedPreferences: SharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val selectedFormat = sharedPreferences.getString("timeFormat", "dd/MM/yyyy")




        val radioButton1 = findViewById<RadioButton>(R.id.first)
        val radioButton2 = findViewById<RadioButton>(R.id.second)
        val radioButton3 = findViewById<RadioButton>(R.id.third)

        if(selectedFormat == "dd/MM/yyyy"){
            radioButton2.isChecked = true
        }else if(selectedFormat == "MM/dd/yyyy"){
            radioButton1.isChecked = true

        }else{
            radioButton3.isChecked = true

        }
        setSingleRadioButtonSelection(radioButton1, radioButton2, radioButton3)


        back.setOnClickListener{


            val selectedFormat = when {
                radioButton1.isChecked -> "MM/dd/yyyy"    // You can change the currency values accordingly
                radioButton2.isChecked -> "dd/MM/yyyy"
                radioButton3.isChecked -> "yyyy/MM/dd"
                else -> "dd/MM/yyyy" // Default value in case none is selected
            }

            // Save the selected currency to SharedPreferences
            editor.putString("timeFormat", selectedFormat)
            editor.apply()
            var intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}