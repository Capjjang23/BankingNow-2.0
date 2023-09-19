package com.example.bankingnow.event

import com.example.bankingnow.model.BankResponseModel
import com.example.bankingnow.model.NumberModel

class BankEvent (val isSuccess: Boolean, val result: BankResponseModel)