package com.rexrama.aficionado.data.model

data class UserModel(
    val name: String,
    val email: String,
    val password: String,
    val isLogin: Boolean,
    val token: String
)