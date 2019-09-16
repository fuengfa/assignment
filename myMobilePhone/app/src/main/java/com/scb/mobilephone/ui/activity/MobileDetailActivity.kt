package com.scb.mobilephone.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.scb.mobilephone.ui.model.Pictures
import com.scb.mobilephone.R
import com.scb.mobilephone.ui.adapter.PhotoPagerAdapter
import com.scb.mobilephone.ui.model.MobileModel


class MobileDetailActivity : AppCompatActivity() , MobileDetailInterface{

    private lateinit var mobile: MobileModel
    private lateinit var presenter: MobileDetailPresenter
    private lateinit var detailName: TextView
    private lateinit var detailDes: TextView
    private lateinit var detailprice: TextView
    private lateinit var detailRating: TextView
    private lateinit var imageSlider: ViewPager
    private lateinit var pictures: ArrayList<Pictures>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mobile_detail)
        showActionBar()
        onView()
        getAndShowModel()
    }

    private fun getAndShowModel() {
        mobile = intent.getParcelableExtra<MobileModel>("mobile") ?: return
        presenter.loadPictures(mobile)
    }

    override fun loadPictures(pictures: ArrayList<Pictures>) {
        detailName.text = mobile.name
        detailDes.text = mobile.description
        detailprice.text = "Price: ${mobile.price}"
        detailRating.text = "Rating: ${mobile.rating}"
        imageSlider.adapter = PhotoPagerAdapter(supportFragmentManager, pictures)
    }

    private fun onView() {
        presenter = MobileDetailPresenter(this)
        imageSlider = findViewById(R.id.viewpager)
        detailName = findViewById(R.id.detailName)
        detailRating = findViewById(R.id.detailRating)
        detailDes = findViewById(R.id.detailDes)
        detailprice = findViewById(R.id.detailPrice)
        pictures = ArrayList<Pictures>()
    }

    private fun showActionBar() {
        //actionbar
        val actionbar = supportActionBar
        //set back button
        actionbar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

interface MobileDetailInterface{
    fun loadPictures(pictures: ArrayList<Pictures>)
}