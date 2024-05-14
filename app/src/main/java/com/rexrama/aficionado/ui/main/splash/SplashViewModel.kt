package com.rexrama.aficionado.ui.main.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.utils.UserPreference

class SplashViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}