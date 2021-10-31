package com.slackers.umichconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text


class CreateAccount : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        auth = FirebaseAuth.getInstance()

        val signUpButt = findViewById<Button>(R.id.signupButt)
        signUpButt.setOnClickListener{
            
        }

        val emailField = findViewById<TextView>(R.id.emailInput)


    }

    fun accountCreation(email: String, password: String)
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener{ task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
            }
        })
    }





}