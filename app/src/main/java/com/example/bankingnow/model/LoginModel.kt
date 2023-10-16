package com.example.bankingnow.model

data class LoginRequestModel (
    val password: String
)

data class LoginResponseModel(
    val isLogin: Boolean
)