package com.shnaseri.strenghmap.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.shnaseri.strenghmap.R
import com.shnaseri.strenghmap.model.Track

class TrackListAdapter(private val mContext: Context, list: List<Track>) :
    BaseAdapter() {
    private val mTrackList: List<Track>

    init {
        mTrackList = list
        mLayoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return mTrackList.size
    }

    override fun getItem(position: Int): Any {
        return mTrackList[position]
    }

    override fun getItemId(position: Int): Long {
        return mTrackList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = mLayoutInflater!!.inflate(R.layout.list_item_track, null)
        }
        val track: Track = getItem(position) as Track
        val titleTextView =
            convertView.findViewById<View>(R.id.track_list_item_idTextView) as TextView
        titleTextView.text = "Track ID : " + track.id
        val dateTextView =
            convertView.findViewById<View>(R.id.track_list_item_dateTextView) as TextView
        dateTextView.text = "Date: " + track.startDate
        val checkbox =
            convertView.findViewById<View>(R.id.track_list_item_uploadedCheckBox) as CheckBox
        checkbox.isChecked = false
        return convertView
    }

    companion object {
        private var mLayoutInflater: LayoutInflater? = null
    }
}