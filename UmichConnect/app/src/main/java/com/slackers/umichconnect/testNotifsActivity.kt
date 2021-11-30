package com.slackers.umichconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast


class testNotifsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_notifs)
        Handler(Looper.getMainLooper()).postDelayed(
            {  Toast.makeText(this, "test test test", Toast.LENGTH_SHORT).show() },
            3000
        )
    }
}