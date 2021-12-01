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
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.slackers.umichconnect.NearbyListUserStore.setNearbyUsersAnon
import com.slackers.umichconnect.databinding.ActivityAnonNearbyBinding


class AnonNearbyActivity : AppCompatActivity() {
    private val TAG = "AnonNearbyActivity"
    private val PERMISSION_REQUEST_CODE = 1
    private var currentLocation: Location? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var locationCallback: LocationCallback
    private lateinit var view: ActivityAnonNearbyBinding
    private lateinit var nearbyListAdapter: AnonNearbyListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = ActivityAnonNearbyBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.getReference("users")
        if (auth.currentUser != null){
            val intent = Intent(this, NearbyActivity::class.java)
            startActivity(intent)
        }

        nearbyListAdapter = AnonNearbyListAdapter(this, nearbyusers)
        view.nearbyListView.setAdapter(nearbyListAdapter)

        // setup refreshContainer here later
        view.refreshContainer.setOnRefreshListener {
            refreshTimeline()
        }

        view.toSigninButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    Log.e(TAG, "Null location received")
                    return
                }
                for (location in locationResult.locations) {
                    val lat = location.latitude
                    val lng = location.longitude
                    Log.d(TAG,
                        "New location received: $lat,$lng"
                    )
                    if (significantMove(location)) {
                        Log.d(TAG, "Significant location change")
                        currentLocation = location
                        refreshTimeline()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
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
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
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

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
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
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun refreshTimeline() {
        Log.d(TAG, "refreshTimeline()")
        view.refreshContainer.isRefreshing = true
        doDiscovery()
    }

    /**
     * Find nearby users given device location
     */
    private fun doDiscovery() {
        Log.d(TAG, "doDiscovery()")

        // make call to get nearby users using coordinates
        if (currentLocation == null) {
            Log.e(TAG, "Location is null, can't do discovery")
            return
        }
        val nearbyLat = mutableSetOf<String>()
        val nearbyLng = mutableSetOf<String>()
        val lat = currentLocation!!.latitude
        val lng = currentLocation!!.longitude
        val startLat = lat - 0.001
        val endLat = lat + 0.001
        val startLng = lng - 0.001
        val endLng = lng + 0.001
        database.orderByChild("latitude").startAt(startLat).endAt(endLat)
            .get().addOnSuccessListener {
                val nearbyLatObj = it.value as HashMap<*, *>
                nearbyLatObj.forEach { (key, _) ->
                    Log.d(TAG, "Nearby lat user found: $key")
                    nearbyLat.add(key.toString())
                }
                database.orderByChild("longitude").startAt(startLng).endAt(endLng)
                    .get().addOnSuccessListener {
                        val nearbyLngObj = it.value as HashMap<*, *>
                        nearbyLngObj.forEach { (key, _) ->
                            Log.d(TAG, "Nearby lng user found: $key")
                            nearbyLng.add(key.toString())
                        }
                        val nearby = nearbyLat.intersect(nearbyLng)
                        Log.d(TAG, "Nearby users: $nearby")
                        setNearbyUsersAnon(applicationContext, nearby) {
                            runOnUiThread {
                                // inform the list adapter that data set has changed
                                // so that it can redraw the screen.
                                Log.d(TAG, "setNearbyUsersAnon() completed")
                                nearbyListAdapter.notifyDataSetChanged()
                            }
                            // stop the refreshing animation upon completion:
                            view.refreshContainer.isRefreshing = false
                        }
                    }.addOnFailureListener{
                        Log.e(TAG, "Error getting nearby lng users from firebase", it)
                    }
            }.addOnFailureListener{
                Log.e(TAG, "Error getting nearby lat users from firebase", it)
            }
    }

    private fun significantMove(location: Location): Boolean {
        if (currentLocation == null) {
            return true
        }
        val lat1 = location.latitude
        val lon1 = location.longitude
        val lat2 = currentLocation!!.latitude
        val lon2 = currentLocation!!.longitude
        if (getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) > 0.05) {
            return true
        }
        return false
    }

    private fun getDistanceFromLatLonInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6371 // Radius of the earth in km
        val dLat = deg2rad(lat2 - lat1)  // deg2rad below
        val dLon = deg2rad(lon2 - lon1)
        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                    cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Distance in km
    }

    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI/180)
    }
}