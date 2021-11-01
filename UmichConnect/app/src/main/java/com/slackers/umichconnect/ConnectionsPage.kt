package com.slackers.umichconnect

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textview.MaterialTextView

class ConnectionsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connections_page)

        supportActionBar!!.title = "Chat With Your Connections"

        val values = arrayOf("Item Ome", "Item Two", "Item Three", "Item Four", "Item Five")
        val mListView = findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values)
        mListView.adapter = adapter
        mListView.setOnItemClickListener{parent, view, position, id ->
            startActivity(Intent(this, MainActivity::class.java))
//                if (position == 0) {
//                    Toast.makeText(this@ConnectionsPage, "Item One", Toast.LENGTH_SHORT).show()
//                }
        }

    }
}