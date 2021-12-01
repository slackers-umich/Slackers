package com.slackers.umichconnect

import android.content.Context

import android.content.Intent


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder

import com.bumptech.glide.Registry

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth


// new since Glide v4
/*
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    // leave empty for now
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Glide default Bitmap Format is set to RGB_565 since it
        // consumed just 50% memory footprint compared to ARGB_8888.
        // Increase memory usage for quality with:
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
    }
}*/

import com.firebase.ui.storage.images.FirebaseImageLoader

import com.google.firebase.storage.StorageReference
import java.io.InputStream


// new since Glide v4
/*@GlideModule
class MyAppGlideModule : AppGlideModule() {
    // leave empty for now
      override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}*/


class ViewProfileActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var storageRef = FirebaseStorage.getInstance().reference
    var imageUri: Uri? = null
    var imagePath: String? = null

    var name: String = "null"
    var bio: String = "null"
    var photo: String = "null"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Bundle b = getIntent().getExtras();
        var profile_id = "-1";

        val intent = getIntent()
        profile_id = intent.getStringExtra("profile_id").toString()
        //profile_id = savedInstanceState?.getString("profile_id").toString();


        setContentView(R.layout.activity_view_profile)
        Log.d("AWD", profile_id)
        //call to api to get userdata based on id
        //might not be needed
        database = Firebase.database.reference

        val namefield: TextView = findViewById<TextView>(R.id.et_name) as TextView;
        val aboutMe = findViewById<TextView>(R.id.et_about_me)
        val imageView = findViewById<ImageView>(R.id.rImage)
        //might need to wait for the result

        database.child("users").child(profile_id.toString()).child("name").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            name = it.value as String
            namefield.setText(name)
            database.child("users").child(profile_id.toString()).child("bio").get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                bio = it.value as String
                aboutMe.setText(bio)
                database.child("users").child(profile_id.toString()).child("photoUri").get().addOnSuccessListener {
                    Log.i("firebase", "Got value ${it.value}")
                    photo = it.value as String
                    val imageref = storageRef.child(photo)
                    Log.i("firebase", "Image URL ${imageref}")
                    imageref.downloadUrl.addOnSuccessListener {Uri->

                        val imageURL = Uri.toString()

                        Log.i("firebase", "Image URL ${imageURL}")

                        Glide.with(this)
                            .load(imageURL)
                            .centerCrop()
                            .into(imageView)

                    }
                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }


        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }




        //api call to get profile info from database



        //load their image





        /*Glide.with(this)
         .load(photo)
         .into(imageView)*/

        /*val major: TextView = findViewById<TextView>(R.id.et_major) as TextView;
        major.text = userRecord.major;
        val year: TextView = findViewById<TextView>(R.id.et_year) as TextView;
        year.text = userRecord.year;
        val interests: TextView = findViewById<TextView>(R.id.et_interests) as TextView;
        interests.text = userRecord.interests;*/


        val connectButton = findViewById<Button>(R.id.connectionRequest)

        connectButton.setOnClickListener {
            sendConnection(profile_id, findViewById<TextView>(R.id.et_connectionMessage).getText().toString())
            finish()
        }

    }


    private fun sendConnection(writeId: String, message: String) {
        val user = Firebase.auth.currentUser
        val userId = user?.uid

        database = Firebase.database.reference

        if (userId != null) {

            database.child("users").child(writeId).child("pending").child(userId).setValue(message)

        }


    }
}