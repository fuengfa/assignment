package com.scb.mobilephone.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scb.mobilephone.*
import com.scb.mobilephone.ui.Activity.MobileDetailActivity
import com.scb.mobilephone.ui.Activity.OnSortClickListener
import com.scb.mobilephone.ui.Service.ApiManager
import com.scb.mobilephone.ui.adapter.MobileAdapter
import com.scb.mobilephone.ui.adapter.OnMobileClickListener
import com.scb.mobilephone.ui.model.*
import kotlinx.android.synthetic.main.fragment_mobile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileFragment: Fragment(),
    OnMobileClickListener, OnSortClickListener {
    override fun onDataChange() {
        loadSongs()
    }

    private lateinit var rvMobile: RecyclerView
    private lateinit var mobileAdapter: MobileAdapter
    private lateinit var sortList: List<MobileModel>
    private var roomDatabase: AppDatbase? = null
    private var cmWorkerThread: CMWorkerThread = CMWorkerThread("favorite").also {
        it.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_mobile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        rvMobile = view.findViewById(R.id.recyclerView)
        mobileAdapter = MobileAdapter(this)
        rvMobile.adapter = mobileAdapter
        rvMobile.layoutManager = LinearLayoutManager(context)
        rvMobile.itemAnimator = DefaultItemAnimator()
        roomDatabase = AppDatbase.getInstance(view.context).also {
            it.openHelper.readableDatabase
        }
        loadSongs()
        swipt_toRefresh.setOnRefreshListener {
            loadSongs()

        }
    }

    private val songListCallback = object : Callback<List<MobileModel>> {
        override fun onFailure(call: Call<List<MobileModel>>, t: Throwable) {
            println("conttext Showtoast")
            context?.showToast("Can not call country list $t")
        }

        override fun onResponse(call: Call<List<MobileModel>>, response: Response<List<MobileModel>>) {
            Handler().postDelayed({
                swipt_toRefresh.isRefreshing = false
            }, 3000)
            context?.showToast("Success")
            sortList = response.body()!!
            var favlist: MobileEntity?
                var task = Runnable {
                    for (list in sortList) {
                        favlist = roomDatabase?.mobileDao()!!.queryMobile(list.id)
                        if (favlist != null) {
                            list.fav = 1
                        }
                    }
                }
                cmWorkerThread.postTask(task)

//            because of Only the original thread that created a view hierarchy can touch its views
            activity?.runOnUiThread(object : Runnable{
                override fun run() {
                    setMobileAdapter(sortList)           }
            })

        }
    }

    override fun sortlowtoheight() {
        setMobileAdapter(sortList.sortedBy { it.price })
    }

    override fun sorthighttolow() {
        setMobileAdapter(sortList.sortedByDescending { it.price })
    }

    override fun sortrating() {
        setMobileAdapter(sortList.sortedByDescending { it.rating })
    }

    fun sortRating() {
        setMobileAdapter(sortList.sortedByDescending { it.rating })
    }

    override fun onHeartClick(mobile: MobileModel) {
        Log.d("plist", "print from func onHeartClick in main")

        var task = Runnable {
            if(mobile.fav == 1){
                roomDatabase?.mobileDao()!!.addMobile(MobileEntity(mobile.id,mobile.name, mobile.description, mobile.brand,
                    mobile.price, mobile.rating, mobile.thumbImageURL, mobile.fav))
            } else {
                roomDatabase?.mobileDao()!!.deleteMobilebyID(mobile.id)
            }
        }
        cmWorkerThread.postTask(task)
        mobileAdapter.submitList(sortList)

    }

    override fun onMobileClick(mobile: MobileModel, _view: View) {
        var intent = Intent(context, MobileDetailActivity::class.java)
        intent.putExtra("mobile", mobile)
        context!!.startActivity(intent)
    }


    private fun setMobileAdapter(list: List<MobileModel>) {
        sortList = list
        mobileAdapter.submitList(list)
    }

    private fun loadSongs() {
        ApiManager.mobileService.mobile().enqueue(songListCallback)
    }

}






