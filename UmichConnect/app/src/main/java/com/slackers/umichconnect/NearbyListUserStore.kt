package com.slackers.umichconnect
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object NearbyListUserStore {
    val nearbyusers = arrayListOf<NearbyListUser?>()
    private val nFields = NearbyListUser::class.declaredMemberProperties.size

    private lateinit var queue: RequestQueue
    private const val serverUrl = "https://mobapp.eecs.umich.edu/"
    //TODO:fix serverurl, add api call to get nearby users

    fun getNearbyUsers(context: Context, completion: () -> Unit) {
        nearbyusers.clear()
        nearbyusers.add(NearbyListUser("BOb", "null"))
    }
}