package com.example.bankingnow.model

data class HeaderRequestModel (
    val Tsymd: String,
    val Trtm: String,
    val Iscd: String,
    val AccessToken: String
)

data class HeaderResponseModel (
    val Trtm: String,
    val Rsms: String,
    val ApiNm: String,
    val IsTuno: String,
    val Tsymd: String,
    val FintechApsno: String,
    val Iscd: String,
    val Rpcd: String,
    val ApiSvcCd: String
)