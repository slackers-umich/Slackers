package com.slackers.umichconnect
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object NearbyListUserStore {
    private const val TAG = "NearbyListUserStore"
    val nearbyusers = arrayListOf<NearbyListUser?>()
    private lateinit var mDatabase: DatabaseReference
    private val nFields = NearbyListUser::class.declaredMemberProperties.size

    private lateinit var queue: RequestQueue
    private const val serverUrl = "https://mobapp.eecs.umich.edu/" // TODO: replace w back-end server IP?

    fun setNearbyUsers(context: Context, nearbyMacs: ArrayList<String?>, completion: () -> Unit) {
        // TODO: check if array is empty --> is so make toast
        Log.d(TAG, "setNearbyUsers()")
        nearbyusers.clear()
        for (address in nearbyMacs) {
            if (address != null) {
                Log.d(TAG, address)
                // TODO: change call below to make it wait for macToUser response if it's not working
                macToUser(address)
            }
        }
    }

//    private fun macToUser(mac: String): NearbyListUser? {
//    /* Takes MAC address and returns NearbyListUser with corresponding user's info
//       or null if no user exists */
//        mDatabase = Firebase.database.getReference("users")
//        var name: String? = null
//        var imgUrl: String? = null
//        // TODO: change to async instead of blocking call? (would have to change setNearbyUsers() too)
//        mDatabase.child(mac).child("name").get().addOnSuccessListener {
//            Log.d("firebase", "Got name ${it.value}")
//            name = it.value.toString()
//        }.addOnFailureListener{
//            Log.d("firebase", "User $mac doesn't exist", it)
//        }
//        if (name == null) {
//            return null
//        }
//        mDatabase.child(mac).child("image_url").get().addOnSuccessListener {
//            Log.d("firebase", "Got image url ${it.value}")
//            imgUrl = it.value.toString()
//        }.addOnFailureListener{
//            Log.i("firebase", "No profile image url", it)
//        }
//        return NearbyListUser(name, imgUrl)
//    }

    private fun macToUser(mac: String) {
        /* Takes MAC address and returns NearbyListUser with corresponding user's info
           or null if no user exists */
        mDatabase = Firebase.database.getReference("users")
        var name: String? = null
        var imgUrl: String? = null
        // TODO: change to async instead of blocking call? (would have to change setNearbyUsers() too)
        mDatabase.child(mac).get().addOnSuccessListener {
            Log.d("firebase", "Got name ${it.child("name").value}")
            name = it.child("name").value.toString()
            imgUrl = it.child("image_url").value.toString()
            if (name != null) {
                nearbyusers.add(NearbyListUser(name, imgUrl))
            }
        }.addOnFailureListener{
            Log.d("firebase", "User $mac doesn't exist", it)
        }
    }
}