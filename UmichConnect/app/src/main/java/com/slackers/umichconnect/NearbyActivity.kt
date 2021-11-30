
package com.slackers.umichconnect

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.slackers.umichconnect.NearbyListUserStore.nearbyusers
import com.slackers.umichconnect.databinding.ActivityNearbyBinding
import android.util.Log
import com.slackers.umichconnect.NearbyListUserStore.setNearbyUsers
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class NearbyActivity : AppCompatActivity() {
    private val TAG = "NearbyActivity"
    private val PERMISSION_REQUEST_CODE = 1
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
    }

    override fun onStart() {
        super.onStart()

        // TODO: call getCurrentLocation()
        fusedLocationClient.getCurrentLocation()
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    refreshTimeline()
                }
                // TODO: handle if null
            }
    }

    override fun onResume() {
        super.onResume()

        // TODO: call requestLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun refreshTimeline() {
        Log.d(TAG, "refreshTimeline()")
        view.refreshContainer.isRefreshing = true
        doDiscovery()
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

    /**
     * Find nearby users given device location
     */
    private fun doDiscovery() {
        Log.d(TAG, "doDiscovery()")

        // TODO: make api call to get nearby users using coordinates

        // TODO: change nearbyMacs to be result of api call
        setNearbyUsers(applicationContext, nearbyMacs) {
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
}