package com.example.bankingnow.model

data class RemitModel (
    var money: String = "",
    var name: String = "",
    var user: UserRequestModel = UserRequestModel()
)