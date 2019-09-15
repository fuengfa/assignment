package com.scb.mobilephone.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.scb.mobilephone.R
import kotlinx.android.synthetic.main.fragment_pictures.*


class PicturesFragment(var imageUrl: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        picInViewpager.let {
                if(imageUrl.contains("http",true)){

                }else{
                    imageUrl = "https://${imageUrl}"
                }

            Glide.with(this).load(imageUrl).into(it)
        }
    }
}
