package com.example.bankingnow.apiManager

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bankingnow.MyApplication.Companion.prefs
import com.example.bankingnow.api.RecordService
import com.example.bankingnow.event.LoginEvent
import com.example.bankingnow.event.NumberPrivateEvent
import com.example.bankingnow.event.NumberPublicEvent
import com.example.bankingnow.event.RemitEvent
import com.example.bankingnow.model.PasswordCheckResponse
import com.example.bankingnow.model.RecordModel
import com.example.bankingnow.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class RecordApiManager {
    private var retrofit: Retrofit? = null
    private var retrofitService: RecordService? = null
    val _resultLivedata: MutableLiveData<String> = MutableLiveData("0")
    val resultLivedata: LiveData<String>
        get() = _resultLivedata

    var listener: postMyBalance? = null


    // 클래스내 인터페이스 작성
    interface postMyBalance {
        fun postBalance(balanceModel: BalanceResponseModel)
    }

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

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
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val requestBuilder = request.newBuilder()

                // 여기에서 로그인 토큰을 가져와서 추가
                val loginToken = prefs.getString("login", "") // 로그인 토큰을 얻는 메서드
                if (!loginToken.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $loginToken")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://9688254484.for-seoul.synctreengine.com")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitService = retrofit?.create(RecordService::class.java)
    }

    fun updateTokenInRetrofit(newToken: String) {
        val newOkHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val requestBuilder = request.newBuilder()

                // 새로운 토큰을 추가
                requestBuilder.addHeader("Authorization", "Bearer $newToken")

                // 다른 헤더도 필요하면 추가 가능

                chain.proceed(requestBuilder.build())
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://your_base_url_here/")
            .client(newOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofitService = retrofit?.create(RecordService::class.java)
    }

    fun postTest() {
        val resultData: Call<PostTestModel>? = retrofitService?.postTest()
        resultData?.enqueue(object : Callback<PostTestModel> {
            override fun onResponse(
                call: Call<PostTestModel>,
                response: Response<PostTestModel>
            ) {
                if (response.isSuccessful) {
                    val result: PostTestModel = response.body()!!
                    Log.d("TestResultt", result.toString())
                } else {
                    Log.d("TestResultt", "fail")
                }
            }

            override fun onFailure(call: Call<PostTestModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("TestResultt", "통신 실패")
            }
        })
    }

    fun postBalance(finAcno: String){
        val requestData = BalanceRequestModel(finAcno)
        val resultData: Call<BalanceResponseModel>? = retrofitService?.postBalance(requestData)
        resultData?.enqueue(object : Callback<BalanceResponseModel> {
            override fun onResponse(
                call: Call<BalanceResponseModel>,
                response: Response<BalanceResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result: BalanceResponseModel = response.body()!!
                    listener?.postBalance(result)
                    Log.d("postBalance", result.toString())
                } else {
                    Log.d("postBalance", "실패 코드: "+response.code())
                }
            }

            override fun onFailure(call: Call<BalanceResponseModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("postBalance", "통신 실패")
            }
        })
    }

    fun toLoginService(password: String) {
        val request = LoginRequestModel(password)
        val resultData: Call<LoginResponseModel>? = retrofitService?.loginService(request)
        resultData?.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: Response<LoginResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result: LoginResponseModel = response.body()!!
                    Log.d("password check", result.toString())
                    updateTokenInRetrofit("")
                    EventBus.getDefault().post(LoginEvent(true, result))
                } else {
                    Log.d("password check", "실패")
                    Log.d("response:", response.toString())
                    EventBus.getDefault().post(LoginEvent(false, LoginResponseModel(false)))
                }
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                t.printStackTrace()
                EventBus.getDefault().post(LoginEvent(false, LoginResponseModel(false)))
                Log.d("password check", "통신 실패")
            }
        })
    }

    // 송금 금액, 계좌 번호 받아올때  사용
    fun postNumber(postData: RecordModel, isPublic: Boolean) {
        val resultData: Call<NumberModel>? = retrofitService?.postNumber(postData)
        resultData?.enqueue(object : Callback<NumberModel> {
            override fun onResponse(
                call: Call<NumberModel>,
                response: Response<NumberModel>
            ) {
                if (response.isSuccessful) {
                    val result: NumberModel = response.body()!!
                    if (isPublic)
                        EventBus.getDefault().post(NumberPublicEvent(true, result))
                    else
                        EventBus.getDefault().post(NumberPrivateEvent(true, result))
                } else {
                    Log.d("resultt", "실패코드_${response.code()}")
                    if (isPublic)
                        EventBus.getDefault().post(NumberPublicEvent(false, NumberModel("")))
                    else
                        EventBus.getDefault().post(NumberPrivateEvent(false, NumberModel("")))
                }
            }

            override fun onFailure(call: Call<NumberModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("resultt", "통신 실패")
                if (isPublic)
                    EventBus.getDefault().post(NumberPublicEvent(false, NumberModel("")))
                else
                    EventBus.getDefault().post(NumberPrivateEvent(false, NumberModel("")))
            }
        })
    }

    fun toRemitService(postData: RemitRequestModel) {
        Log.d("remitt_post: ", postData.toString())
        val resultData: Call<RemitResponseModel>? = retrofitService?.remitService(postData)
        resultData?.enqueue(object : Callback<RemitResponseModel> {
            override fun onResponse(
                call: Call<RemitResponseModel>,
                response: Response<RemitResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result: RemitResponseModel = response.body()!!
                    EventBus.getDefault().post(RemitEvent(true, result))
                    Log.d("remitt", result.toString())
                } else {
                    Log.d("remitt", "실패코드_${response.code()}")
                    EventBus.getDefault().post(RemitEvent(false, RemitResponseModel(false)))
                }
            }

            override fun onFailure(call: Call<RemitResponseModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("remitt", "통신 실패")
                EventBus.getDefault().post(RemitEvent(false, RemitResponseModel(false)))
            }
        })
    }
}