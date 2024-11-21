package org.mdw32.trakki

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp2Activity : ComponentActivity() {



    private lateinit var passwordLayout: TextInputLayout
    private lateinit var confirmPasswordLayout: TextInputLayout
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var passwordLabel: TextView
    private lateinit var login: TextView
    private lateinit var confirmPasswordLabel: TextView
    private lateinit var signup: Button
    private lateinit var cancel: Button
    private lateinit var customGoogleButton: LinearLayout
    private lateinit var bottom: LinearLayout
    private lateinit var loadingSpinner: ProgressBar


    private lateinit var email: String
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)


        email = intent.getStringExtra("EMAIL") ?: ""
        username = intent.getStringExtra("FULL_NAME") ?: ""


        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm)
        passwordLayout = findViewById(R.id.passwordLayout)
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout)
        passwordLabel = findViewById(R.id.passwordLabel)
        confirmPasswordLabel = findViewById(R.id.confirmPasswordLabel)
        signup = findViewById(R.id.signup)
        login = findViewById(R.id.login)
        cancel = findViewById(R.id.cancel)
        customGoogleButton = findViewById(R.id.customGoogleButton)

        loadingSpinner = findViewById(R.id.loadingSpinner)
        bottom = findViewById(R.id.bottom)

        signup.setOnClickListener {
            // Reset styles first
            resetStyles()

            // Validate input
            val passwordText = password.text.toString().trim()
            val confirmPasswordText = confirmPassword.text.toString().trim()

            var hasError = false

            // Password validation
            if (passwordText.isEmpty() || passwordText.length < 8) {
                applyErrorStyle(password, passwordLabel, passwordLayout)
                passwordLayout.error = if (passwordText.isEmpty()) {
                    "Required Field."
                } else {
                    "Minimum length is 8 characters."
                }
                hasError = true
            }

            // Confirm password validation
            if (confirmPasswordText.isEmpty() || confirmPasswordText != passwordText) {
                applyErrorStyle(confirmPassword, confirmPasswordLabel, confirmPasswordLayout)
                confirmPasswordLayout.error = if (confirmPasswordText.isEmpty()) {
                    "Required Field."
                } else {
                    "Passwords do NOT match."
                }
                hasError = true
            }

            if (hasError) {
                return@setOnClickListener
            }
            showLoading(true)
            // Firebase Authentication for creating user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "auth successful", Toast.LENGTH_SHORT).show()

                        // Registration successful, now save the username to Firestore
                        val user = FirebaseAuth.getInstance().currentUser
                        val db = FirebaseFirestore.getInstance()

                        if (user != null) {
                            val userData = hashMapOf(
                                "username" to username,
                                "email" to user.email
                            )

                            // Create a new document with the user's UID as the document ID
                            db.collection("users").document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Successfully saved username to Firestore
                                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                    showLoading(false)
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish() // Close the sign-up activity
                                }
                                .addOnFailureListener { e ->
                                    // Error saving user data

                                    showLoading(false)

                                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Registration failed
                        showLoading(false)

                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }


        }

        cancel.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(password,passwordLabel,passwordLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }
        })
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(confirmPassword,confirmPasswordLabel,confirmPasswordLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }
        })





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
        password.setBackgroundResource(R.drawable.edittext_border) // Normal border
        passwordLabel.setTextColor(Color.parseColor("#333333")) // Default label color
        confirmPassword.setBackgroundResource(R.drawable.edittext_border) // Normal border
        passwordLayout.error = null
        confirmPasswordLayout.error = null
        confirmPasswordLabel.setTextColor(Color.parseColor("#333333")) // Default label color
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            // Show the loading spinner and disable the button
            loadingSpinner.visibility = View.VISIBLE
            bottom.visibility = View.GONE


        } else {
            // Hide the loading spinner and re-enable the button
            loadingSpinner.visibility = View.GONE
            bottom.visibility = View.VISIBLE

        }
    }


}