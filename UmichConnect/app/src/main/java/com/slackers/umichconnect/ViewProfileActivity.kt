package com.slackers.umichconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

/*
class ViewProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Bundle b = getIntent().getExtras();
        var profile_id = -1;
        if(savedInstanceState!=null){
            profile_id = savedInstanceState.getInt("profile_id");
        }
        setContentView(R.layout.activity_view_profile)

        //call to api to get userdata based on id
        UserRecord userRecord = FirebaseAuth.getInstance().getUser(profile_id);


        //api call to get profile info from database
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
}*/
