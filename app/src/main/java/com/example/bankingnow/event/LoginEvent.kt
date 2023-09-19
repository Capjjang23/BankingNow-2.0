package com.example.bankingnow.event

import com.example.bankingnow.model.PasswordCheckResponse

class LoginEvent (val isSuccess: Boolean, val result: PasswordCheckResponse)