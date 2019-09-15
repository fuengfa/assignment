package com.scb.mobilephone.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scb.mobilephone.*
import com.scb.mobilephone.ui.Activity.MobileDetailActivity
import com.scb.mobilephone.ui.Activity.OnSortClickListener
import com.scb.mobilephone.ui.adapter.FavoriteAdapter
import com.scb.mobilephone.ui.adapter.deleteFavorite
import com.scb.mobilephone.ui.callback.CustomItemTouchHelperCallback
import com.scb.mobilephone.ui.model.*

class FavoriteFragment: Fragment(),
    OnSortClickListener, deleteFavorite {
    
    private lateinit var recyclerViewMobileFavList: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var sortList: List<MobileModel>
    private var roomDatabase: AppDatbase? = null
    private var cmWorkerThread: CMWorkerThread = CMWorkerThread("favorite").also {
        it.start()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var _view = inflater.inflate(R.layout.fragment_favorite, container, false)
        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteAdapter = FavoriteAdapter(this)
        setRecyclerView(view)
        setSwipeToDelete()
        setRoomDatabase(view)
        loadFavoriteList()
    }

    private fun setSwipeToDelete() {
        val callback = CustomItemTouchHelperCallback(favoriteAdapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerViewMobileFavList)
    }

    private fun setRoomDatabase(view: View) {
        roomDatabase = AppDatbase.getInstance(view.context).also {
            it.openHelper.readableDatabase
        }
    }

    private fun setRecyclerView(view: View) {
        recyclerViewMobileFavList = view.findViewById(R.id.recyclerView)
        recyclerViewMobileFavList.let {
            it.adapter = favoriteAdapter
            it.layoutManager = LinearLayoutManager(view.context)
            it.itemAnimator = DefaultItemAnimator()
        }
    }

    private fun setFavoriteAdapter(list: List<MobileModel>) {
        sortList = list
        //because of Only the original thread that created a view hierarchy can touch its views
        activity?.runOnUiThread(object : Runnable {
            override fun run() {
                favoriteAdapter.submitList(sortList)
            }
        })
    }

    override fun showDetail(mobile: MobileModel, _view: View) {
        var intent = Intent(context, MobileDetailActivity::class.java)
        intent.putExtra("mobile", mobile)
        context!!.startActivity(intent)
    }

    override fun submitDataChange() {
        loadFavoriteList()
    }

    override fun OndeleteFavorite(id: Int) {
        val task = Runnable {
            roomDatabase?.mobileDao()?.deleteMobilebyID(id)
        }
        cmWorkerThread.postTask(task)
    }

    private fun loadFavoriteList() {
        val task = Runnable {
            var favList = roomDatabase?.mobileDao()?.queryMobiles()
            setFavoriteAdapter(mobileModelMapper(favList ?: listOf()))
        }
        cmWorkerThread.postTask(task)
    }

    private fun mobileModelMapper(entity: List<MobileEntity>): ArrayList<MobileModel> {
        val mobileModelList = arrayListOf<MobileModel>()
        entity.forEach {
            mobileModelList.add(it.transformToMobileModel())
        }
        return mobileModelList
    }

    override fun sortPriceLowToHeight() {
        setFavoriteAdapter(sortList.sortedBy { it.price })
    }

    override fun sortPriceHighToLow() {
        setFavoriteAdapter(sortList.sortedByDescending { it.price })
    }

    override fun sortRatingFromHighToLow() {
        setFavoriteAdapter(sortList.sortedByDescending { it.rating })
    }
}
