package com.slackers.umichconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import java.lang.Exception


class LogoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

//        handler = Handler()
//        handler.postDelayed(Runnable {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000)

        val background: Thread = object : Thread() {
            override fun run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep((3 * 1000).toLong())

                    // After 5 seconds redirect to another intent
                    val i = Intent(baseContext, LoginActivity::class.java)
                    startActivity(i)

                    //Remove activity
                    finish()
                } catch (e: Exception) {
                }
            }
        }
        // start thread
        background.start()
    }
}