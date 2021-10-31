package com.slackers.umichconnect

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.slackers.umichconnect.NearbyListUserStore.nearbyusers
import com.slackers.umichconnect.databinding.ActivityNearbyBinding
import android.util.Log
import com.slackers.umichconnect.NearbyListUserStore.setNearbyUsers
import android.view.View


class NearbyActivity : AppCompatActivity() {
    private val TAG = "NearbyActivity"
    private lateinit var view: ActivityNearbyBinding
    private lateinit var nearbyListAdapter: NearbyListAdapter
    private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val nearbyMacs = arrayListOf<String?>()
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

        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Log.d(TAG, "Fine location access denied")
                finish()
            }
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device doesn't support bluetooth")
        }

        if (mBluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode != Activity.RESULT_OK) {
                    Log.e(TAG, "Bluetooth enabled failed")
                }
            }
            startForResult.launch(enableBtIntent)
        }

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)

        // Register for broadcasts when discovery has finished
        val filter2 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mReceiver, filter2)
    }

    override fun onStart() {
        super.onStart()

        refreshTimeline()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Make sure we're not doing discovery anymore
        mBluetoothAdapter.cancelDiscovery()

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver)
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val mReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d(TAG, "device found")
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        val deviceHardwareAddress = device.address // MAC address
                        // TODO: check for duplicate mac addresses
                        nearbyMacs.add(deviceHardwareAddress)
                        Log.d(TAG, deviceHardwareAddress)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d(TAG, "scan finished")
                    setNearbyUsers(applicationContext, nearbyMacs) {
                        runOnUiThread {
                            // inform the list adapter that data set has changed
                            // so that it can redraw the screen.
                            nearbyListAdapter.notifyDataSetChanged()
                        }
                        // stop the refreshing animation upon completion:
                        view.refreshContainer.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun refreshTimeline() {
        // TODO: don't refresh if still looking
        Log.d(TAG, "refreshTimeline()")
        doDiscovery()
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private fun doDiscovery() {
        Log.d(TAG, "doDiscovery()")

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering() ?: false) {
            mBluetoothAdapter.cancelDiscovery()
        }

        nearbyMacs.clear()

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery()
    }
}