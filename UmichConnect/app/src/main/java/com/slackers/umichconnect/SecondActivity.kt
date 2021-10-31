package com.slackers.umichconnect

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

        private val names = arrayListOf<String>(
            "Beth", "Ryan", "Mike", "Sanjit", "Carlos", "Mary", "Jaden", "Sora", "Doug", "Jack", "Maya", "Sam", "Henry", "George"
        )

        init{
            mContext = context
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

            val nametextView = rowMain.findViewById<TextView>(R.id.textView)
            nametextView.text = names.get(position)


            val deleteBtn = rowMain.findViewById(R.id.decline) as Button
            val acceptBtn = rowMain.findViewById(R.id.accept) as Button

            deleteBtn.setOnClickListener {
                names.removeAt(position)
                notifyDataSetChanged()
            }

            acceptBtn.setOnClickListener {
                names.removeAt(position)
                notifyDataSetChanged()
            }


            return rowMain
        }
    }
}