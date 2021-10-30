package com.slackers.umichconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast


class EditProfile : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etMajor:EditText
    lateinit var etYear: EditText
    lateinit var etInterests:EditText
    lateinit var etAboutMe:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        viewInitializations()
    }


    fun viewInitializations() {

        etName = findViewById(R.id.et_name)
        etMajor = findViewById(R.id.et_major)
        etYear  = findViewById(R.id.et_year)
        etInterests = findViewById(R.id.et_interests)
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
        if (etMajor.text.toString().equals("")) {
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
        }
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

    fun performEditProfile (view: View) {
        if (validateInput()) {

            // Input is valid, here send data to your server

            val name = etName.text.toString()
            val major = etMajor.text.toString()
            val year = etYear.text.toString()
            val interests = etInterests.text.toString()
            val aboutMe = etAboutMe.text.toString()

            Toast.makeText(this,"Profile Update Successfully",Toast.LENGTH_SHORT).show()
            // Here you can call you API
            //TODO API CALL
        }
    }

}