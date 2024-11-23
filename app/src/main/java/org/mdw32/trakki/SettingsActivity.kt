package org.mdw32.trakki

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import java.time.LocalDate

class SettingsActivity: ComponentActivity(){

    private lateinit var bottom : LinearLayout;
    private lateinit var back : ImageView;
    private lateinit var formatArrow : LinearLayout;
    private lateinit var languageArrow : LinearLayout;
    private lateinit var CurrArrow : LinearLayout;
    private lateinit var language : TextView;
    private lateinit var currency : TextView;
    private lateinit var format : TextView;

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        bottom = findViewById(R.id.bottom)
        back = findViewById(R.id.back)
        formatArrow = findViewById(R.id.format_arrow)
        languageArrow = findViewById(R.id.language_arrow)
        CurrArrow = findViewById(R.id.curr_arrow)
        language = findViewById(R.id.language_text)
        currency = findViewById(R.id.curr_text)
        format = findViewById(R.id.format_text)

        val today = LocalDate.now()
        val day = today.dayOfMonth
        val month = today.monthValue
        val year = today.year

        // Retrieve the stored currency from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)


        val selectedCurrency = sharedPreferences.getString("currency", "USD") // Default to "USD" if not found
        val selectedLanguage = sharedPreferences.getString("language", "English") // Default to "USD" if not found
        val selectedFormat = sharedPreferences.getString("timeFormat", "dd/MM/yyyy") // Default to "USD" if not found

        // Update the TextView with the selected currency
        currency.text = selectedCurrency
        language.text = selectedLanguage

        if (selectedFormat == "dd/MM/yyyy") {
            format.text = String.format("%02d/%02d/%04d", day, month, year)
        } else if (selectedFormat == "MM/dd/yyyy") {
            format.text = String.format("%02d/%02d/%04d", month, day, year)
        } else { // Assuming the default is "yyyy/MM/dd"
            format.text = String.format("%04d/%02d/%02d", year, month, day)
        }


        bottom.post {
            val height = bottom.height // Get the height of the wallet layout
            val translationY = -1.3F * height  // Calculate half the height
            bottom.translationY = translationY // Apply translation
        }

        back.setOnClickListener{
            var intent = Intent (this, BaseActivity::class.java)
            startActivity(intent)
            finish()
        }
        formatArrow.setOnClickListener{
            var intent = Intent(this, TimeFormatActivity::class.java )
            startActivity(intent)
        }
        languageArrow.setOnClickListener{
            var intent = Intent(this, LanguageActivity::class.java )
            startActivity(intent)
        }
        CurrArrow.setOnClickListener{
            var intent = Intent(this, CurrancyActivity::class.java )
            startActivity(intent)
        }



    }

}