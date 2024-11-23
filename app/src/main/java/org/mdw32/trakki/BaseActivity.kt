package org.mdw32.trakki
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class BaseActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var fab : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        replaceFragmment(HomeFragment())
         bottomNavigationView = findViewById(R.id.bottom_navigation)
        fab = findViewById(R.id.fab)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    replaceFragmment(HomeFragment())
                    true
                }
                R.id.nav_transactions -> {
                    replaceFragmment(TransactionsFragment())
                    true
                }
                R.id.nav_recurring -> {
                    replaceFragmment(ReccuringFragment())
                    true
                }
                R.id.nav_profil -> {
                    replaceFragmment(ProfilFragment())
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener{
            var intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }





    private fun replaceFragmment( fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()    }
}
