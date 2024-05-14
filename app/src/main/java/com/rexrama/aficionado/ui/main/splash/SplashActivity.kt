package com.rexrama.aficionado.ui.main.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rexrama.aficionado.databinding.ActivitySplashBinding
import com.rexrama.aficionado.ui.auth.welcome.WelcomeActivity
import com.rexrama.aficionado.ui.main.home.MainActivity
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val dataStore = UserPreference.getInstance(dataStore)
        setViewModel(dataStore)

    }


    private fun setViewModel(pref: UserPreference) {
        val viewModelFactory = ViewModelFactory(pref, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]

        viewModel.getUser().observe(this) { user ->
            val intent = if (user.isLogin) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, WelcomeActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                finish()
            }, DELAY_MILLIS)

        }
    }

    companion object {
        private const val DELAY_MILLIS: Long = 3000
    }
}