package com.slackers.umichconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class OwnProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_own_profile)
        //TODO retrieve users own data
        //TODO add more parameters only available for your own profile




        val name: TextView = findViewById<TextView>(R.id.et_name) as TextView;
        name.text = userRecord.name;
        val major: TextView = findViewById<TextView>(R.id.et_major) as TextView;
        major.text = userRecord.major;
        val year: TextView = findViewById<TextView>(R.id.et_year) as TextView;
        year.text = userRecord.year;
        val interests: TextView = findViewById<TextView>(R.id.et_interests) as TextView;
        interests.text = userRecord.interests;
        val aboutMe: TextView = findViewById<TextView>(R.id.et_about_me) as TextView;
        aboutMe.text = userRecord.about_me;
    }


    fun takeToEdit(view: View){
        val intent = Intent(this, EditProfileActivity::class.java);
        startActivity(intent)
    }
}