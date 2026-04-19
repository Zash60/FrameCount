package com.xath06.framecount

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.xath06.framecount.databinding.ActivityMainBinding
import com.xath06.framecount.ui.ResultsFragment
import com.xath06.framecount.ui.SegmentsFragment
import com.xath06.framecount.ui.SettingsFragment
import com.xath06.framecount.ui.TimingDetailsFragment
import com.xath06.framecount.ui.VideoInputFragment
import com.xath06.framecount.viewmodel.FrameCountViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val viewModel: FrameCountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupTabs()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, VideoInputFragment())
                .commit()
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragment = when (tab?.position) {
                    0 -> VideoInputFragment()
                    1 -> TimingDetailsFragment()
                    2 -> ResultsFragment()
                    3 -> SegmentsFragment()
                    4 -> SettingsFragment()
                    else -> return
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                binding.tabLayout.getTabAt(4)?.select()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun navigateToTab(tabIndex: Int) {
        binding.tabLayout.getTabAt(tabIndex)?.select()
    }
}