package com.slackers.umichconnect
import android.content.Context
import android.media.session.MediaSession
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.full.declaredMemberProperties

object NearbyListUserStore: CoroutineScope by MainScope() {
    private const val TAG = "NearbyListUserStore"
    val nearbyusers = arrayListOf<NearbyListUser?>()
    private lateinit var mDatabase: DatabaseReference

    private lateinit var queue: RequestQueue

    fun setNearbyUsers(context: Context, nearbyMacs: ArrayList<String?>, completion: () -> Unit) {
        // TODO: check if array is empty --> is so make toast
        Log.d(TAG, "setNearbyUsers()")
        nearbyusers.clear()
//        for (address in nearbyMacs) {
//            if (address != null) {
//                Log.d(TAG, address)
//                macToUser(address)
//            }
//        }
        nearbyusers.add(NearbyListUser("Jeffery", null))
        nearbyusers.add(NearbyListUser("Rachel", null))
        nearbyusers.add(NearbyListUser("Gloria", null))
        nearbyusers.add(NearbyListUser("Eddie", null))
        completion()
    }

    private fun macToUser(mac: String) {
        /* Takes MAC address and creates NearbyListUser with corresponding user's info
           and adds to nearbyusers if user exists */
        mDatabase = Firebase.database.getReference("users")
        var name: String? = ""
        var imgUrl: String? = ""
        // TODO: change to async instead of blocking call? (would have to change setNearbyUsers() too)
        mDatabase.child(mac).get().addOnSuccessListener {
            Log.d(TAG, "$mac 1")
            Log.d("firebase", "Got name ${it.child("name").value}")
            name = it.child("name").value.toString()
            imgUrl = it.child("photoUri").value.toString()
            if (name != null && name != "null") {
                nearbyusers.add(NearbyListUser(name, imgUrl))
            }
            Log.d(TAG, "$mac 2")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting from firebase", it)
        }
    }
}