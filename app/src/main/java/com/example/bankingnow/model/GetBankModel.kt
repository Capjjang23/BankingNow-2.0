package com.example.bankingnow.model

data class GetBankRequestModel(
    val voiceBank :String
)
data class GetBankResponseModel(
    val closet_bank :String
)