package com.rexrama.aficionado.ui.auth.register

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rexrama.aficionado.data.model.RegisterModel
import com.rexrama.aficionado.data.remote.api.ApiConfig
import com.rexrama.aficionado.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val apiService = ApiConfig.getApiService()

    private val _moveActivity = MutableLiveData<Unit>()
    val moveActivity: LiveData<Unit> = _moveActivity

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun userRegistration(user: RegisterModel, context: Context) {
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
                        resultDialog(
                            "Account Successfully Created",
                            "Your Account has been created. Please login to continue!",
                            context
                        )
                    } else {
                        resultDialog("Registration Failed", response.message(), context)
                    }
                } else if (response.code() == 400) {
                    resultDialog("Registration Failed", "Email already taken!", context)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                resultDialog("Registration Failed", t.message, context)
            }
        })
    }

    private fun resultDialog(title: String, message: String?, context: Context) {
        if (title == "Registration Failed") {
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
                setPositiveButton("Proceed") { _, _ ->
                    _moveActivity.value = Unit
                }
                setCancelable(false)
                create()
                show()
            }

        }
    }
}
