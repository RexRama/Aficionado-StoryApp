package com.rexrama.aficionado.ui.main.detail

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rexrama.aficionado.databinding.ActivityViewImageFullScreenBinding

@Suppress("DEPRECATION")
class ViewImageFullScreen : AppCompatActivity() {
    private lateinit var binding: ActivityViewImageFullScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImageFullScreenBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(binding.root)
        val detailImage = intent.getStringExtra("imageUri")

        // Display image in full screen

        Glide.with(this)
            .load(detailImage)
            .into(binding.fullScreenImageView)
    }
}