package com.example.bankingnow.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bankingnow.model.RemitRequestModel

class RemitViewModel: ViewModel() {
    private val _remitLiveData: MutableLiveData<RemitRequestModel> = MutableLiveData(RemitRequestModel())
    val remitLiveData: LiveData<RemitRequestModel>
        get() = _remitLiveData


    fun isFill(): Boolean {
        return (_remitLiveData.value!!.money.isNotBlank())
    }

    fun setRemitMoney(newMoney: String) {
        _remitLiveData.value!!.money = newMoney
        Log.d("resultt", remitLiveData.value.toString())
    }

    fun setRemitAccount(newAccount: String) {
        _remitLiveData.value!!.account = newAccount
        Log.d("resultt", remitLiveData.value.toString())
    }

    override fun toString(): String {
        return "RemitViewModel(_remitLiveData=${_remitLiveData.value.toString()})"
    }


}