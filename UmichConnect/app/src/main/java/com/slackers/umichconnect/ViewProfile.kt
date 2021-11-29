package com.slackers.umichconnect

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

// new since Glide v4
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    // leave empty for now
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Glide default Bitmap Format is set to RGB_565 since it
        // consumed just 50% memory footprint compared to ARGB_8888.
        // Increase memory usage for quality with:
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
    }

}

class ViewProfile : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var storageRef = FirebaseStorage.getInstance().reference
    var imageUri: Uri? = null
    var imagePath: String? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Bundle b = getIntent().getExtras();
        var profile_id = "-1";
        if(savedInstanceState!=null){
            profile_id = savedInstanceState.getString("profile_id").toString();
        }
        setContentView(R.layout.activity_view_profile)

        //call to api to get userdata based on id
        //might not be needed
        database = Firebase.database.reference

        //might need to wait for the result
        database.child("users").child(profile_id.toString()).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            user = it.value as User?
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }




        //api call to get profile info from database
        val name: TextView = findViewById<TextView>(R.id.et_name) as TextView;
        name.text = user?.name ?: "";
        //load their image
        val imageView = findViewById<ImageView>(R.id.rImage)
        Glide.with(this)
            .load(user?.photoUri)
            .into(imageView)
        /*val major: TextView = findViewById<TextView>(R.id.et_major) as TextView;
        major.text = userRecord.major;
        val year: TextView = findViewById<TextView>(R.id.et_year) as TextView;
        year.text = userRecord.year;
        val interests: TextView = findViewById<TextView>(R.id.et_interests) as TextView;
        interests.text = userRecord.interests;*/
        val aboutMe: TextView = findViewById<TextView>(R.id.et_about_me) as TextView;
        aboutMe.text = user?.bio ?: "";

    }
}