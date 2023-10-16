package com.example.bankingnow.api

import com.example.bankingnow.model.NumberModel
import com.example.bankingnow.model.PostTestModel
import com.example.bankingnow.model.RecordModel
import com.example.bankingnow.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RecordService {
    @POST("/server")
    fun postTest(): Call<PostTestModel>

    @POST("/balance")
    fun postBalance(@Body request: BalanceRequestModel): Call<BalanceResponseModel>

    @POST("/process_audio/")
    fun postNumber(@Body postData: RecordModel): Call<NumberModel>

    @POST("/login")
    fun loginService(@Body request: LoginRequestModel): Call<LoginResponseModel>

    @POST("/draw-transfer")
    fun remitService(@Body request: RemitRequestModel):Call<RemitResponseModel>
}