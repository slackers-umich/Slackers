package com.slackers.umichconnect


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ConnectionsActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var bn: com.google.android.material.bottomnavigation.BottomNavigationView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var uid: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connections)
        var database = Firebase.database.reference

        if (Firebase.auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        mAuth = FirebaseAuth.getInstance()
        mDbRef = Firebase.database.getReference("users")
        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

//        var connections: MutableList<String> = ArrayList()
//
//        val user = Firebase.auth.currentUser
//        user?.let {
//            uid = user.uid
//        }
//        database.child("user/" +uid + "/connections").get().addOnSuccessListener {
//            for (h in it.children){
//                val user = h//.getValue(String::class.java)
//                userList.add(user)
//            }
//        }

        bn = findViewById(R.id.bottom_navigation)
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, NearbyActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_2 -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_3 -> {
                    val intent = Intent(this, AcceptDeclineActivity::class.java)
                    startActivity(intent)
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

        mDbRef.child("${mAuth.currentUser!!.uid}/connections").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.children.forEach() {
                        val uid = it.value.toString()
                        mDbRef.child(uid).get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val currentUser = it.result.getValue(User::class.java)
                                    if (currentUser != null) {
                                        currentUser.userId = uid
                                    }
                                    userList.add(currentUser!!)
                                    adapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
                else {
                    Log.e("ConnectionsActivity", "Error getting connections from firebase")
                }
            }

        val intent = Intent(this, LocationUpdateService::class.java)
        startService(intent)

//        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear()
//                //userList.add(User("good", "good", "good", listOf<String>("x1","y1")))
//                var thisUserConnections = listOf<String?>()
//                for (postSnapshot in snapshot.children) {
//                    val currentUser = postSnapshot.getValue(User::class.java)
//                    if (mAuth.currentUser?.uid == currentUser?.uid) { // User should not be displayed in list of chats
//                        thisUserConnections = currentUser!!.connections
//                    }
////                    if (mAuth.currentUser?.uid != currentUser?.uid) { // User should not be displayed in list of chats
////                        userList.add(currentUser!!)
////                    }
//                }
//                for (postSnapshot in snapshot.children) {
//                    val currentUser = postSnapshot.getValue(User::class.java)
//                    if (currentUser?.uid in thisUserConnections) {
//                        userList.add(currentUser!!)
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) { // If logout is clicked
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }
}