package com.shnaseri.strenghmap

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.shnaseri.strenghmap.controller.LocationReceiver
import com.shnaseri.strenghmap.controller.PositioningManager
import com.shnaseri.strenghmap.controller.TrackingManager
import com.shnaseri.strenghmap.databinding.ActivityMainBinding
import com.shnaseri.strenghmap.presentation.DataFragment
import com.shnaseri.strenghmap.presentation.PagerAdapter
import com.shnaseri.strenghmap.presentation.TrackListFragment
import com.shnaseri.strenghmap.presentation.TrackMapFragment
import com.shnaseri.strenghmap.telephony.CustomPhoneStateListener
import com.shnaseri.strenghmap.telephony.TelephonyInfo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "mobilenetworkstracker"
    private lateinit var mViewPager: ViewPager
    private var mMenu: Menu? = null

    @Inject
    lateinit var mTelephonyInfo: TelephonyInfo

    @Inject
    lateinit var mTrackingManager: TrackingManager

    @Inject
    lateinit var mPositioningManager: PositioningManager

    @Inject
    lateinit var mCustomPhoneStateListener: CustomPhoneStateListener

    @Inject
    lateinit var mDataFragment: DataFragment

    @Inject
    lateinit var mTrackMapFragment: TrackMapFragment

    @Inject
    lateinit var mTrackListFragment: TrackListFragment

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        toolbar.setLogo(R.drawable.bs4)
        toolbar.logoDescription = "logo description here"
        val tabLayout: TabLayout = binding.tabLayout
        tabLayout.tabTextColors =
            ContextCompat.getColorStateList(applicationContext, R.color.theme_window_background)

        tabLayout.addTab(tabLayout.newTab().setText(resources.getText(R.string.tab_data)))
        tabLayout.addTab(tabLayout.newTab().setText(resources.getText(R.string.tab_series)))
        tabLayout.addTab(tabLayout.newTab().setText(resources.getText(R.string.tab_map)))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        //
        //  Setup ViewPager
        //
        mViewPager = binding.pager
        val adapter = PagerAdapter(
            supportFragmentManager,
            tabLayout.tabCount,
            mDataFragment,
            mTrackListFragment,
            mTrackMapFragment,
        )
        mViewPager.adapter = adapter
        mViewPager.addOnPageChangeListener(object :
            TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Refresh network information when DataFragment is shown
                if (position == 0) {
                    mDataFragment.updateUI()
                }
            }
        })
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                mViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        // Start location updates on program first start if not started yet
        if (!mPositioningManager.isLocationUpdatesEnabled) {
            mPositioningManager.startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        // stop tracking if isTrackingOn == false
        if (!mTrackingManager.isTrackingOn) {
            mPositioningManager.stopLocationUpdates()
            handleNotification(LocationReceiver.ACTION_STOP_NOTIFICATION)
        }
    }

    /**
     * Create menu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.track_list_options, menu)
        if (mTrackingManager.isTrackingOn) {
            mMenu!!.getItem(0).setIcon(R.drawable.menu_item_location_off_selector)
        }
        return true
    }

    /**
     * Menu item selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_item_new_track) {
            if (!mTrackingManager.isTrackingOn) {
                // Insert record of new track into database
                mTrackingManager.startTracking()
                // Start notification of tracking
                handleNotification(LocationReceiver.ACTION_START_NOTIFICATION)
                // Change icon
                mMenu!!.getItem(0).setIcon(R.drawable.menu_item_location_off_selector)
            } else if (mTrackingManager.isTrackingOn) {
                mTrackingManager.stopTracking()
                // Stop notification
                handleNotification(LocationReceiver.ACTION_STOP_NOTIFICATION)
                // Change icon
                mMenu!!.getItem(0).setIcon(R.drawable.menu_item_location_on_selector)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Interface method TrackListFragment.Callbacks
     * Passes track ID to MapFragment in order to show selected track on map.
     * Switching to map fragment
     * @param trackId
     */
    fun onTrackSelected(trackId: Long) {
        // Switch to tab with Map
        mViewPager!!.currentItem = 2
        val fragmentManager: FragmentManager = supportFragmentManager
        // find currently shown fragment by tag
        val fragment: Fragment? =
            fragmentManager.findFragmentByTag("android:switcher:" + R.id.pager + ":" + mViewPager!!.currentItem)
        (fragment as TrackMapFragment).onTrackSelected(trackId)
    }

    /**
     * Sends broadcast message to stop notifications
     */
    private fun handleNotification(action: String) {
        val i = Intent()
        i.action = action
        sendBroadcast(i)
    }
}
