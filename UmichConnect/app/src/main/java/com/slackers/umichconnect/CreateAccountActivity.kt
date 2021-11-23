package com.slackers.umichconnect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import android.net.wifi.WifiManager
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage


@IgnoreExtraProperties
data class User(val email: String? = null, val name: String? = null,
                val bio: String? = null, val macAddr: String? = null, val photoUri: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var storageRef = FirebaseStorage.getInstance().reference
    var imageUri: Uri? = null
    var imagePath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        auth = FirebaseAuth.getInstance()
        val emailField = findViewById<EditText>(R.id.emailCreateAccount)
        val nameField = findViewById<EditText>(R.id.nameCreateAccount)
        val bioField = findViewById<EditText>(R.id.bioCreateAccount)
        val passField = findViewById<EditText>(R.id.passwordCreateAccount)
        val imageUpload = findViewById<ImageView>(R.id.pfpCreateAccount)
        val changePfpText = findViewById<TextView>(R.id.changePfpCreateAccount)
        val signUpButt = findViewById<Button>(R.id.signUpCreateAccount)


        val currentUser = auth.currentUser

        if (currentUser != null){
            val intent = Intent(this, NearbyActivity::class.java)
            startActivity(intent)
        }
        val getResult =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    imageUri = it.data?.getData()
                    imageUpload.setImageURI(imageUri)
                }
            }
        changePfpText.setOnClickListener{
            val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getResult.launch(openGalleryIntent)
        }
        signUpButt.setOnClickListener {
            accountCreation(emailField.getText().toString(), passField.getText().toString(),
                nameField.getText().toString(), bioField.getText().toString())
        }


    }
    //uploads image to firebase storage
    private fun uploadImageToFirebase(imageUri: Uri?, userId: String){
        imagePath = "users/$userId/profile.jpg"
        val fileReference = storageRef.child(imagePath!!)
        if (imageUri != null) {
            fileReference.putFile(imageUri)
        }
    }
    //creates the account through the firebase authentication
    private fun accountCreation(email: String, password: String, name: String, bio: String) {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val mac = wifiManager.connectionInfo.macAddress


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully Registered", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, NearbyActivity::class.java)
                    val user = Firebase.auth.currentUser
                    if (user != null) {
                        uploadImageToFirebase(imageUri, user.uid)
                    }
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                        photoUri = imageUri

                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user?.let {
                                    // Name, email address, and profile photo Url
                                    val name = user.displayName.toString()
                                    val email = user.email.toString()
                                    val uid = user.uid
                                    val photoUri = user.photoUrl.toString()
                                    writeNewUser(uid, name, email, bio, mac, imagePath.toString())

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
    //writes the user into the firebase
    private fun writeNewUser(
        userId: String,
        name: String,
        email: String,
        bio: String,
        macAddr: String,
        photoUri: String
    ) {
        database = Firebase.database.reference
        val user = User(email, name, bio, macAddr, photoUri)

        database.child("users").child(userId).setValue(user)
    }

}



