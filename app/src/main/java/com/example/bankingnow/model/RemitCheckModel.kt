package com.example.bankingnow.model

data class RemitCheckModel (
    var money: String = "",
    var name: String = "",
    var user: UserRequestModel = UserRequestModel()
)