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

    fun setNearbyUsers(context: Context, nearby: Set<String>, completion: () -> Unit) {
        // TODO: check if set is empty --> is so make toast about no users in area
        Log.d(TAG, "setNearbyUsers()")
        mDatabase = Firebase.database.getReference("users")
        nearbyusers.clear()
        val n = nearby.size
        nearby.forEach {
            var name: String = ""
            var imgUrl: String = ""
            mDatabase.child(it).get().addOnSuccessListener {
                Log.d(TAG, "Got name ${it.child("name").value}")
                name = it.child("name").value.toString()
                imgUrl = it.child("photoUri").value.toString()
                nearbyusers.add(NearbyListUser(name, imgUrl))
                if (nearbyusers.size == n) {
                    completion()
                }
            }.addOnFailureListener{
                Log.e(TAG, "Error getting user info from firebase", it)
            }
        }
    }
}