package com.rexrama.aficionado.utils

import android.content.Context
import android.content.Intent
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.ui.main.home.MainActivity
import com.rexrama.aficionado.ui.main.map.MapsActivity
import com.rexrama.aficionado.ui.main.profile.ProfileActivity
import com.rexrama.aficionado.ui.main.upload.UploadStoryActivity

class Util {
    fun toHome(context: Context) {
        val toHome = Intent(context, MainActivity::class.java)
        context.startActivity(toHome)
    }

    fun toUpload(context: Context) {
        val toUpload = Intent(context, UploadStoryActivity::class.java)
        context.startActivity(toUpload)
    }

    fun toLocation(context: Context) {
        val toLocation = Intent(context, MapsActivity::class.java)
        context.startActivity(toLocation)
    }

    fun toProfile(context: Context){
        val toProfile = Intent(context, ProfileActivity::class.java)
        context.startActivity(toProfile)
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: ListStoryItem)
    }

}