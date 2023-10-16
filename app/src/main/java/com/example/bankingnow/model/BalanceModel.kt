package com.example.bankingnow.model

data class BalanceRequestModel (
    val FinAcno: String
)

data class BalanceResponseModel (
    val Ldbl: String
)