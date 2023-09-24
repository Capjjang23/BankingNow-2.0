package com.example.bankingnow.apiManager

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bankingnow.api.RecordService
import com.example.bankingnow.event.BankEvent
import com.example.bankingnow.event.LoginEvent
import com.example.bankingnow.event.NumberPrivateEvent
import com.example.bankingnow.event.NumberPublicEvent
import com.example.bankingnow.event.RemitEvent
import com.example.bankingnow.event.UserNameEvent
import com.example.bankingnow.model.GetBalanceModel
import com.example.bankingnow.model.BankRequestModel
import com.example.bankingnow.model.BankResponseModel
import com.example.bankingnow.model.PasswordCheckRequest
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

    var listener: getMyBalance? = null


    // 클래스내 인터페이스 작성
    interface getMyBalance {
        fun getBalance(balanceModel: GetBalanceModel)
    }

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        // 다른 인터셉터 또는 설정들을 추가할 수 있습니다.
        .build()


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
        // http://192.168.0.17:8000
        // https://jsonplaceholder.typicode.com
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.6:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofitService = retrofit?.create(RecordService::class.java)
    }


    fun postTest(postData: RecordModel, isPublic: Boolean) {
        val resultData: Call<NumberModel>? = retrofitService?.postNumber(postData)
        resultData?.enqueue(object : Callback<NumberModel> {
            override fun onResponse(
                call: Call<NumberModel>,
                response: Response<NumberModel>
            ) {
                if (response.isSuccessful) {
                    val result: NumberModel = response.body()!!
                } else {
                    Log.d("resultt", "실패코드_${response.code()}")
                }
            }

            override fun onFailure(call: Call<NumberModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("resultt", "통신 실패")
            }
        })
    }

    fun checkPW(password: String) {
        val passwordCheckRequest = PasswordCheckRequest(password)
        val resultData: Call<PasswordCheckResponse>? =
            retrofitService?.checkPassword(passwordCheckRequest)
        resultData?.enqueue(object : Callback<PasswordCheckResponse> {
            override fun onResponse(
                call: Call<PasswordCheckResponse>,
                response: Response<PasswordCheckResponse>
            ) {
                if (response.isSuccessful) {
                    val result: PasswordCheckResponse = response.body()!!
                    Log.d("password check", result.toString())
                    EventBus.getDefault().post(LoginEvent(true, result))
                } else {
                    Log.d("password check", "실패")
                    Log.d("response:", response.toString())
                    EventBus.getDefault().post(LoginEvent(false, PasswordCheckResponse()))
                }
            }

            override fun onFailure(call: Call<PasswordCheckResponse>, t: Throwable) {
                t.printStackTrace()
                EventBus.getDefault().post(LoginEvent(false, PasswordCheckResponse()))
                Log.d("password check", "통신 실패")
            }
        })
    }

    fun getBalance(){
        val resultData: Call<GetBalanceModel>? = retrofitService?.getBalance()
        resultData?.enqueue(object : Callback<GetBalanceModel> {
            override fun onResponse(
                call: Call<GetBalanceModel>,
                response: Response<GetBalanceModel>
            ) {
                if (response.isSuccessful) {
                    val result: GetBalanceModel = response.body()!!
                    listener?.getBalance(result)
                    Log.d("getBalance", result.toString())
                } else {
                    Log.d("getBalance", "실패")
                }
            }

            override fun onFailure(call: Call<GetBalanceModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("getBalance", "통신 실패")
            }
        })
    }

    fun getBank(bankSpeech :String){
        val bankRequestModel = BankRequestModel(bankSpeech)
        Log.d("getBank", bankRequestModel.toString())
        val resultData: Call<BankResponseModel>? = retrofitService?.postBank(bankRequestModel)
        resultData?.enqueue(object : Callback<BankResponseModel> {
            override fun onResponse(
                call: Call<BankResponseModel>,
                response: Response<BankResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result: BankResponseModel = response.body()!!
                    Log.d("getBank", result.toString())
                    EventBus.getDefault().post(BankEvent(true, result))
                } else {
                    Log.d("getBank", "실패")
                    EventBus.getDefault().post(BankEvent(false, BankResponseModel("")))
                }
            }

            override fun onFailure(call: Call<BankResponseModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("getBank", "통신 실패")
                EventBus.getDefault().post(BankEvent(false, BankResponseModel("")))
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

    fun postUserName(postData: UserRequestModel) {
        Log.d("resultt", postData.toString())
        val resultData: Call<UserResponseModel>? = retrofitService?.postUserName(postData)
        resultData?.enqueue(object : Callback<UserResponseModel> {
            override fun onResponse(
                call: Call<UserResponseModel>,
                response: Response<UserResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result: UserResponseModel = response.body()!!
                    EventBus.getDefault().post(UserNameEvent(true, result))
                } else {
                    Log.d("resultt", "실패코드_${response.code()}")
                    EventBus.getDefault().post(UserNameEvent(false, UserResponseModel("")))
                }
            }

            override fun onFailure(call: Call<UserResponseModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("resultt", "통신 실패")
                EventBus.getDefault().post(UserNameEvent(false, UserResponseModel("")))
            }
        })
    }

    fun remit(postData: RemitRequestModel) {
        Log.d("resultt", postData.toString())
        val resultData: Call<RemitResponseModel>? = retrofitService?.remit(postData)
        resultData?.enqueue(object : Callback<RemitResponseModel> {
            override fun onResponse(
                call: Call<RemitResponseModel>,
                response: Response<RemitResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result: RemitResponseModel = response.body()!!
                    Log.d("remitt",result.result_msg)
                    EventBus.getDefault().post(RemitEvent(true, result))
                } else {
                    Log.d("remitt", "실패코드_${response.code()}")
                    EventBus.getDefault().post(RemitEvent(false, RemitResponseModel("")))
                }
            }

            override fun onFailure(call: Call<RemitResponseModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("remitt", "통신 실패")
                EventBus.getDefault().post(UserNameEvent(false, UserResponseModel("")))
            }
        })
    }
}