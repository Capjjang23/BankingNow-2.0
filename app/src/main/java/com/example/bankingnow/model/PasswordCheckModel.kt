package com.example.bankingnow.model

data class PasswordCheckRequest(
    val password: String
)

data class PasswordCheckResponse (
    val is_password_correct : Boolean
)