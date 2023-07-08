package com.shnaseri.strenghmap.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import javax.inject.Provider

class PagerAdapter(
    fm: FragmentManager,
    private var count: Int,
    private val dataFragment: DataFragment,
    private val trackListFragment: TrackListFragment,
    private val trackMapFragment: TrackMapFragment,
) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int = count

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                dataFragment
            }

            1 -> {
                trackListFragment
            }

            2 -> {
                trackMapFragment
            }
            else -> trackMapFragment
        }
    }

    companion object {
        private const val TAG = "mobilenetworkstracker"
    }
}
