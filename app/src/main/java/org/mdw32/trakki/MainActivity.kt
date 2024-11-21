package org.mdw32.trakki


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import org.mdw32.trakki.ui.DotIndicatorView
import org.mdw32.trakki.ui.adapter.ImagePagerAdapter

class MainActivity : ComponentActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotIndicatorView: DotIndicatorView
    private lateinit var handler: Handler
    private var imageList = listOf(R.drawable.image1, R.drawable.image2, R.drawable.image3) // Your image list
    private var imageCount = imageList.size // Dynamically set based on your image list size
    private lateinit var firebaseAnalytics: FirebaseAnalytics



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // The user is already logged in, redirect them to the home screen
            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Initialize the ViewPager2 and DotIndicatorView
        viewPager = findViewById(R.id.viewPager)
        dotIndicatorView = findViewById(R.id.dotIndicatorView)

        // Setup the dot indicators with the image count
        dotIndicatorView.setupIndicator(imageCount)

        // Set the adapter for the ViewPager2
        viewPager.adapter = ImagePagerAdapter(imageList)

        // Sync dots when the page is manually scrolled
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                dotIndicatorView.updateDotIndicator(position) // Update dots when page is selected
            }
        })

        // Setup auto-scroll functionality every 3 seconds
        handler = Handler(Looper.getMainLooper())
        val scrollRunnable = object : Runnable {
            override fun run() {
                // Move to the next page in the ViewPager2, looping back to the first page if needed
                val nextItem = (viewPager.currentItem + 1) % imageCount
                viewPager.setCurrentItem(nextItem, true) // Set the current item in ViewPager2
                handler.postDelayed(this, 6000) // Continue auto-scrolling every 3 seconds
            }
        }
        handler.postDelayed(scrollRunnable, 6000) // Start auto-scrolling after the delay



        val login = findViewById<TextView>(R.id.loginTextView);
        login.setOnClickListener {
            val intent =  Intent(this, LoginActivity::class.java);
            startActivity(intent);
            finish()

        }

        val startBtn = findViewById<Button>(R.id.startBtn);
        startBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java);
            startActivity(intent);
            finish()
        }




    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the handler callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null)
    }
}
