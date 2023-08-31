package com.example.bankingnow.api

import com.example.writenow.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RecordService {
    @GET("/posts/1")
    fun getTest(): Call<TestGetModel>

    @POST("/posts")
    fun postTest(@Body postData: String): Call<TestPostModel>
}