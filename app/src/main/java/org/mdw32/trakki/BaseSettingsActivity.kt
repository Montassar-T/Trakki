package org.mdw32.trakki

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

abstract class BaseSettings: ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back press action: navigate to SettingsActivity and finish the current activity
                val intent = Intent(this@BaseSettings, SettingsActivity::class.java)
                startActivity(intent)
                finish()  // Finish the current activity to go to the next one
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }




    protected fun setSingleRadioButtonSelection( settingKey: String , radioButtons: List<RadioButton>) {
        for (radioButton in radioButtons) {
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Uncheck all other radio buttons in the group
                    for (otherButton in radioButtons) {
                        if (otherButton != radioButton) {
                            otherButton.isChecked = false
                        }
                    }

                    // Handle the logic for each category: Language, Currency, Date Format
                    val sharedPreferences: SharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    when (settingKey) {
                        "language" -> {
                            // Handle language selection
                            val selectedLanguage = when (radioButton.id) {
                                R.id.first -> "English" // Example: R.id.first for English
                                R.id.second -> "French"
                                R.id.third -> "العربية"
                                else -> "English" // Default
                            }
                            editor.putString("language", selectedLanguage)
                        }

                        "currency" -> {
                            // Handle currency selection
                            val selectedCurrency = when (radioButton.id) {
                                R.id.first -> "TND" // Example: R.id.first for DNT
                                R.id.second -> "USD"
                                R.id.third -> "EUR"
                                else -> "DNT" // Default
                            }
                            editor.putString("currency", selectedCurrency)
                        }

                        "timeFormat" -> {
                            // Handle date format selection
                            val selectedFormat = when (radioButton.id) {
                                R.id.first -> "MM/dd/yyyy" // Example: R.id.first for MM/dd/yyyy
                                R.id.second -> "dd/MM/yyyy"
                                R.id.third -> "yyyy/MM/dd"
                                else -> "dd/MM/yyyy" // Default
                            }
                            editor.putString("timeFormat", selectedFormat)
                        }

                        else -> {
                            // Default case if no matching setting key is found
                        }
                    }

                    // Apply the changes
                    editor.apply()
                }
            }
        }
    }


}