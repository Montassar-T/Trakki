package org.mdw32.trakki

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView


class ExpenseActivity: BaseActivity() {



    private lateinit var back : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.fragment_expense)



        back = findViewById(R.id.back)

//        back.setOnClickListener{
//            val intent = Intent(this, TransactionsActivity::class.java)
//            startActivity(intent)
//
//        }
    }
}