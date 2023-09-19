package com.example.bankingnow.model

data class RemitRequestModel(
    val account_bank_to : String,
    val account_no_to: String,
    val amount:String,
    val money: Int,
    val user_to:Int
)

data class RemitResponseModel(
    val result_msg:String
)