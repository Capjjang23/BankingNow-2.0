package com.example.bankingnow.model

import com.google.gson.annotations.SerializedName

data class UserRequestModel (
    @SerializedName("bank_name")
    var bank: String = "",
    @SerializedName("account_number")
    var account: String = ""
)

data class UserResponseModel (
    @SerializedName("user_id")
    val name: String
)