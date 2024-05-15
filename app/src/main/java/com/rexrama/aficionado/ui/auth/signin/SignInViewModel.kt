package com.rexrama.aficionado.ui.auth.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rexrama.aficionado.data.model.SignInModel
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.data.remote.api.ApiConfig
import com.rexrama.aficionado.data.remote.response.LoginResponse
import com.rexrama.aficionado.utils.DialogUtils
import com.rexrama.aficionado.utils.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(private val pref: UserPreference, private val dialogUtils: DialogUtils) :
    ViewModel() {
    private val apiService = ApiConfig.getApiService()

    private val _moveActivity = MutableLiveData<Unit>()
    val moveActivity: LiveData<Unit> = _moveActivity

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun userSignIn(user: SignInModel) {
        _isLoading.value = true
        val client = apiService.userSignIn(user)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        isLogin(responseBody.loginResult.token)
                        dialogUtils.showResultDialog(
                            "SignIn Successful",
                            "Login successful, press okay to proceed!"
                        ) {
                            _moveActivity.value = Unit
                        }
                    } else {
                        dialogUtils.showErrorDialog("SignIn Failed", response.message())
                    }
                } else if (response.code() == 401) {
                    dialogUtils.showErrorDialog("SignIn Failed", "Wrong Credentials!")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                dialogUtils.showErrorDialog("SignIn Failed", t.message)
            }

        })
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun isLogin(token: String) {
        viewModelScope.launch {
            pref.login(token)
        }
    }
}