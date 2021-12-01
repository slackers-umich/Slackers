package com.slackers.umichconnect

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import com.slackers.umichconnect.NearbyListUserStore
import com.slackers.umichconnect.NearbyListUserStore.setNearbyUsers
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class LocationUpdateService : Service() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private var currentLocation: Location? = null
    lateinit var builder: Notification.Builder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_REQUEST_CODE = 1
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    var uid: String? = null

    override fun onBind(intent: Intent): IBinder?
    {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val runable = Runnable {
            var update = 1
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val user = Firebase.auth.currentUser
            user?.let {
                uid = user.uid
            }
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    //and get current nearby
                    getLocation()
                    doDiscovery()
                    //get users nearby
                    Log.e("test1", "testnotif")
                }
            }, 0, 5000)

            var database = Firebase.database.reference
            //on database change
            database.child("users/" + uid + "/nearbyUsers").addValueEventListener(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nearbyUsers : MutableList<String> = ArrayList()
                    val oldNearbyUsers: MutableList<String> = ArrayList()
                    //set nearby users and old nearby users to arraylists
                    for (h in snapshot.children){
                        val user = h.getValue(String::class.java)
                        nearbyUsers.add(user.toString())
                    }
                    database.child("users/" + uid + "/oldNearbyUsers").get().addOnSuccessListener {
                        for (h in it.children){
                            val user = h.getValue(String::class.java)
                            oldNearbyUsers.add(user.toString())
                        }
                    }
                    //delay the checker since .get() takes a bit
                    Handler().postDelayed({
                        val tempNearby = HashSet(nearbyUsers)
                        val tempOld = HashSet(oldNearbyUsers)
                        if (tempNearby != tempOld)
                        {
                            if (update == 0)
                            {
                                val intent = Intent(applicationContext, NearbyActivity::class.java)
                                createNotification("There are new users in the area. Come see who they are!", intent)
                            }
                            database.child("users/" + uid + "/oldNearbyUsers").setValue(nearbyUsers)
                        }
                    }, 50)
                    database.child("users/" + uid + "/update").setValue(0)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
            database.child("users/" + uid + "/connections").addValueEventListener(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    Handler().postDelayed({
                        if (update == 0)
                        {
                            val intent = Intent(applicationContext, NearbyActivity::class.java)
                            createNotification("You have a new connection request!", intent)
                        }
                        database.child("users/" + uid + "/update").setValue(0)
                        update = 0
                    }, 50)

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            )

        }

        val thread = Thread(runable)
        thread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        var database = Firebase.database.reference
        database.child("users/" + uid + "/update").setValue(1)
    }

    private fun createNotification(contentText: String, intentNotif: Intent) {
        val intent2 = intentNotif
        val pendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
                .setContentTitle("Umich Connect")
                .setContentText(contentText)
                .setAutoCancel(true)
        }
        notificationManager.notify(1234, builder.build())
    }

    private fun doDiscovery() {
        var database = Firebase.database.getReference("users")
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
                        setNearbyUsers(applicationContext, nearby) {
                        }
                    }.addOnFailureListener{
                        Log.e(TAG, "Error getting nearby lng users from firebase", it)
                    }
            }.addOnFailureListener{
                Log.e(TAG, "Error getting nearby lat users from firebase", it)
            }
    }

    private fun getLocation(){
        var database = Firebase.database.getReference("users")
        var auth = FirebaseAuth.getInstance()

        val cts = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    // update location in db
                    val lat = location.latitude
                    val lng = location.longitude
                    Log.d(TAG,
                        "Current location received: $lat,$lng"
                    )
                    val currentUser = auth.currentUser
                    database.child(currentUser!!.uid)
                        .child("latitude").setValue(lat)
                    database.child(currentUser.uid)
                        .child("longitude").setValue(lng)
                    currentLocation = location
                }
            }
    }
}