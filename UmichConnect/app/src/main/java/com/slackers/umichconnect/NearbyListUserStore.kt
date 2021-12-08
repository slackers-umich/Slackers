package com.slackers.umichconnect
import android.content.Context
import android.media.session.MediaSession
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.firebase.auth.FirebaseAuth
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
    val nearbyusersIds = arrayListOf<String>()
    var connections = arrayListOf<String>()
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth

    fun setNearbyUsers(context: Context, nearby: Set<String>, completion: () -> Unit) {
        nearbyusers.clear()
        nearbyusersIds.clear()
        connections.clear()
        if (nearby.isEmpty()) {
            // TODO: make toast about no users in area
//            mDatabase.child("${auth.currentUser!!.uid}/nearbyUsers")
//                .setValue(nearbyusersIds)
            completion()
            return
        }
        Log.d(TAG, "setNearbyUsers()")
        mDatabase = Firebase.database.getReference("users")
        auth = FirebaseAuth.getInstance()
        var n = nearby.size
        mDatabase.child("${auth.currentUser!!.uid}/connections").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.children.forEach() {
                        connections.add(it.value.toString())
                    }
                    Log.d(TAG, "Got connections: $connections")
                }
                nearby.forEach {
                    if (it == auth.currentUser!!.uid || it in connections) { n -= 1 }
                    else {
                        var name: String = ""
                        var imgUrl: String = ""
                        mDatabase.child(it).get().addOnSuccessListener {
                            name = it.child("name").value.toString()
                            imgUrl = it.child("photoUri").value.toString()
                            Log.d(TAG, "Got name $name")
                            nearbyusers.add(NearbyListUser(it.key.toString(), name, imgUrl))
                            nearbyusersIds.add(it.key.toString())
                            if (nearbyusers.size == n) {
//                                mDatabase.child("${auth.currentUser!!.uid}/nearbyUsers")
//                                    .setValue(nearbyusersIds)
                                completion()
                            }
                        }.addOnFailureListener{
                            Log.e(TAG, "Error getting user info from firebase", it)
                        }
                    }
                }
                if (n < 1) { completion() }
            }
    }

    fun setNearbyUsersAnon(context: Context, nearby: Set<String>, completion: () -> Unit) {
        nearbyusers.clear()
        if (nearby.isEmpty()) {
            // TODO: make toast about no users in area
            completion()
            return
        }
        Log.d(TAG, "setNearbyUsersAnon()")
        mDatabase = Firebase.database.getReference("users")
        val n = nearby.size
        nearby.forEach {
            var name: String = ""
            var imgUrl: String = ""
            mDatabase.child(it).get().addOnSuccessListener {
                name = it.child("name").value.toString()
                imgUrl = it.child("photoUri").value.toString()
                Log.d(TAG, "Got name $name")
                nearbyusers.add(NearbyListUser(it.key.toString(), name, imgUrl))
                nearbyusersIds.add(it.key.toString())
                if (nearbyusers.size == n) {
                    completion()
                }
            }.addOnFailureListener{
                Log.e(TAG, "Error getting user info from firebase", it)
            }
        }
    }
}