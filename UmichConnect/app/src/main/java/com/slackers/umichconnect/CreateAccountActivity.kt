package com.slackers.umichconnect

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

@IgnoreExtraProperties
data class User(val email: String? = null,
                val name: String? = null, val bio: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        auth = FirebaseAuth.getInstance()
        val emailField = findViewById<EditText>(R.id.emailCreateAccount)
        val nameField = findViewById<EditText>(R.id.nameCreateAccount)
        val bioField = findViewById<EditText>(R.id.bioCreateAccount)
        val passField = findViewById<EditText>(R.id.passwordCreateAccount)

        val signUpButt = findViewById<Button>(R.id.signUpCreateAccount)
        signUpButt.setOnClickListener {
            accountCreation(emailField.getText().toString(), passField.getText().toString(),
                nameField.getText().toString(), bioField.getText().toString())

        }


    }

    private fun accountCreation(email: String, password: String, name: String, bio: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)

                    val user = Firebase.auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                        photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user?.let {
                                    // Name, email address, and profile photo Url
                                    val name = user.displayName.toString()
                                    val email = user.email.toString()
                                    val uid = user.uid
                                    writeNewUser(uid, name, email, bio)
                                }
                            }
                        }


                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun writeNewUser(userId: String, name: String, email: String, bio: String) {
        database = Firebase.database.reference
        val user = User(email, name, bio)

        database.child("users").child(userId).setValue(user)
    }
}



