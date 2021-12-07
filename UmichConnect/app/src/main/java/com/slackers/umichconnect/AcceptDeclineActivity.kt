package com.slackers.umichconnect

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.FieldPosition

class AcceptDeclineActivity: AppCompatActivity() {
    private lateinit var bn: com.google.android.material.bottomnavigation.BottomNavigationView
    //var array = arrayOf("Melbourne", "Vienna", "Vancouver", "Toronto", "Calgary", "Adelaide", "Perth", "Auckland", "Helsinki", "Hamburg", "Munich", "New York", "Sydney", "Paris", "Cape Town", "Barcelona", "London", "Bangkok")
    //private lateinit var bn: com.google.android.material.bottomnavigation.BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_decline)

        if (Firebase.auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        bn = findViewById(R.id.bottom_navigation)
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, NearbyActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_2 -> {
                    // put your code here
                    val intent = Intent(this, ConnectionsActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_3 -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_4 -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bn.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

//        val adapter = ArrayAdapter(this,
//            R.layout.activity_accept_decline_listview_item, array)
//
        val listView: ListView = findViewById(R.id.accept_decline_listview)
        listView.adapter = MyCustomAdapter(this)

        val intent = Intent(this, LocationUpdateService::class.java)
        startService(intent)

//        listView.setAdapter(adapter)


    }

    private class MyCustomAdapter(context: Context): BaseAdapter(){
        private val mContext: Context

        private val names = arrayListOf<String>()

        private val pending_pairs = arrayListOf<Map<String, String>>()

        private var firstTime = true

        init{
            mContext = context


            var uid: String? = null
            var database = Firebase.database.reference
            val user = Firebase.auth.currentUser
            user?.let {
                uid = user.uid
            }

            database.child("users/" + uid + "/pending").get().addOnSuccessListener {
                for (child in it.getChildren()) {
                    database.child("users").child(child.getKey().toString()).child("name").get()
                        .addOnSuccessListener {
                            Log.i("firebase", "Got value ${it.value}")
                            names.add(it.value.toString())
                            val temp_pair = mapOf(child.getKey().toString() to child.value.toString())
                            pending_pairs.add(temp_pair)
                            notifyDataSetChanged()
                        }
                    //val user = h.getValue(String::class.java)
                    //names.add(user.toString())
                }
            }
        }

        override fun getCount(): Int{
            return names.size
        }

        override fun getItemId(position: Int): Long{
            return position.toLong()
        }

        override fun getItem(position: Int): Any{
            return "Test String"
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.row_accept_decline, viewGroup, false)

            val nametextView = rowMain.findViewById<TextView>(R.id.accept_decline_name)
            nametextView.text = names.get(position)


            val deleteBtn = rowMain.findViewById(R.id.decline) as Button
            val acceptBtn = rowMain.findViewById(R.id.accept) as Button

            deleteBtn.setOnClickListener {
                var uid: String? = null
                val database = Firebase.database.reference
                val user = Firebase.auth.currentUser
                user?.let {
                    uid = user.uid
                }
                names.removeAt(position)
                pending_pairs.removeAt(position)
                database.child("users/${uid}/pending").setValue(pending_pairs)
                    .addOnSuccessListener {
                        Log.d("firebase", "Deleted request")
                }
                notifyDataSetChanged()
            }

            acceptBtn.setOnClickListener {
                var uid: String? = null
                val database = Firebase.database.reference
                val user = Firebase.auth.currentUser
                user?.let {
                    uid = user.uid
                }
                val connect_id = pending_pairs[position].keys.first()
                names.removeAt(position)
                pending_pairs.removeAt(position)
                database.child("users/${uid}/pending").setValue(pending_pairs)
                    .addOnSuccessListener {
                        Log.d("firebase", "Deleted request")
                    }

                database.child("users/${uid}/connections").push().setValue(connect_id)
                    .addOnSuccessListener {
                        Log.d("firebase", "Pushed connection to database")
                    }

                database.child("users/${connect_id}/connections").push().setValue(uid)
                    .addOnSuccessListener {
                        Log.d("firebase", "Pushed connection to database")
                    }

                notifyDataSetChanged()
            }


            return rowMain
        }
    }
}
