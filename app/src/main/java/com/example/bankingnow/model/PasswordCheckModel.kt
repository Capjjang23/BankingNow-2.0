package com.example.bankingnow.model

data class PasswordCheckRequest(
    val password: String
)

data class PasswordCheckResponse (
    var is_password_correct : Boolean = false
)