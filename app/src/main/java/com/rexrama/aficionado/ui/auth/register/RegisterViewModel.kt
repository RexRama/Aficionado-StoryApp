package com.rexrama.aficionado.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rexrama.aficionado.data.model.RegisterModel
import com.rexrama.aficionado.data.remote.api.ApiConfig
import com.rexrama.aficionado.data.remote.response.RegisterResponse
import com.rexrama.aficionado.utils.DialogUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val dialogUtils: DialogUtils) : ViewModel() {
    private val apiService = ApiConfig.getApiService()

    private val _moveActivity = MutableLiveData<Unit>()
    val moveActivity: LiveData<Unit> = _moveActivity

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun userRegistration(user: RegisterModel) {
        _isLoading.value = true
        val client = apiService.userRegister(user)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        dialogUtils.showResultDialog(
                            "Account Successfully Created",
                            "Your Account has been created. Please login to continue!"
                        ) {
                            _moveActivity.value = Unit
                        }
                    } else {
                        dialogUtils.showErrorDialog("Registration Failed", response.message())
                    }
                } else if (response.code() == 400) {
                    dialogUtils.showErrorDialog("Registration Failed", "Email already taken!")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                dialogUtils.showErrorDialog("Registration Failed", t.message)
            }
        })
    }
}
