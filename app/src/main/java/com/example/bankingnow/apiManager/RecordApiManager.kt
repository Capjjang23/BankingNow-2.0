package com.example.bankingnow.apiManager

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bankingnow.api.RecordService
import com.example.bankingnow.event.PostNumberEvent
import com.example.bankingnow.model.GetBalanceModel
import com.example.bankingnow.model.GetBankRequestModel
import com.example.bankingnow.model.GetBankResponseModel
import com.example.bankingnow.model.PasswordCheckRequest
import com.example.bankingnow.model.PasswordCheckResponse
import com.example.bankingnow.model.RecordModel
import com.example.bankingnow.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        fun getBalance(balance: Long)
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
        // http://192.168.47.145:8000
        // https://jsonplaceholder.typicode.com
        retrofit = Retrofit.Builder()
            .baseUrl("http://223.194.133.37:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofitService = retrofit?.create(RecordService::class.java)
    }

    fun postTest(postData: RecordModel) {
        val resultData: Call<PostTestModel>? = retrofitService?.postTest(postData)
        resultData?.enqueue(object : Callback<PostTestModel> {
            override fun onResponse(
                call: Call<PostTestModel>,
                response: Response<PostTestModel>
            ) {
                if (response.isSuccessful) {
                    val result: PostTestModel = response.body()!!
                    Log.d("resultt", result.toString())
                    //EventBus.getDefault().post(GetDataEvent(resultData))
                } else {
                    //EventBus.getDefault().post(GetDataEvent(null))
                    Log.d("resultt", "실패코드_${response.code()}")
                }
            }

            override fun onFailure(call: Call<PostTestModel>, t: Throwable) {
                t.printStackTrace()
                //EventBus.getDefault().post(GetDataEvent(null))
                Log.d("resultt", "통신 실패")
            }
        })
    }

    fun getTest() {
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
                } else {
                    Log.d("password check", "실패")
                    Log.d("response:", response.toString())
                }
            }

            override fun onFailure(call: Call<PasswordCheckResponse>, t: Throwable) {
                t.printStackTrace()
                //EventBus.getDefault().post(GetDataEvent(null))
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
                    listener?.getBalance(result.balance)
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
        val resultData: Call<GetBankResponseModel>? = retrofitService?.getBank(GetBankRequestModel(bankSpeech))
        resultData?.enqueue(object : Callback<GetBankResponseModel> {
            override fun onResponse(
                call: Call<GetBankResponseModel>,
                response: Response<GetBankResponseModel>
            ) {
                if (response.isSuccessful) {
                    val result: GetBankResponseModel = response.body()!!
                    Log.d("getBank", result.toString())
                } else {
                    Log.d("getBank", "실패")
                }
            }

            override fun onFailure(call: Call<GetBankResponseModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("getBank", "통신 실패")
            }
        })
    }

    // 송금 금액, 계좌 번호 받아올때  사용
    fun postNumber(postData: RecordModel) {
        val resultData: Call<NumberModel>? = retrofitService?.postNumber(postData)
        resultData?.enqueue(object : Callback<NumberModel> {
            override fun onResponse(
                call: Call<NumberModel>,
                response: Response<NumberModel>
            ) {
                if (response.isSuccessful) {
                    val result: NumberModel = response.body()!!
//                    EventBus.getDefault().post(PostNumberEvent(true, result))
                } else {
                    Log.d("resultt", "실패코드_${response.code()}")
                    EventBus.getDefault().post(PostNumberEvent(false, NumberModel("")))
                }
            }

            override fun onFailure(call: Call<NumberModel>, t: Throwable) {
                t.printStackTrace()
                Log.d("resultt", "통신 실패")
                EventBus.getDefault().post(PostNumberEvent(false, NumberModel("")))
            }
        })
    }
}