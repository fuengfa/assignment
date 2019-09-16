package com.scb.mobilephone.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scb.mobilephone.ui.adapter.SectionsPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.scb.mobilephone.R
import com.scb.mobilephone.ui.model.AppDatbase
import com.scb.mobilephone.ui.model.CMWorkerThread
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mCMWorkerThread: CMWorkerThread
    private var mDatabaseAdapter: AppDatbase? = null
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var alertDialog1: AlertDialog
    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialValue()
        createSectionPageAdapter()
        setTabListener()
        sortIt.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            var values = arrayOf<CharSequence>(" Price low to high ", " Price high to low ", " Rating 5-1 ")
            builder.setSingleChoiceItems(values, -1, DialogInterface.OnClickListener { dialog, item ->
                when (item) {
                    0 -> {
                        if (viewPager.currentItem == 0) {
                            sectionsPagerAdapter.mobileFragment.sortPriceLowToHeight()
                        } else if (viewPager.currentItem == 1) {
                            sectionsPagerAdapter.favoriteFragment.sortPriceLowToHeight()
                        }
                    }

                    1 -> {
                        if (viewPager.currentItem == 0) {
                            sectionsPagerAdapter.mobileFragment.sortPriceHighToLow()
                        } else if (viewPager.currentItem == 1) {
                            sectionsPagerAdapter.favoriteFragment.sortPriceHighToLow()
                        }
                    }

                    2 -> {
                        if (viewPager.currentItem == 0) {
                            sectionsPagerAdapter.mobileFragment.sortRating()
                        } else if (viewPager.currentItem == 1) {
                            sectionsPagerAdapter.favoriteFragment.sortRatingFromHighToLow()
                        }
                    }
                }
                alertDialog1.dismiss()
            })
            alertDialog1 = builder.create()
            alertDialog1.show()
        }
        setupDatabase()
        setupWorkerThread()
    }

    private fun setTabListener() {
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    sectionsPagerAdapter.mobileFragment.onDataChange()

                } else {
                    sectionsPagerAdapter.favoriteFragment.submitDataChange()
                }
            }
        })
    }

    private fun setupWorkerThread() {
        mCMWorkerThread = CMWorkerThread("scb_database").also {
            it.start()
        }
    }

    private fun setupDatabase() {
        mDatabaseAdapter = AppDatbase.getInstance(this).also {
            it.openHelper.readableDatabase
        }
    }

    private fun initialValue() {
        viewPager = findViewById(R.id.view_pager)
        tabs = findViewById(R.id.tabs)
    }


    private fun createSectionPageAdapter() {
        sectionsPagerAdapter = SectionsPagerAdapter(this@MainActivity, supportFragmentManager)
        viewPager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(viewPager)
    }
}

interface OnSortClickListener {
    fun sortPriceLowToHeight()
    fun sortPriceHighToLow()
    fun sortRatingFromHighToLow()
}

