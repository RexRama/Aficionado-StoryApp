package com.rexrama.aficionado.ui.main.detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rexrama.aficionado.R
import com.rexrama.aficionado.databinding.ActivityDetailBinding
import com.rexrama.aficionado.ui.auth.welcome.WelcomeActivity
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.Util
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigation: BottomNavigationView = binding.bottomNavigation
        bottomNavigation.selectedItemId = R.id.to_home
        setupNavigation(bottomNavigation)

        setDetail()

        val dataStore = UserPreference.getInstance(dataStore)
        setViewModel(dataStore)


    }

    private fun setDetail() {
        val detailName = intent.getStringExtra(EXTRA_NAME)
        val detailDate = intent.getStringExtra(EXTRA_DATE)
        val detailUrl = intent.getStringExtra(EXTRA_URL)
        val detailDescription = intent.getStringExtra(EXTRA_DESCRIPTION)

        Glide.with(this)
            .load(detailUrl)
            .into(binding.ivDetailImage)

        binding.apply {
            tvDetailUsername.text = detailName
            tvDetailDescriptionText.text = detailDescription
            tvDetailDate.text = detailDate
        }

        setViewImageFullScreen(detailUrl)
    }

    private fun setViewImageFullScreen(detailUrl: String?) {
        val fullScreenView = binding.ivDetailImage
        fullScreenView.setOnClickListener {
            val intentFullScreen = Intent(this@DetailActivity, ViewImageFullScreen::class.java)
            intentFullScreen.putExtra("imageUri", detailUrl)
            startActivity(intentFullScreen)
        }
    }

    private fun setViewModel(dataStore: UserPreference) {

        val viewModelFactory = ViewModelFactory(dataStore)
        viewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java]

    }

    private fun setupNavigation(item: BottomNavigationView) {
        item.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_home -> {
                    Util().toHome(this)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.add_story -> {
                    Util().toUpload(this)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.to_location -> {
                    Util().toLocation(this)
                    return@setOnItemSelectedListener true
                }

                R.id.logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("LogOut")
                        setMessage("Are you sure you want to logout?")
                        setPositiveButton("Yes") { _, _ ->
                            viewModel.logout()
                            val intent = Intent(context, WelcomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("No") { _, _ ->

                        }
                        create()
                        show()
                    }
                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }
    }


    companion object {
        const val EXTRA_NAME = "name"
        const val EXTRA_DATE = "date"
        const val EXTRA_URL = "url"
        const val EXTRA_DESCRIPTION = "description"
    }


}