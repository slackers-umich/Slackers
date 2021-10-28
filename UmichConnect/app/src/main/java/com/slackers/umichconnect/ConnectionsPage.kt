package com.slackers.umichconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class ConnectionsPage : AppCompatActivity() {
    var array = arrayOf("Melbourne", "Vienna", "Vancouver", "Toronto", "Calgary", "Adelaide", "Perth", "Auckland", "Helsinki", "Hamburg", "Munich", "New York", "Sydney", "Paris", "Cape Town", "Barcelona", "London", "Bangkok")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connections_page)

        val adapter = ArrayAdapter(this,
            R.layout.activity_connections_page_listview_item, array)

        val listView: ListView = findViewById(R.id.listview_1)
        listView.setAdapter(adapter)

    }
}