package com.shnaseri.strenghmap.presentation

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.shnaseri.strenghmap.R
import com.shnaseri.strenghmap.controller.TrackingManager
import com.shnaseri.strenghmap.db.AppDatabase
import com.shnaseri.strenghmap.loaders.TrackLoader
import com.shnaseri.strenghmap.model.Track
import javax.inject.Inject

class TrackListFragment @Inject constructor() :
    ListFragment(),
    LoaderManager.LoaderCallbacks<List<*>?> {
    private var mCallbacks: Callbacks? = null
    private var mTrackId: Long = 0

    @Inject
    lateinit var mTrackingManager: TrackingManager

    @Inject
    lateinit var mDatabaseManager: AppDatabase
    private val mTracks: ArrayList<Track>? = null

    /*
     *  Loader callbacks
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<*>?> {
//        Log.d(TAG, "onCreateLoader() called");
        return TrackLoader(activity?.applicationContext!!, mDatabaseManager)
    }

    override fun onLoadFinished(loader: Loader<List<*>?>, data: List<*>?) {
//        Log.d(TAG, "onLoadFinished() called");
        val trackList: List<Track> = data as List<Track>
        //        Log.d(TAG, "onLoadFinished: Track list size = " + trackList.size());
//        Log.d(TAG, "onLoadFinished: Track list tracks ");
        for (i in trackList.indices) {
            Log.d(
                TAG,
                (
                    "Track ID: " + trackList[i].id
                    ) + " date " + trackList[i].startDate.toString(),
            )
        }
        val adapter = TrackListAdapter(activity!!.applicationContext, trackList)
        listAdapter = adapter
    }

    override fun onLoaderReset(loader: Loader<List<*>?>) {
        listAdapter = null
    }

    /*
     * Interface for host activity
     */
    interface Callbacks {
        // Pass track ID to MapFragment
        fun onTrackSelected(trackId: Long)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sTrackListFragment = this
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        refreshTrackList()
        registerForContextMenu(listView)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        mCallbacks!!.onTrackSelected(id)
    }

    /*
     *   Context menu methods
     */
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        activity?.menuInflater?.inflate(R.menu.menu_item_delete_track, menu)
        // Get long-pressed item id
        val info = menuInfo as AdapterContextMenuInfo
        mTrackId = info.id
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Get menu item position
        when (item.itemId) {
            R.id.menu_item_delete_track -> {
                refreshTrackList()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun refreshTrackList() {
        loaderManager.restartLoader(0, null, this)
    }

    companion object {
        private val TAG = "mobilenetworkstracker"
        var sTrackListFragment: TrackListFragment? = null
        val instance: TrackListFragment?
            get() {
                if (sTrackListFragment == null) {
                    sTrackListFragment = TrackListFragment()
                }
                return sTrackListFragment
            }
    }
}
