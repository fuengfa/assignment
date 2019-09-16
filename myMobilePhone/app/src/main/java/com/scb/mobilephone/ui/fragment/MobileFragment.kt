package com.scb.mobilephone.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scb.mobilephone.*
import com.scb.mobilephone.ui.activity.MobileDetailActivity
import com.scb.mobilephone.ui.activity.OnSortClickListener
import com.scb.mobilephone.ui.adapter.MobileAdapter
import com.scb.mobilephone.ui.adapter.OnMobileClickListener
import com.scb.mobilephone.ui.model.*
import kotlinx.android.synthetic.main.fragment_mobile.*

class MobileFragment: Fragment(),
    OnMobileClickListener, OnSortClickListener, MobileFragmentPresenterInterface {

    override fun loadMobileList(mobileList: List<MobileModel>) {
        this.mobileList = mobileList
        runUiThread()
        swipeToDeleteStop()
    }

    override fun onDataChange() {
        presenter.loadSongs(viewFragment)
    }

    private lateinit var presenter: MobileFragmentPresenter
    private lateinit var recyclerViewMobile: RecyclerView
    private lateinit var mobileAdapter: MobileAdapter
    private lateinit var mobileList: List<MobileModel>
    private lateinit var viewFragment: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mobile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFragment = view
        mobileAdapter = MobileAdapter(this)
        presenter = MobileFragmentPresenter(this)
        setRecyclerView(view)
        presenter.loadSongs(view)
        swipe_toRefresh.setOnRefreshListener {
            presenter.loadSongs(view)
        }
    }

    private fun setRecyclerView(view: View) {
        recyclerViewMobile = view.findViewById(R.id.recyclerView)
        recyclerViewMobile.let {
            it.adapter = mobileAdapter
            it.layoutManager = LinearLayoutManager(context)
            it.itemAnimator = DefaultItemAnimator()
        }

    }

    fun runUiThread(){
        //because of Only the original thread that created a view hierarchy can touch its views
        activity?.runOnUiThread { setMobileAdapter(mobileList) }
    }

    private fun swipeToDeleteStop() {
            swipe_toRefresh.isRefreshing = false
    }

    override fun sortPriceLowToHeight() {
        setMobileAdapter(mobileList.sortedBy { it.price })
    }

    override fun sortPriceHighToLow() {
        setMobileAdapter(mobileList.sortedByDescending { it.price })
    }

    override fun sortRatingFromHighToLow() {
        setMobileAdapter(mobileList.sortedByDescending { it.rating })
    }

    fun sortRating() {
        setMobileAdapter(mobileList.sortedByDescending { it.rating })
    }

    override fun onHeartClick(mobile: MobileModel) {
        presenter.setHeartClick(mobile)
        setMobileAdapter(mobileList)
    }


    override fun onMobileClick(mobile: MobileModel, _view: View) {
        var intent = Intent(context, MobileDetailActivity::class.java).putExtra("mobile", mobile)
        context!!.startActivity(intent)
    }


    private fun setMobileAdapter(list: List<MobileModel>) {
        mobileList = list
        mobileAdapter.submitList(list)
    }
}

interface MobileFragmentPresenterInterface{
    fun loadMobileList(mobileList: List<MobileModel>)
}






