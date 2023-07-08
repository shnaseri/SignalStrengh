package com.shnaseri.strenghmap.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(fm: FragmentManager, private var count: Int) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int = count

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                DataFragment.instance!!
            }

            1 -> {
                TrackListFragment.instance!!
            }

            2 -> {
                TrackMapFragment()
            }
            else -> TrackMapFragment()
        }
    }

    companion object {
        private const val TAG = "mobilenetworkstracker"
    }
}
