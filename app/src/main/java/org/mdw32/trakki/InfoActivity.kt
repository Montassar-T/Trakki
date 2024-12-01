package org.mdw32.trakki

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InfoActivity: ComponentActivity() {
    private lateinit var back : ImageView;
    private lateinit var save : Button;
    private lateinit var switchCompat: SwitchCompat;

    private lateinit var name: EditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var nameLabel: TextView

    private lateinit var email: EditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailLabel: TextView

    private lateinit var phone: EditText
    private lateinit var phoneLayout: TextInputLayout
    private lateinit var phoneLabel: TextView
    private lateinit var phoneContainer: LinearLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        var initEmail: String = "";
        var initName: String = "";
        var initPhone: String = "";

        switchCompat = findViewById(R.id.switchCompat)
        name = findViewById(R.id.name)
        nameLayout = findViewById(R.id.nameLayout)
        nameLabel = findViewById(R.id.nameLabel)

        back = findViewById(R.id.back)
        save = findViewById(R.id.save)

        email = findViewById(R.id.email)
        emailLabel = findViewById(R.id.EmailLabel)
        emailLayout = findViewById(R.id.emailLayout)

        phone = findViewById(R.id.phone)
        phoneLayout = findViewById(R.id.phoneLayout)
        phoneLabel = findViewById(R.id.phoneLabel)
        phoneContainer = findViewById(R.id.phoneContainer)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val db = FirebaseFirestore.getInstance()

            // Access the user's document using their UID
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->

                    if (document != null && document.exists()) {

                        val username = document.getString("username")
                        val email = document.getString("email")
                        val phone = document.getString("phone")


                        if (username != null) {
                            initName = username
                        };
                        if (email != null) {
                            initEmail = email
                        };
                        if (phone != null) {
                            initPhone = phone
                        };
                        // Use the retrieved data to update UI
                        name.setText(username)  // Example: Update name field with username
                        this.email.setText(email)  // Example: Update email field with email

                        Log.d("UserData", "Username: $username, Email: $email")
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

        } else {
            // User is not authenticated
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show()
        }


        switchCompat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                phoneContainer.visibility = View.VISIBLE
            } else {
                phoneContainer.visibility = View.GONE

            }
        }


        save.setOnClickListener{
            resetStyles()
            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val phoneText = phone.text.toString().trim()

            var hasError = false

            if (nameText.isEmpty() || nameText.length < 3) {
                applyErrorStyle(name, nameLabel, nameLayout)
                nameLayout.error = if (nameText.isEmpty()) {
                    "Required Field."
                } else {
                    "Minimum length is 3 characters."
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

            // Validate phone
            if (phoneText.isEmpty() || phoneText.length != 8 ) {
                applyErrorStyle(email,emailLabel,emailLayout)
                emailLayout.error = if (emailText.isEmpty()) {
                    "Required Field."
                } else {
                    "Provide a valid Phone number."
                }
                hasError = true
            }

            if (!hasError) {
                val db = FirebaseFirestore.getInstance()
                val user = FirebaseAuth.getInstance().currentUser

                if (user != null) {
                    val userDocRef = db.collection("users").document(user.uid)

                    val updatedData = hashMapOf<String, Any>()

                    // Update the name if it has changed
                    if (nameText != initName) {
                        updatedData["username"] = nameText
                    }

                    // Update the email if it has changed
                    if (emailText != initEmail) {
                        updatedData["email"] = emailText
                    }

                    // Update the phone only if it's not empty and was not previously saved
                    if (switchCompat.isChecked && phoneText.isNotEmpty()) {
                        updatedData["phone"] = phoneText
                    }

                    // Update Firestore document if there are any changes
                    if (updatedData.isNotEmpty()) {

                        userDocRef.update(updatedData)
                            .addOnSuccessListener {
                                name.text = null
                                phone.text = null
                                email.text = null
                                Toast.makeText(this, "Information saved successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Error saving data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show()
                    }
                }
            }



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

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(name,nameLabel,nameLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    nameLayout.error = null
                    nameLayout.isErrorEnabled = false
                }
            }
        })


        phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(phone,phoneLabel,phoneLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    phoneLayout.error = null
                    phoneLayout.isErrorEnabled = false
                }
            }
        })


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
        email.setBackgroundResource(R.drawable.edittext_border) // Normal border
        emailLayout.error = null
        emailLabel.setTextColor(Color.parseColor("#333333")) // D
        name.setBackgroundResource(R.drawable.edittext_border) // Normal border
        nameLayout.error = null
        nameLabel.setTextColor(Color.parseColor("#333333")) // Default label color

        phone.setBackgroundResource(R.drawable.edittext_border) // Normal border
        phoneLayout.error = null
        phoneLabel.setTextColor(Color.parseColor("#333333")) // D
    }
    // Email Validation Method using Regex
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}