package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView


class IncomeActivity: BaseActivity() {


    private lateinit var back : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_income)
        back = findViewById(R.id.back)

//
//        back.setOnClickListener{
//            val intent = Intent(this, ::class.java)
//            startActivity(intent)
//
//        }

    }
}