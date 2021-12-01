package com.slackers.umichconnect

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class LocationUpdateService : Service() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    var uid: String? = null

    override fun onBind(intent: Intent): IBinder?
    {
       return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val runable = Runnable {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val user = Firebase.auth.currentUser
            user?.let {
                uid = user.uid
            }
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    //and get current nearby
                    //get users nearby
                    Log.e("test1", "testnotif")
                }
            }, 0, 5000)

            var database = Firebase.database.reference
            //on database change
            database.child("users/" + uid + "/nearbyUsers").addValueEventListener(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var update: Int? = null
                    database.child("users/" + uid + "/update").get().addOnSuccessListener{
                        update = it.value.toString().toInt()
                    }
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
                                createNotification("There are new users in the area. Come see who they are!")
                                database.child("users/" + uid + "/oldNearbyUsers").setValue(nearbyUsers)
                            }
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
                    var update: Int? = null
                    database.child("users/" + uid + "/update").get().addOnSuccessListener{
                        update = it.value.toString().toInt()
                    }
                    Handler().postDelayed({
                        Log.e("tag1", "$update")
                        if (update == 0)
                        {
                            createNotification("You have a new connection request!")
                        }
                        database.child("users/" + uid + "/update").setValue(0)
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

    private fun createNotification(contentText: String) {
        val intent2 = Intent(this, NearbyActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
        val contentView = RemoteViews(packageName, R.layout.activity_after_notification)
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
}