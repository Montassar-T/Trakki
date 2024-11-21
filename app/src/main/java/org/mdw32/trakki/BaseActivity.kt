package org.mdw32.trakki
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

 class BaseActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        replaceFragmment(HomeFragment())
         bottomNavigationView = findViewById(R.id.bottom_navigation)

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
    }





    private fun replaceFragmment( fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()    }
}
