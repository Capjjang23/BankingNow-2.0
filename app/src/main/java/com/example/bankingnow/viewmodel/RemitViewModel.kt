package com.example.bankingnow.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bankingnow.model.RemitIsFillModel
import com.example.bankingnow.model.RemitCheckModel

class RemitViewModel: ViewModel() {
    private val _remitLiveData: MutableLiveData<RemitCheckModel> = MutableLiveData(RemitCheckModel())
    val remitLiveData: LiveData<RemitCheckModel>
        get() = _remitLiveData

    fun getRemit(): RemitIsFillModel {
        return if (_remitLiveData.value!!.money.isNotBlank()
            && _remitLiveData.value!!.user.account.isNotBlank()
            && _remitLiveData.value!!.user.bank.isNotBlank())
            RemitIsFillModel(true, _remitLiveData.value!!)
        else
            RemitIsFillModel(false, RemitCheckModel())
    }

    fun setRemitMoney(newMoney: String) {
        _remitLiveData.value!!.money = newMoney
        Log.d("resultt", remitLiveData.value.toString())
    }

    fun setRemitAccount(newAccount: String) {
        _remitLiveData.value!!.user.account = newAccount
        Log.d("resultt", remitLiveData.value.toString())
    }

    fun setRemitBank(newBank: String) {
        _remitLiveData.value!!.user.bank = newBank
        Log.d("resultt", remitLiveData.value.toString())
    }
}