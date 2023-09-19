package com.example.bankingnow.api

import com.example.bankingnow.model.GetBalanceModel
import com.example.bankingnow.model.GetBankRequestModel
import com.example.bankingnow.model.GetBankResponseModel
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
    @GET("/posts/1")
    fun getTest(): Call<TestGetModel>

    @POST("/process_audio/")
    fun postNumber(@Body postData: RecordModel): Call<NumberModel>

    @POST("/accounts/check_password/")
    fun checkPassword(@Body request: PasswordCheckRequest): Call<PasswordCheckResponse>

    @GET("/money/check_balance/")
    fun getBalance():Call<GetBalanceModel>

    @POST("/post_bank/")
    fun getBank(@Body request: GetBankRequestModel):Call<GetBankResponseModel>
}