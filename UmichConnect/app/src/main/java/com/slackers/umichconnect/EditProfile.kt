package com.slackers.umichconnect

import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage

import android.content.Intent
import android.app.Activity
import android.net.Uri
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class EditProfileActivity : AppCompatActivity() {


    class EditProfile : AppCompatActivity() {
        private lateinit var database: DatabaseReference
        private lateinit var auth: FirebaseAuth
        var storageRef = FirebaseStorage.getInstance().reference
        var imageUri: Uri? = null
        var imagePath: String? = null


        lateinit var etName: EditText

        //lateinit var etMajor:EditText
        //lateinit var etYear: EditText
        //lateinit var etInterests:EditText
        lateinit var etAboutMe: EditText

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_edit_profile)

            val imageUpload = findViewById<ImageView>(R.id.pfpCreateAccount)
            val changePfpText = findViewById<TextView>(R.id.changePfpCreateAccount)
            val updateProfile = findViewById<Button>(R.id.updateAccount)

            val getResult =
                registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        imageUri = it.data?.getData()
                        imageUpload.setImageURI(imageUri)
                    }
                }


            changePfpText.setOnClickListener {
                val openGalleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getResult.launch(openGalleryIntent)
            }

            updateProfile.setOnClickListener {

            }

            viewInitializations()
        }

        //uploads image to firebase storage
        private fun uploadImageToFirebase(imageUri: Uri?, userId: String) {
            imagePath = "users/$userId/profile.jpg"
            val fileReference = storageRef.child(imagePath!!)
            if (imageUri != null) {
                fileReference.putFile(imageUri)
            }
        }

        fun viewInitializations() {

            etName = findViewById(R.id.et_name)
            //etMajor = findViewById(R.id.et_major)
            //etYear  = findViewById(R.id.et_year)
            //etInterests = findViewById(R.id.et_interests)
            etAboutMe = findViewById(R.id.et_about_me)


            // To show back button in actionbar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        }

        // Checking if the input in form is valid
        fun validateInput(): Boolean {
            if (etName.text.toString().equals("")) {
                etName.setError("Please Enter Your Name")
                return false
            }
            /*if (etMajor.text.toString().equals("")) {
                etMajor.setError("Please Enter Your Major")
                return false
            }
            if (etYear.text.toString().equals("")) {
                etYear.setError("Please Enter Your Year")
                return false
            }
            if (etInterests.text.toString().equals("")) {
                etInterests.setError("Please Enter Your Main Interests")
                return false
            }*/
            if (etAboutMe.text.toString().equals("")) {
                etAboutMe.setError("Please Fill the About Me section")
                return false
            }
            // checking the proper email format
            /*if (!isEmailValid(etEmail.text.toString())) {
                etEmail.setError("Please Enter Valid Email")
                return false
            }*/


            return true
        }

        fun isEmailValid(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        // Hook Click Event
        private fun profileEdit(name: String, bio: String) {
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val mac = wifiManager.connectionInfo.macAddress


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
        }

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
}