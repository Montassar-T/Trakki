package org.mdw32.trakki

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputLayout

class SignUpActivity : ComponentActivity() {

    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var nextButton: Button
    private lateinit var fullNameLabel: TextView
    private lateinit var emailLabel: TextView
    private lateinit var login: TextView

    private lateinit var emailLayout: TextInputLayout
    private lateinit var fullNameLayout: TextInputLayout



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        fullName = findViewById(R.id.name)
        email = findViewById(R.id.email)
        nextButton = findViewById(R.id.next)
        fullNameLabel = findViewById(R.id.fullNameLabel)
        emailLabel = findViewById(R.id.EmailLabel)
        emailLayout = findViewById(R.id.emailLayout)
        fullNameLayout = findViewById(R.id.nameLayout)
        login = findViewById(R.id.login)

        nextButton.setOnClickListener {
            // Reset styles first
            resetStyles()

            // Validate input
            val fullNameText = fullName.text.toString().trim()
            val emailText = email.text.toString().trim()

            var hasError = false
            if (fullNameText.isEmpty() || fullNameText.length < 3) {
                applyErrorStyle(fullName,fullNameLabel,fullNameLayout)
                fullNameLayout.error = if (fullNameText.isEmpty()) {
                    "Required Field."
                } else {
                    "Provide a valid name."
                }
                hasError = true
            }

            // Validate Email
            if (emailText.isEmpty() || !isValidEmail(emailText)) {
                applyErrorStyle(email,emailLabel,emailLayout)
                emailLayout.error = if (emailText.isEmpty()) {
                    "Required Field."
                } else {
                    "Provide a valid email."
                }
                hasError = true
            }

            if (hasError) {
                return@setOnClickListener
            }




            // Move to the next screen
            val intent = Intent(this, SignUp2Activity::class.java)
            intent.putExtra("FULL_NAME", fullName.text.toString())
            intent.putExtra("EMAIL", emailText)
            startActivity(intent)
        }
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(email,emailLabel,emailLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty() && isValidEmail(s.toString())) {
                    emailLayout.error = null
                    emailLayout.isErrorEnabled = false
                }
            }
        })
        fullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(fullName,fullNameLabel,fullNameLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    // Additional actions, like clearing an error
                    emailLayout.error = null
                }
            }
        })


        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }




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
        fullName.setBackgroundResource(R.drawable.edittext_border) // Normal border
        fullNameLabel.setTextColor(Color.parseColor("#333333")) // Default label color
        email.setBackgroundResource(R.drawable.edittext_border) // Normal border
        emailLayout.error = null
        fullNameLayout.error = null
        emailLabel.setTextColor(Color.parseColor("#333333")) // Default label color
    }



    // Email Validation Method using Regex
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}


