package com.example.bankingnow.apiManager

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bankingnow.api.RecordService
import com.example.bankingnow.model.PasswordCheckModel
import com.example.writenow.model.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class RecordApiManager {
    private var retrofit: Retrofit? = null
    private var retrofitService: RecordService? = null
    val _resultLivedata: MutableLiveData<String> = MutableLiveData()
    val resultLivedata: LiveData<String>
        get() = _resultLivedata

    companion object {  // DCL 적용한 싱글톤 구현
        var instance: RecordApiManager? = null
        fun getInstance(context: Context?): RecordApiManager? {
            if (instance == null) {
                @Synchronized
                if (instance == null)
                    instance = RecordApiManager()
            }
            return instance
        }
    }

    init {
        // http://192.168.47.145:8000
        // https://jsonplaceholder.typicode.com
        retrofit = Retrofit.Builder()
            .baseUrl("http://223.194.128.21:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitService = retrofit?.create(RecordService::class.java)
    }

    fun postTest(postData: String){
        val resultData: Call<TestPostModel>? = retrofitService?.postTest(postData)
        resultData?.enqueue(object : Callback<TestPostModel> {
            override fun onResponse(
                call: Call<TestPostModel>,
                response: Response<TestPostModel>
            ) {
                if (response.isSuccessful) {
                    val result: TestPostModel = response.body()!!
                    Log.d("resultt", result.toString())
                    //EventBus.getDefault().post(GetDataEvent(resultData))
                } else {
                    //EventBus.getDefault().post(GetDataEvent(null))
                    Log.d("resultt", "실패코드_${response.code()}")
                }
            }

            override fun onFailure(call: Call<TestPostModel>, t: Throwable) {
                t.printStackTrace()
                //EventBus.getDefault().post(GetDataEvent(null))
                Log.d("resultt","통신 실패")
            }
        })
    }

    fun getTest(){
        val resultData: Call<TestGetModel>? = retrofitService?.getTest()
        resultData?.enqueue(object : Callback<TestGetModel> {
            override fun onResponse(
                call: Call<TestGetModel>,
                response: Response<TestGetModel>
            ) {
                if (response.isSuccessful) {
                    val result: TestGetModel = response.body()!!
                    Log.d("resultt", result.toString())
                    //EventBus.getDefault().post(GetDataEvent(resultData))
                } else {
                    //EventBus.getDefault().post(GetDataEvent(null))
                    Log.d("resultt", "실패")
                }
            }

            override fun onFailure(call: Call<TestGetModel>, t: Throwable) {
                t.printStackTrace()
                //EventBus.getDefault().post(GetDataEvent(null))
                Log.d("resultt","통신 실패")
            }
        })
    }

    fun checkPW(password: String){
        val resultData: Call<PasswordCheckModel>? = retrofitService?.checkPassWord(password)
        resultData?.enqueue(object : Callback<PasswordCheckModel> {
            override fun onResponse(
                call: Call<PasswordCheckModel>,
                response: Response<PasswordCheckModel>
            ) {
                if (response.isSuccessful) {
                    val result: PasswordCheckModel = response.body()!!
                    Log.d("password check", result.toString())
                } else {
                    Log.d("password check", "실패")
                }
            }

            override fun onFailure(call: Call<PasswordCheckModel>, t: Throwable) {
                t.printStackTrace()
                //EventBus.getDefault().post(GetDataEvent(null))
                Log.d("password check","통신 실패")
            }
        })
    }
}