package com.rexrama.aficionado.ui.auth.signin

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rexrama.aficionado.data.model.SignInModel
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.data.remote.api.ApiConfig
import com.rexrama.aficionado.data.remote.response.LoginResponse
import com.rexrama.aficionado.utils.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(private val pref: UserPreference) : ViewModel() {
    private val apiService = ApiConfig.getApiService()

    private val _moveActivity = MutableLiveData<Unit>()
    val moveActivity: LiveData<Unit> = _moveActivity

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun userSignIn(user: SignInModel, context: Context) {
        _isLoading.value = true
        val client = apiService.userSignIn(user)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        isLogin(responseBody.loginResult.token)
                        resultDialog(
                            "SignIn Successful",
                            "Login successful, press okay to proceed!",
                            context
                        )
                    } else {
                        resultDialog("SignIn Failed", response.message(), context)
                    }
                } else if (response.code() == 401) {
                    resultDialog("SignIn Failed", "Wrong Credentials!", context)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                resultDialog("SignIn Failed", t.message, context)
            }

        })
    }

    private fun resultDialog(title: String, message: String?, context: Context) {
        if (title == "SignIn Failed") {
            AlertDialog.Builder(context).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("Retry") { _, _ ->
                }
                setCancelable(false)
                create()
                show()
            }
        } else {
            AlertDialog.Builder(context).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("Okay") { _, _ ->
                    _moveActivity.value = Unit
                }
                setCancelable(false)
                create()
                show()
            }
        }
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