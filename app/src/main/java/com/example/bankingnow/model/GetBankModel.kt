package com.example.bankingnow.model

data class GetBankRequestModel(
    val voice_bank :String
)
data class GetBankResponseModel(
    val closest_bank :String
)