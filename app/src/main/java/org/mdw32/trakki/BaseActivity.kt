package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class BaseActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // Initially load HomeFragment
        replaceFragment(HomeFragment())

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        fab = findViewById(R.id.fab)

        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // If Home is selected again, just make sure it's shown
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_transactions -> {
                    replaceFragment(TransactionsFragment())
                    true
                }
                R.id.nav_recurring -> {
                    replaceFragment(ReccuringFragment())
                    true
                }
                R.id.nav_profil -> {
                    replaceFragment(ProfilFragment())
                    true
                }
                else -> false
            }
        }


        // Handle FAB click to add new transaction
        fab.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        // Handle device back button to navigate back to HomeFragment if another fragment is visible
        onBackPressedDispatcher.addCallback(this) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment !is HomeFragment) {
                // Pop the current fragment and navigate back to HomeFragment
                supportFragmentManager.popBackStack()
                // Update the bottom navigation to reflect the HomeFragment
                bottomNavigationView.selectedItemId = R.id.nav_home
            } else {
                // If already at HomeFragment, exit the app
                super.onBackPressed()
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // If HomeFragment is already in the back stack, do not add it again
        if (fragment is HomeFragment) {
            // Replace HomeFragment but don't add it to the back stack
            transaction.replace(R.id.fragment_container, fragment)
        } else {
            // For other fragments, replace and add to back stack
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}
