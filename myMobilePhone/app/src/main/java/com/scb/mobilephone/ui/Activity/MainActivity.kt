package com.scb.mobilephone.ui.Activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.scb.mobilephone.ui.main.SectionsPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.scb.mobilephone.R
import com.scb.mobilephone.ui.fragment.FavoriteFragment
import com.scb.mobilephone.ui.fragment.MobileFragment
import com.scb.mobilephone.ui.model.AppDatbase
import com.scb.mobilephone.ui.model.CMWorkerThread
import com.scb.mobilephone.ui.model.MobileModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

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
        createSectionPageAgapter()
        sortIt.setOnClickListener {
            var builder = AlertDialog.Builder(this)
            var values = arrayOf<CharSequence>(" Price low to high ", " Price high to low ", " Rating 5-1 ")
            builder.setSingleChoiceItems(values, -1, DialogInterface.OnClickListener { dialog, item ->
                when (item) {
                    0 -> {
                        if (viewPager.currentItem == 0) {
                            sectionsPagerAdapter.mobileFragment.sortlowtoheight()
                        }else if (viewPager.currentItem == 1){
                            sectionsPagerAdapter.favoriteFragment.sortlowtoheight()
                        }
                    }

                    1 -> {
                        if (viewPager.currentItem == 0) {
                            sectionsPagerAdapter.mobileFragment.sorthighttolow()
                        }else if (viewPager.currentItem == 1){
                            sectionsPagerAdapter.favoriteFragment.sorthighttolow()
                        }
                    }

                    2 -> {
                        if (viewPager.currentItem == 0) {
                            sectionsPagerAdapter.mobileFragment.sortRating()
                        }else if (viewPager.currentItem == 1){
                            sectionsPagerAdapter.favoriteFragment.sortrating()
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

    fun initialValue() {
//        alertDialog1 = presenter.creatDialog(this)
        viewPager = findViewById(R.id.view_pager)
        tabs = findViewById(R.id.tabs)
    }


    fun createSectionPageAgapter() {
        sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = sectionsPagerAdapter
//        viewPager.addOnPageChangeListener()
        tabs.setupWithViewPager(viewPager)
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    sectionsPagerAdapter.mobileFragment.onDataChange()

                }else{
                    println("11111")
                    sectionsPagerAdapter.favoriteFragment.submitDataChange()

                }
            }

        })
    }
}

interface OnSortClickListener {
    fun sortlowtoheight()
    fun sorthighttolow()
    fun sortrating()
}


