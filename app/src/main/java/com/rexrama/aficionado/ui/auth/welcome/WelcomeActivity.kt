package com.rexrama.aficionado.ui.auth.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.rexrama.aficionado.databinding.ActivityWelcomeBinding
import com.rexrama.aficionado.ui.auth.register.RegisterActivity
import com.rexrama.aficionado.ui.auth.signin.SignInActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    companion object {
        private const val DELAY_DURATION: Long = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buttonAction()

        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofPropertyValuesHolder(
            binding.ivAuthImage,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f)
        ).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }

        val welcomeTitle = createAnimation(binding.tvAuthTitle)
        val welcomeDesc = createAnimation(binding.tvAuthDesc)
        val welcomeRegisterButton = createAnimation(binding.btRegister)
        val welcomeSignInButton = createAnimation(binding.btLogin)

        val button = AnimatorSet().apply {
            playTogether(welcomeRegisterButton, welcomeSignInButton)
        }

        AnimatorSet().apply {
            playSequentially(welcomeTitle, welcomeDesc, button)
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


    private fun buttonAction() {
        binding.btLogin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}