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
    //var array = arrayOf("Melbourne", "Vienna", "Vancouver", "Toronto", "Calgary", "Adelaide", "Perth", "Auckland", "Helsinki", "Hamburg", "Munich", "New York", "Sydney", "Paris", "Cape Town", "Barcelona", "London", "Bangkok")
    //private lateinit var bn: com.google.android.material.bottomnavigation.BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_decline)


//        val adapter = ArrayAdapter(this,
//            R.layout.activity_accept_decline_listview_item, array)
//
        val listView: ListView = findViewById(R.id.accept_decline_listview)
        listView.adapter = MyCustomAdapter(this)

//        listView.setAdapter(adapter)



    }

    private class MyCustomAdapter(context: Context): BaseAdapter(){
        private val mContext: Context

        private val names = arrayListOf<String>(
            "TEST"
        )

        private val connections = arrayListOf<String>()

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
                for (child in it.getChildren()){
                    database.child("users").child(child.getKey().toString()).child("name").get().addOnSuccessListener {
                        Log.i("firebase", "Got value ${it.value}")
                        names.add(it.value.toString())
                    }
                    //val user = h.getValue(String::class.java)
                    //names.add(user.toString())
                }
            }



//            var mAuth = FirebaseAuth.getInstance()
//            var mDbRef = FirebaseDatabase.getInstance().getReference()
//
//            var userList = ArrayList<User>()
//            var adapter = UserAdapter(context, userList)
//            mDbRef.child("user").addValueEventListener(object: ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (firstTime) {
//                        userList.clear()
//                        //userList.add(User("good", "good", "good", listOf<String>("x1","y1")))
//                        var thisUserPendingNames = listOf<String?>()
//                        var thisConnections = listOf<String?>()
//
//                        for (postSnapshot in snapshot.children) {
//                            postSnapshot.child("pending")
//                            val currentUser = postSnapshot.getValue(User::class.java)
//                            if (mAuth.currentUser?.uid == currentUser?.uid) { // User should not be displayed in list of chats
//                                currentUser!!.child("pending")
//                                thisUserPendingNames = currentUser!!.pendingName
//                                //thisConnections = currentUser!!.connections
//                            }
//
//                        }
//                        for (postSnapshot in snapshot.children) {
//                            val currentUser = postSnapshot.getValue(User::class.java)
//                            if (currentUser?.name in thisUserPendingNames) {
//                                userList.add(currentUser!!)
//                                names.add(currentUser!!.name.toString())
//                            }
//                            if (currentUser?.uid in thisConnections) {
//                                connections.add(currentUser!!.uid.toString())
//                            }
//                        }
//                        adapter.notifyDataSetChanged()
//                        firstTime = false
//                    }}
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//
//            })
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
                var database = Firebase.database.reference
                val user = Firebase.auth.currentUser
                user?.let {
                    uid = user.uid
                }


                names.removeAt(position)
                database.child("users/" + uid + "/pending").setValue(names)


                notifyDataSetChanged()
//
//
//
//
//
//
//
//                var mAuth = FirebaseAuth.getInstance()
//                var mDbRef = FirebaseDatabase.getInstance().getReference()
//                var userList = ArrayList<User>()
//                var adapter = UserAdapter(mContext, userList)
//
//                val temp = mapOf<String,List<String>>(
//                    Pair("pendingName", names)
//                )
//                FirebaseDatabase.getInstance().getReference("user").child(mAuth.currentUser?.uid.toString()).updateChildren(temp)
            }

            acceptBtn.setOnClickListener {

                var uid: String? = null
                var database = Firebase.database.reference
                val user = Firebase.auth.currentUser
                user?.let {
                    uid = user.uid
                }


                names.removeAt(position)
                database.child("users/" + uid + "/pending").setValue(names)


                notifyDataSetChanged()


//                var connections : MutableList<String> = ArrayList()
//
//                var uid: String? = null
//                var database = Firebase.database.reference
//                val user = Firebase.auth.currentUser
//                user?.let {
//                    uid = user.uid
//                }
//
//                database.child("users/"+uid+"/connections").get().addOnSuccessListener {
//                    for (h in it.children){
//                        val user = h.getValue(String::class.java)
//                        connections.add(user.toString())
//                    }
//                    connections.add(names[position])
//                }
//                database.child("users/" + uid + "/connections").setValue(connections)
//
//                names.removeAt(position)
//                database.child("users/" + uid + "/pending").setValue(names)
//
//
//                notifyDataSetChanged()


//                var userWhoWasAccepted = names[position]
//
//                names.removeAt(position)
//                connections.add(userWhoWasAccepted)
//                notifyDataSetChanged()
//
//                var mAuth = FirebaseAuth.getInstance()
//                var mDbRef = FirebaseDatabase.getInstance().getReference()
//                var userList = ArrayList<User>()
//                var adapter = UserAdapter(mContext, userList)
//
//                val temp = mapOf<String,List<String>>(
//                    Pair("pendingName", names)
//                )
//                FirebaseDatabase.getInstance().getReference("user").child(mAuth.currentUser?.uid.toString()).updateChildren(temp)
////                 FirebaseDatabase.getInstance().getReference("user").va//child(mAuth.currentUser?.uid.toString()).child("connections").get().value
//
//                val tempConnections = mapOf<String, List<String>>(
//                    Pair("connections", connections)
//                )
//
//                FirebaseDatabase.getInstance().getReference("user").child(mAuth.currentUser?.uid.toString()).updateChildren(tempConnections)

            }


            return rowMain
        }
    }
}
