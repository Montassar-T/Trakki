package org.mdw32.trakki

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.time.LocalDate

class TimeFormatActivity: BaseSettings() {


    private lateinit var back : ImageView;
    private lateinit var one : TextView;
    private lateinit var two : TextView;
    private lateinit var three : TextView;


    @SuppressLint("SetTextI18n", "DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_format)


        back = findViewById(R.id.back)

        val radioButton1 = findViewById<RadioButton>(R.id.first)
        val radioButton2 = findViewById<RadioButton>(R.id.second)
        val radioButton3 = findViewById<RadioButton>(R.id.third)

        one = findViewById<RadioButton>(R.id.one)
        two = findViewById<RadioButton>(R.id.two)
        three = findViewById<RadioButton>(R.id.three)

        val today = LocalDate.now()
        val day = today.dayOfMonth
        val month = today.monthValue
        val year = today.year


        val formattedDay = String.format("%02d", day)
        val formattedMonth = String.format("%02d", month)
        val formattedYear = String.format("%04d", year)

// Set the text for each view based on the selected format
        one.text = "Exemple: $formattedMonth/$formattedDay/$formattedYear"  // MM/dd/yyyy
        two.text = "Exemple: $formattedDay/$formattedMonth/$formattedYear"  // dd/MM/yyyy
        three.text = "Exemple: $formattedYear/$formattedMonth/$formattedDay"  // yyyy/MM/dd

        // Get SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // Get the saved date format from SharedPreferences
        val selectedFormat = sharedPreferences.getString("timeFormat", "dd/MM/yyyy") // Default to "MM/dd/yyyy" if not found

        // Check the saved format and set the corresponding radio button
        when (selectedFormat) {
            "dd/MM/yyyy" -> radioButton2.isChecked = true
            "MM/dd/yyyy" -> radioButton1.isChecked = true
            "yyyy/MM/dd" -> radioButton3.isChecked = true
            else -> radioButton1.isChecked = true // Default to "MM/dd/yyyy"
        }

        // Set up the listener for radio button selections
        setSingleRadioButtonSelection("timeFormat", listOf(radioButton1, radioButton2, radioButton3))




        back.setOnClickListener{

            var intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}