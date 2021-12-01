package com.slackers.umichconnect

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlinx.coroutines.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer

class testNotifsActivity : AppCompatActivity() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    var uid: String? = null
    val nearbyUsers : MutableList<String> = ArrayList()
    val oldNearbyUsers: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_notifs)
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
        database.child("users/" + uid + "/nearbyUsers").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (h in snapshot.children){
                    val user = h.getValue(String::class.java)
                    Log.e("tag", user.toString())

                    nearbyUsers.add(user.toString())

                }
                val children2 = database.child("users/" + uid + "/oldNearbyUsers/").get()




                /*if (children == children2)
                {
                    createNotification()
                }*/

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    private fun createNotification() {
        val intent = Intent(this, afterNotificationActivity::class.java)
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
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
        }
        notificationManager.notify(1234, builder.build())
    }


}