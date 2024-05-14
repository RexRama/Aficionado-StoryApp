package com.rexrama.aficionado.data.local.repo

import com.rexrama.aficionado.data.remote.api.ApiService


class StoryRepo(private val apiService: ApiService) {

    private var token = ""

    fun setToken(token: String) {
        this.token = token
    }

}