package com.rexrama.aficionado.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rexrama.aficionado.data.di.Injection
import com.rexrama.aficionado.ui.auth.register.RegisterViewModel
import com.rexrama.aficionado.ui.auth.signin.SignInViewModel
import com.rexrama.aficionado.ui.main.detail.DetailViewModel
import com.rexrama.aficionado.ui.main.home.MainViewModel
import com.rexrama.aficionado.ui.main.map.MapsViewModel
import com.rexrama.aficionado.ui.main.profile.ProfileViewModel
import com.rexrama.aficionado.ui.main.splash.SplashViewModel
import com.rexrama.aficionado.ui.main.upload.UploadStoryViewmodel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val pref: UserPreference,
    private val context: Context
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(DialogUtils(context)) as T
        } else if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(pref, DialogUtils(context)) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref, Injection.provideRepos()) as T
        } else if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(UploadStoryViewmodel::class.java)) {
            return UploadStoryViewmodel(pref, DialogUtils(context)) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(pref, Injection.provideRepos()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)

    }
}
