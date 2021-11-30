
package com.slackers.umichconnect

import android.Manifest
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
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class NearbyActivity : AppCompatActivity() {
    private val TAG = "NearbyActivity"
    private val PERMISSION_REQUEST_CODE = 1
    private var currentLocation: Location? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var view: ActivityNearbyBinding
    private lateinit var nearbyListAdapter: NearbyListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        view = ActivityNearbyBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        nearbyListAdapter = NearbyListAdapter(this, nearbyusers)
        view.nearbyListView.setAdapter(nearbyListAdapter)

        // setup refreshContainer here later
        view.refreshContainer.setOnRefreshListener {
            refreshTimeline()
        }

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
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (!granted) {
//                Log.d(TAG, "Fine location access denied")
//                finish()
//            }
//        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    Log.e(TAG, "Null location received")
                    return
                }
                for (location in locationResult.locations) {
                    Log.d(TAG,
                        "New location received: " + location.latitude + "," + location.longitude)
                    if (significantMove(location)) {
                        Log.d(TAG, "Significant location change")
                        // TODO: update location in db
                        currentLocation = location
                        refreshTimeline()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // TODO: call getCurrentLocation()
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
        val cts = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    currentLocation = location
                    refreshTimeline()
                }
                // TODO: handle if null
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
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    // TODO: add toast to do above
                    Log.e(TAG, "Location permissions denied")
                    finish()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // TODO: change?
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

        // TODO: make api call to get nearby users using coordinates

        // TODO: change nearbyMacs to be result of api call
//        setNearbyUsers(applicationContext, nearbyMacs) {
//            runOnUiThread {
//                // inform the list adapter that data set has changed
//                // so that it can redraw the screen.
//                Log.d(TAG, "setNearbyUsers() completed")
//                nearbyListAdapter.notifyDataSetChanged()
//            }
//            // stop the refreshing animation upon completion:
//            view.refreshContainer.isRefreshing = false
//        }
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