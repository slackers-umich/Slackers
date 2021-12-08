package com.slackers.umichconnect

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.slackers.umichconnect.NearbyListUserStore.nearbyusers
import com.slackers.umichconnect.databinding.ActivityNearbyBinding
import android.util.Log
import com.slackers.umichconnect.NearbyListUserStore.setNearbyUsers
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.messaging.FirebaseMessaging


class NearbyActivity : AppCompatActivity() {
    private val TAG = "NearbyActivity"
    private val PERMISSION_REQUEST_CODE = 1
    private lateinit var bn: com.google.android.material.bottomnavigation.BottomNavigationView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var view: ActivityNearbyBinding
    private lateinit var nearbyListAdapter: NearbyListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = ActivityNearbyBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.getReference("users")
        if (auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val intent = Intent(this, LocationUpdateService::class.java)
        startService(intent)

        nearbyListAdapter = NearbyListAdapter(this, nearbyusers)
        view.nearbyListView.setAdapter(nearbyListAdapter)

        // setup refreshContainer here later
        view.refreshContainer.setOnRefreshListener {
            refreshTimeline()
        }

        bn = findViewById(R.id.bottom_navigation)
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_2 -> {
                    // put your code here
                    val intent = Intent(this, ConnectionsActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_3 -> {
                    val intent = Intent(this, AcceptDeclineActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_4 -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bn.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // TODO: request background location?
        // https://developer.android.com/training/location/permissions#request-background-location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }

        val uid = auth.currentUser?.uid
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("tagger", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result.toString()
            val db = Firebase.database.reference
            db.child("FCMTokens/$uid").setValue(token)
            // Log and toast
            Log.e("tagger", token)
        })
    }

    override fun onResume() {
        super.onResume()

        refreshTimeline()
    }

    override fun onDestroy() {
        super.onDestroy()
        val currentUser = auth.currentUser
        database.child(currentUser!!.uid)
            .child("latitude").removeValue()
        database.child(currentUser.uid)
            .child("longitude").removeValue()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Log.d(TAG, "Location permissions granted")
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // TODO: add toast to do above
                    Log.e(TAG, "Location permissions denied")
                    finish()
                }
                return
            }
        }
    }

    private fun refreshTimeline() {
        Log.d(TAG, "refreshTimeline()")
        view.refreshContainer.isRefreshing = true
        showNearby()
    }

    private fun showNearby() {
        Log.d(TAG, "showNearby()")
        val currentUser = auth.currentUser
        database.child("${currentUser!!.uid}/nearbyUsers").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val nearby: MutableSet<String> = mutableSetOf()
                it.result.children.forEach() {
                    nearby.add(it.value.toString())
                }
                Log.d(TAG, "Got nearbyUsers: $nearby")
                setNearbyUsers(applicationContext, nearby) {
                    runOnUiThread {
                        // inform the list adapter that data set has changed
                        // so that it can redraw the screen.
                        Log.d(TAG, "setNearbyUsers() completed")
                        nearbyListAdapter.notifyDataSetChanged()
                    }
                    // stop the refreshing animation upon completion:
                    view.refreshContainer.isRefreshing = false
                }
            }
            else {
                Log.e(TAG, "Error retrieving nearbyUsers from database")
            }
        }
    }
}