package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ProfilFragment : Fragment() {


    private lateinit var settings: LinearLayout
    private lateinit var change: LinearLayout
    private lateinit var info: LinearLayout
    private lateinit var help: LinearLayout
    private lateinit var back : ImageView
    private lateinit var logout : Button
    private lateinit var name : TextView
    private lateinit var email : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        settings = view.findViewById(R.id.settings)
        help = view.findViewById(R.id.help)
        back = view.findViewById(R.id.back)
        logout = view.findViewById(R.id.logout)
        change = view.findViewById(R.id.change)
        info = view.findViewById(R.id.info)
        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)


        back.setOnClickListener {
            // Replace ProfilFragment with HomeFragment
            (activity as BaseActivity).replaceFragment(HomeFragment())

            // Update the bottom navigation to select the HomeFragment
            (activity as BaseActivity).bottomNavigationView.selectedItemId = R.id.nav_home
        }

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

                        // Use the retrieved data to update UI
                        if (username != null) {
                            name.text =username.capitalize(Locale.ROOT)
                        } // Example: Update name field with username
                        this.email.text = email  // Example: Update email field with email

                        Log.d("UserData", "Username: $username, Email: $email")
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error getting user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

        } else {
            // User is not authenticated
            Toast.makeText(requireContext(), "No user signed in", Toast.LENGTH_SHORT).show()
        }


        settings.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            startActivity(intent)
        }
        change.setOnClickListener {
            val intent = Intent(requireActivity(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        info.setOnClickListener {
            val intent = Intent(requireActivity(), InfoActivity::class.java)
            startActivity(intent)
        }

        help.setOnClickListener {
            val intent = Intent(requireActivity(), HelpActivity::class.java)
            startActivity(intent)
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Logs out the user from Firebase
            val intent = Intent(requireContext(), LoginActivity::class.java) // Navigate to the Login screen
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear backstack
            startActivity(intent)
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        }
    }

}