package com.example.bankingnow.api

import com.example.bankingnow.model.NumberModel
import com.example.bankingnow.model.PasswordCheckRequest
import com.example.bankingnow.model.PasswordCheckResponse
import com.example.bankingnow.model.PostTestModel
import com.example.bankingnow.model.RecordModel
import com.example.bankingnow.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RecordService {
    @POST("/server")
    fun postTest(): Call<PostTestModel>

    @POST("/balance")
    fun postBalance(@Body request: BalanceRequestModel): Call<BalanceResponseModel>

    @POST("/process_audio/")
    fun postNumber(@Body postData: RecordModel): Call<NumberModel>

    @POST("/accounts/check_password/")
    fun checkPassword(@Body request: PasswordCheckRequest): Call<PasswordCheckResponse>

    @POST("/draw-transfer")
    fun tryRemit(@Body request: RemitRequestModel):Call<RemitResponseModel>
}