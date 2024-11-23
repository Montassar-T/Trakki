package org.mdw32.trakki

import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import androidx.activity.ComponentActivity

abstract class BaseSettings: ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    protected fun setSingleRadioButtonSelection(vararg radioButtons: RadioButton) {
        // Loop through all the radio buttons
        for (radioButton in radioButtons) {
            // Set a listener for each radio button
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                // If the current radio button is checked, uncheck the others
                if (isChecked) {
                    for (otherButton in radioButtons) {
                        if (otherButton != radioButton) {
                            otherButton.isChecked = false
                        }
                    }
                }
            }
        }
    }
}