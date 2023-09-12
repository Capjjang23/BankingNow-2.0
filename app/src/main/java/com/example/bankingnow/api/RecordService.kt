package com.example.bankingnow.api

import com.example.bankingnow.model.GetBalanceModel
import com.example.bankingnow.model.PasswordCheckRequest
import com.example.bankingnow.model.PasswordCheckResponse
import com.example.writenow.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RecordService {
    @GET("/posts/1")
    fun getTest(): Call<TestGetModel>

    @POST("/posts")
    fun postTest(@Body postData: String): Call<TestPostModel>


    @POST("/accounts/check_password/")
    fun checkPassword(@Body request: PasswordCheckRequest): Call<PasswordCheckResponse>

    @GET("/money/check_balance/")
    fun getBalance():Call<GetBalanceModel>
}