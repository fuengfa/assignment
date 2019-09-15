package com.scb.mobilephone.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.scb.mobilephone.ui.fragment.PicturesFragment
import com.scb.mobilephone.ui.model.Pictures

class PhotoPagerAdapter(fm: FragmentManager, var imgArray: List<Pictures>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return PicturesFragment(imgArray[position].url)
        }

    override fun getCount(): Int {
        return imgArray.count()
    }
}