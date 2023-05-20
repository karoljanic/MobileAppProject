package org.mobileapp.model

data class LoginModel(
    val isLoginSuccessful : Boolean = false,
    val loginError: String? = null,
)

data class LoginResult(
    val data: UserData?,
    val errorMessage: String?,
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
)