package com.rexrama.aficionado.ui.main.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rexrama.aficionado.utils.UserPreference
import kotlinx.coroutines.launch

class DetailViewModel(private val pref: UserPreference) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}