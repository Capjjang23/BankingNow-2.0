package com.example.bankingnow.api

import com.example.bankingnow.model.PasswordCheckModel
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
    fun checkPassWord(@Body password: String): Call<PasswordCheckModel>
}