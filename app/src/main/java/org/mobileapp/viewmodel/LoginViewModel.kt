package org.mobileapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mobileapp.model.LoginModel
import org.mobileapp.model.LoginResult

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginModel())
    val state = _state.asStateFlow()

    fun onLoginResult(res: LoginResult) {
        _state.update {
            it.copy(isLoginSuccessful = res.data != null, loginError = res.errorMessage)
        }
    }

    fun reset() {
        _state.update { LoginModel() }
    }


}