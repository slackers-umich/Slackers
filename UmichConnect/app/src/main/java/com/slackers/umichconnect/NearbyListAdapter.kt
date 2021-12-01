package com.slackers.umichconnect

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import coil.load
import com.slackers.umichconnect.databinding.ListitemNearbyBinding

class NearbyListAdapter(context: Context, users: ArrayList<NearbyListUser?>) :
    ArrayAdapter<NearbyListUser?>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.listitem_nearby, parent, false)
            rowView.tag = ListitemNearbyBinding.bind(rowView) // cache binding
            rowView.tag

//            val profileBtn = rowView.findViewById<Button>(R.id.profileButton)
//            profileBtn.setOnClickListener {
//                val intent = Intent(this.context, ViewProfile::class.java)
//                intent.putExtra("profile_id", uid)
//                startActivity(this.context, intent, null)
//            }
        }) as ListitemNearbyBinding

        getItem(position)?.run {
            val profileBtn = listItemView.profileButton
            profileBtn.setOnClickListener {
                val intent = Intent(context, ViewProfile::class.java)
                intent.putExtra("profile_id", uid)
                startActivity(context, intent, null)
            }
            listItemView.usernameView.text = username
            listItemView.root.setBackgroundColor(Color.parseColor(if (position % 2 == 0) "#E0E0E0" else "#EEEEEE"))
            // show image
            imageUrl?.let {
                listItemView.userImageView.setVisibility(View.VISIBLE)
                listItemView.userImageView.load(it) {
                    crossfade(true)
                    crossfade(1000)
                }
            } ?: run {
                listItemView.userImageView.setVisibility(View.GONE)
                listItemView.userImageView.setImageBitmap(null)
            }
        }

        return listItemView.root
    }
}