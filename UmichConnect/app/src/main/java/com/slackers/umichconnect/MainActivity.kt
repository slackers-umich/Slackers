package com.slackers.umichconnect

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = Intent(this, LocationUpdateService::class.java)
        startService(intent)
        var uid: String? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, testNotifsActivity::class.java)
            startActivity(intent)
        }
        val buttonConnectionsPage = findViewById<Button>(R.id.buttonConnectionsPage)
        buttonConnectionsPage.setOnClickListener {
            val intent = Intent(this, ConnectionsPage::class.java)
            startActivity(intent)
        }

        val signOutButton = findViewById<Button>(R.id.SignOutButton)
        signOutButton.setOnClickListener{
            Firebase.auth.signOut()
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
        }

        val user = Firebase.auth.currentUser
        user?.let {
            uid = user.uid
        }
        if (user != null) {
            Toast.makeText(this, "User signed in uid: " + uid, Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }



    }

}