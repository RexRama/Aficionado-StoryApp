package com.rexrama.aficionado.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rexrama.aficionado.data.model.RegisterModel
import com.rexrama.aficionado.databinding.ActivityRegisterBinding
import com.rexrama.aficionado.ui.auth.signin.SignInActivity
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    companion object {
        private const val DELAY_DURATION: Long = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dataStore = UserPreference.getInstance(dataStore)
        setViewModel(dataStore)
        setProcess()

        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofPropertyValuesHolder(
            binding.cvRegister,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.1f)
        ).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        val registerTitle = createAnimation(binding.tvRegisterTitle)
        val registerName = createAnimation(binding.tvRegisterName)
        val registerNameLayout = createAnimation(binding.tfEditRegisterName)
        val registerNameEdit = createAnimation(binding.editRegisterName)
        val registerEmail = createAnimation(binding.tvRegisterEmail)
        val registerEmailLayout = createAnimation(binding.tfEditRegisterEmail)
        val registerEmailEdit = createAnimation(binding.editRegisterEmail)
        val registerPassword = createAnimation(binding.tvRegisterPassword)
        val registerPasswordLayout = createAnimation(binding.tfEditRegisterPassword)
        val registerPasswordEdit = createAnimation(binding.cvEditRegisterPassword)
        val registerButton = createAnimation(binding.btRegister)

        val regName = AnimatorSet().apply {
            playTogether(registerName, registerNameEdit, registerNameLayout)
        }

        val regEmail = AnimatorSet().apply {
            playTogether(registerEmail, registerEmailLayout, registerEmailEdit)
        }

        val regPass = AnimatorSet().apply {
            playTogether(registerPassword, registerPasswordLayout, registerPasswordEdit)
        }

        AnimatorSet().apply {
            playSequentially(registerTitle, regName, regEmail, regPass, registerButton)
            startDelay = DELAY_DURATION
            start()
        }
    }

    private fun createAnimation(view: View, animation: Animation? = null): ObjectAnimator {
        return if (animation != null) {
            view.startAnimation(animation)
            ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(animation.duration)
        } else {
            ObjectAnimator.ofFloat(view, View.ALPHA, 1f).setDuration(DELAY_DURATION)
        }
    }

    private fun setProcess() {
        binding.btRegister.setOnClickListener {
            val regName = binding.editRegisterName
            val regEmail = binding.editRegisterEmail
            val regPassword = binding.cvEditRegisterPassword


            when {
                regName.text.toString().isEmpty() -> {
                    regName.error = "Name field cannot be empty!"
                }

                regEmail.text.toString().isEmpty() -> {
                    regEmail.error = "Email field cannot be empty!"
                }

                regPassword.text.toString().isEmpty() -> {
                    regPassword.error = "Password field cannot be empty"
                }
            }

            if (regName.error == null && regEmail.error == null && regPassword.error == null) {
                val user = RegisterModel(
                    regName.text.toString(),
                    regEmail.text.toString(),
                    regPassword.text.toString()
                )
                viewModel.userRegistration(user)
            }

        }
    }


    private fun setViewModel(dataStore: UserPreference) {
        val viewModelFactory = ViewModelFactory(dataStore, this)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        )[RegisterViewModel::class.java]

        viewModel.moveActivity.observe(this) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        viewModel.isLoading.observe(this) {
            isLoading(it)
        }
    }

    private fun isLoading(it: Boolean) {
        binding.pbRegister.visibility = if (it) View.VISIBLE else View.GONE
    }
}
