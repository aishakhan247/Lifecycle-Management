package com.example.lifecyclemanagement

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.view.View

class DisplayActivity : AppCompatActivity() {
    var mTvName: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        //Get the text views
        mTvName = findViewById<View>(R.id.firstname) as TextView

        //Get the starter intent
        val receivedIntent = intent

        //Set the text views
        mTvName!!.text = receivedIntent.getStringExtra("N_DATA")


    }
}