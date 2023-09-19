package com.example.bankingnow.model

data class GetBalanceModel (
    val balance : Long,
    val user_id : String,
    val bank_name: String
)