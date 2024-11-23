package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity

class HelpActivity: ComponentActivity() {

    private lateinit var text1 :TextView
    private lateinit var text2 :TextView
    private lateinit var text3 :TextView

    private lateinit var arrow1 :ImageView
    private lateinit var arrow2 :ImageView
    private lateinit var arrow3 :ImageView
    private lateinit var back :ImageView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        text1 = findViewById(R.id.text_first)
        text2 = findViewById(R.id.text_second)
        text3 = findViewById(R.id.text_third)


        arrow1 = findViewById(R.id.first)
        arrow2 = findViewById(R.id.second)
        arrow3 = findViewById(R.id.third)
        back = findViewById(R.id.back)



        arrow1.setOnClickListener {
            if(text1.visibility == View.VISIBLE){
                arrow1.animate().rotation(0f).setDuration(200).start()
                text1.visibility = View.GONE
            }else{
                arrow1.animate().rotation(180f).setDuration(200).start()
                text1.visibility = View.VISIBLE
            }
        }
        arrow2.setOnClickListener {
            if(text2.visibility == View.VISIBLE){
                arrow2.animate().rotation(0f).setDuration(200).start()
                text2.visibility = View.GONE
            }else{
                arrow2.animate().rotation(180f).setDuration(200).start()

                text2.visibility = View.VISIBLE
            }
        }
        arrow3.setOnClickListener {
            if(text3.visibility == View.VISIBLE){
                arrow3.animate().rotation(0f).setDuration(200).start()
                text3.visibility = View.GONE
            }else{
                arrow3.animate().rotation(180f).setDuration(200).start()

                text3.visibility = View.VISIBLE
            }
        }

        back.setOnClickListener {
           finish()
        }

    }
}