package com.scb.mobilephone.ui.fragment

import com.scb.mobilephone.ui.Service.ApiManager
import com.scb.mobilephone.ui.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileFragmentPresenter(val view: MobileFragmentPresenterInterface) {

    private var roomDatabase: AppDatbase? = null
    private var cmWorkerThread: CMWorkerThread = CMWorkerThread("favorite").also {
        it.start()
    }

    fun init(roomDatabase: AppDatbase?){
        this.roomDatabase = roomDatabase
        loadMobile()
    }

    private val songListCallback = object : Callback<List<MobileModel>> {
        override fun onFailure(call: Call<List<MobileModel>>, t: Throwable) {
        }

        override fun onResponse(call: Call<List<MobileModel>>, response: Response<List<MobileModel>>) {
            var mobileList = response.body()!!
            queryRoomDatabaseByMobileId(mobileList)
        }
    }

    private fun queryRoomDatabaseByMobileId(mobileList: List<MobileModel>){
        var task = Runnable {
            for (list in mobileList) {
                var favlist = roomDatabase?.mobileDao()!!.queryMobile(list.id)
                if (favlist != null) {
                    list.fav = 1
                }
            }
            view.loadMobileList(mobileList)
        }
        cmWorkerThread.postTask(task)
    }


    fun loadMobile() {
        ApiManager.mobileService.mobile().enqueue(songListCallback)
    }

    fun setHeartClick(mobile: MobileModel) {
        var task = Runnable {
            if (mobile.fav == 1) {
                roomDatabase?.mobileDao()!!.addMobile(createMobileEntity(mobile))
            } else {
                roomDatabase?.mobileDao()!!.deleteMobilebyID(mobile.id)
            }
        }
        cmWorkerThread.postTask(task)
    }

    private fun createMobileEntity(mobile: MobileModel): MobileEntity {
        return MobileEntity(
            mobile.id, mobile.name, mobile.description, mobile.brand,
            mobile.price, mobile.rating, mobile.thumbImageURL, mobile.fav
        )
    }
}