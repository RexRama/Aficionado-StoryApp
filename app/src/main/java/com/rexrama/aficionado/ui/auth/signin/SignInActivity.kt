package com.rexrama.aficionado.ui.auth.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rexrama.aficionado.data.model.SignInModel
import com.rexrama.aficionado.data.model.UserModel
import com.rexrama.aficionado.databinding.ActivitySignInBinding
import com.rexrama.aficionado.ui.auth.register.RegisterActivity
import com.rexrama.aficionado.ui.main.home.MainActivity
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var user: UserModel
    private lateinit var viewModel: SignInViewModel

    companion object {
        private const val DELAY_DURATION: Long = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataStore = UserPreference.getInstance(dataStore)
        setViewModel(dataStore)
        setProcess()

        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofPropertyValuesHolder(
            binding.ivSigninImage,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f)
        ).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        val title = createAnimation(binding.tvSigninTitle)
        val subtitle = createAnimation(binding.tvSigninSubtitle)
        val emailTitle = createAnimation(binding.tvSigninEmail)
        val emailLayout = createAnimation(binding.tfSigninEmail)
        val emailEdit = createAnimation(binding.editSigninEmail)
        val passTitle = createAnimation(binding.tvSigninPassword)
        val passLayout = createAnimation(binding.tfSigninPassword)
        val passEdit = createAnimation(binding.editSigninPassword)
        val buttonSignIn = createAnimation(binding.btLogin)
        val buttonRegister = createAnimation(binding.btRegister)
        val tvOr = createAnimation(binding.orTextView)
        val line1 = createAnimation(binding.lineBottom)
        val line2 = createAnimation(binding.lineTop)

        val email = AnimatorSet().apply {
            playTogether(emailTitle, emailLayout, emailEdit)
        }

        val password = AnimatorSet().apply {
            playTogether(passTitle, passLayout, passEdit)
        }

        val button = AnimatorSet().apply {
            playTogether(buttonSignIn, line1, line2, tvOr, buttonRegister)
        }

        AnimatorSet().apply {
            playSequentially(title, subtitle, email, password, button)
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
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btLogin.setOnClickListener {
            val signInEmail = binding.editSigninEmail
            val signInPassword = binding.editSigninPassword

            when {
                signInEmail.text.toString().isEmpty() -> {
                    signInEmail.error = "Email field cannot be empty!"
                }

                signInPassword.text.toString().isEmpty() -> {
                    signInPassword.error = "Password field cannot be empty!"
                }

                else -> {
                    signInEmail.error = null
                    signInPassword.error = null
                }
            }

            if (signInEmail.error == null && signInPassword.error == null) {
                val user = SignInModel(signInEmail.text.toString(), signInPassword.text.toString())
                viewModel.userSignIn(user, this)
            }
        }
    }

    private fun setViewModel(dataStore: UserPreference) {
        val viewModelFactory = ViewModelFactory(dataStore)
        viewModel = ViewModelProvider(
            this, viewModelFactory
        )[SignInViewModel::class.java]

        viewModel.getUser().observe(this) { user ->
            this.user = user
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.moveActivity.observe(this) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(it: Boolean) {
        binding.pbSignIn.visibility = if (it) View.VISIBLE else View.GONE

    }
}