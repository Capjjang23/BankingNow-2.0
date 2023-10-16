package com.example.bankingnow.event

import com.example.bankingnow.model.LoginResponseModel

class LoginEvent (val isSuccess: Boolean, val result: LoginResponseModel)