package org.mdw32.trakki

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity:AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailLabel: TextView

    private lateinit var password: EditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordLabel: TextView
    private lateinit var forget: TextView
    private lateinit var signup: TextView
    private lateinit var customGoogleButton: LinearLayout
    private lateinit var bottom: LinearLayout
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var login: Button
    private lateinit var mAuth: FirebaseAuth
    private val RC_SIGN_IN = 9001 // Request code for Google Sign-In


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        password = findViewById(R.id.password)
        passwordLayout = findViewById(R.id.passwordLayout)
        passwordLabel = findViewById(R.id.passwordLabel)



        email = findViewById(R.id.email)
        emailLabel = findViewById(R.id.EmailLabel)
        emailLayout = findViewById(R.id.emailLayout)
        login = findViewById(R.id.login)
        forget = findViewById(R.id.forget)
        signup = findViewById(R.id.signup)
        customGoogleButton = findViewById(R.id.customGoogleButton)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        bottom = findViewById(R.id.bottom)





        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // You can find this in the firebase console
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        customGoogleButton.setOnClickListener {
            signInWithGoogle()
        }






        forget.setOnClickListener {



            val intent = Intent(this, ForgetActivity::class.java)
                startActivity(intent)


        }
        signup.setOnClickListener {



            val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)


        }

        login.setOnClickListener {

            resetStyles()
            val passwordText = password.text.toString().trim()
            val emailText = email.text.toString().trim()

            var hasError = false

            if (passwordText.isEmpty() || passwordText.length < 8) {
                applyErrorStyle(password, passwordLabel, passwordLayout)
                passwordLayout.error = if (passwordText.isEmpty()) {
                    "Required Field."
                } else {
                    "Minimum length is 8 characters."
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
            showLoading(true)
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful*
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            // You can access the user info here, for example, username or other details
                            // Redirect to the home or dashboard screen
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            showLoading(false)
                            val intent = Intent(this, BaseActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)
                            finish()  // Close the login activity
                        }
                    } else {
                        // Login failed
                        showLoading(false)

                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
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
                if (!s.isNullOrEmpty() && isValidEmail(s.toString())) {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }
        })



    }


    // Handle the result from Google Sign-In intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Google Sign-In was successful, authenticate with Firebase
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign-In failed
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Authenticate with Firebase using the Google account
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    val user = mAuth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    // Navigate to the next screen (e.g., home screen)
                    navigateToHome()
                } else {
                    // Sign-in failed
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun navigateToHome() {
        val intent = Intent(this, BaseActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()  // Close the login activity
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
        password.setBackgroundResource(R.drawable.edittext_border) // Normal border
        passwordLayout.error = null
        passwordLabel.setTextColor(Color.parseColor("#333333")) // Default label color
    }
    // Email Validation Method using Regex
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
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