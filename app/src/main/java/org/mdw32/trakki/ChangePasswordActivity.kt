package org.mdw32.trakki

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity: ComponentActivity() {
    private lateinit var back : ImageView;
    private lateinit var save : Button;




    private lateinit var oldPass: EditText
    private lateinit var oldPassLayout: TextInputLayout
    private lateinit var oldPassLabel: TextView


    private lateinit var newPass: EditText
    private lateinit var newPassLayout: TextInputLayout
    private lateinit var newPassLabel: TextView

    private lateinit var pass: EditText
    private lateinit var passLayout: TextInputLayout
    private lateinit var passLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        back = findViewById(R.id.back)
        save = findViewById(R.id.save)
        oldPass = findViewById(R.id.oldpassword)
        oldPassLayout = findViewById(R.id.oldpasswordLayout)
        oldPassLabel = findViewById(R.id.oldpasswordLabel)
        newPass = findViewById(R.id.newpassword)
        newPassLayout = findViewById(R.id.newpasswordLayout)
        newPassLabel = findViewById(R.id.newpasswordLabel)
        pass = findViewById(R.id.password)
        passLayout = findViewById(R.id.passwordLayout)
        passLabel = findViewById(R.id.passwordLabel)







        back.setOnClickListener{

            finish()
        }


        oldPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing
                resetLayout(oldPass,oldPassLabel,oldPassLayout)
            }
            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    oldPassLayout.error = null
                    oldPassLayout.isErrorEnabled = false
                }
            }
        })
        pass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing
                resetLayout(pass,passLabel,passLayout)
            }
            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    passLayout.error = null
                    passLayout.isErrorEnabled = false
                }
            }
        })
        newPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing
                resetLayout(newPass,newPassLabel,newPassLayout)
            }
            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    newPassLayout.error = null
                    newPassLayout.isErrorEnabled = false
                }
            }
        })


        save.setOnClickListener{
            resetStyles()

            val passwordText = pass.text.toString().trim()
            val oldpass = oldPass.text.toString().trim()
            val newpassText = newPass.text.toString().trim()

            var hasError = false

            // Validate old password
            if (oldpass.isEmpty() || oldpass.length < 8) {
                applyErrorStyle(oldPass, oldPassLabel, oldPassLayout)
                oldPassLayout.error = if (oldpass.isEmpty()) {
                    "Required Field."
                } else {
                    "Wrong Password"
                }
                hasError = true
            }

            // Validate new password
            if (newpassText.isEmpty() || newpassText.length < 8) {
                applyErrorStyle(newPass, newPassLabel, newPassLayout)
                newPassLayout.error = if (newpassText.isEmpty()) {
                    "Required Field."
                } else {
                    "Minimum length is 8 characters."
                }
                hasError = true
            }

            // Validate password confirmation
            if (passwordText.isEmpty() || passwordText.length < 8) {
                applyErrorStyle(pass, passLabel, passLayout)
                passLayout.error = if (passwordText.isEmpty()) {
                    "Required Field."
                } else {
                    "Minimum length is 8 characters."
                }
                hasError = true
            }

            // Check if passwords match
            if (passwordText != newpassText) {
                applyErrorStyle(pass, passLabel, passLayout)
                passLayout.error = "Please Confirm Password."
                hasError = true
            }
            if (hasError) {
                return@setOnClickListener
            }

            // Proceed if no errors
            if (!hasError) {
                val user = FirebaseAuth.getInstance().currentUser

                if (user != null) {
                    // Create the credential with the email and entered old password
                    val credential = EmailAuthProvider.getCredential(user.email ?: "", oldpass)

                    // Reauthenticate the user with the old password
                    user.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Old password is correct, proceed to update the password
                                user.updatePassword(passwordText)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            // Password updated successfully
                                            pass.text = null
                                            oldPass.text = null
                                            newPass.text = null

                                            Toast.makeText(applicationContext, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Handle failure to update password
                                            Toast.makeText(applicationContext, "Failed to update password. Please try again.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                // The old password is incorrect
                                applyErrorStyle(oldPass, oldPassLabel, oldPassLayout)
                                oldPassLayout.error = "Incorrect current password"
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle reauthentication failure (network issues or other problems)
                            applyErrorStyle(oldPass, oldPassLabel, oldPassLayout)
                            oldPassLayout.error = "Incorrect current password"
                        }
                } else {
                    // Handle the case where the user is not logged in
                    Toast.makeText(applicationContext, "No user logged in", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Override the back button press behavior
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Finish the current activity when back button is pressed
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)


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
        pass.setBackgroundResource(R.drawable.edittext_border) // Normal border
        passLayout.error = null
        passLabel.setTextColor(Color.parseColor("#333333")) // D


        oldPass.setBackgroundResource(R.drawable.edittext_border) // Normal border
        oldPassLayout.error = null
        oldPassLabel.setTextColor(Color.parseColor("#333333")) // Default label color

        newPass.setBackgroundResource(R.drawable.edittext_border) // Normal border
        newPassLayout.error = null
        newPassLabel.setTextColor(Color.parseColor("#333333")) // Default label color
    }











}