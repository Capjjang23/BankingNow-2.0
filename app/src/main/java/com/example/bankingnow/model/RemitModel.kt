package com.example.bankingnow.model

import com.example.bankingnow.MyApplication.Companion.prefs
import com.google.gson.annotations.SerializedName

data class RemitRequestModel (
    @SerializedName("FinAcno")
    var account: String = "",
    @SerializedName("Tram")
    var money: String = ""
)

data class RemitResponseModel (
    @SerializedName("isTransfer")
    val isRemit: Boolean
)