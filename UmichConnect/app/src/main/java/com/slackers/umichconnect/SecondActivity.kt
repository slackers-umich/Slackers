package com.slackers.umichconnect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import java.text.FieldPosition

class SecondActivity: AppCompatActivity() {
    //var array = arrayOf("Melbourne", "Vienna", "Vancouver", "Toronto", "Calgary", "Adelaide", "Perth", "Auckland", "Helsinki", "Hamburg", "Munich", "New York", "Sydney", "Paris", "Cape Town", "Barcelona", "London", "Bangkok")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

//        val adapter = ArrayAdapter(this,
//            R.layout.activity_accept_decline_listview_item, array)
//
        val listView: ListView = findViewById(R.id.listview_2)
        listView.adapter = MyCustomAdapter(this)
//        listView.setAdapter(adapter)

    }

    private class MyCustomAdapter(context: Context): BaseAdapter(){
        private val mContext: Context

        init{
            mContext = context
        }

        override fun getCount(): Int{
            return 5
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
            return rowMain
//            val textView = TextView(mContext)
//            textView.text = "HERE is my row"
//            return textView
        }
    }
}