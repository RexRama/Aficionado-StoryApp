package com.rexrama.aficionado.ui.main.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rexrama.aficionado.R
import com.rexrama.aficionado.adapter.StoryAdapter
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.databinding.ActivityMainBinding
import com.rexrama.aficionado.ui.auth.welcome.WelcomeActivity
import com.rexrama.aficionado.ui.main.detail.DetailActivity
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.Util
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private var doubleBackToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigation: BottomNavigationView = binding.bottomNavigation
        bottomNavigation.selectedItemId = R.id.to_home
        setupNavigation(bottomNavigation)

        setView()
        val dataStore = UserPreference.getInstance(dataStore)
        setViewModel(dataStore)
        setBackButton()
        fetchStories()

        setPermission()


    }

    private fun fetchStories() {
        viewModel.getUser()?.observe(this) { user ->
            val token = "Bearer ${user.token}"
            viewModel.setToken(token)
            viewModel.fetchStories(token)
        }
    }

    private fun setPermission() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

    }

    private fun setView() {
        val recyclerView = binding.rvStories
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
    }


    private fun setBackButton() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            @Suppress("DEPRECATION")
            override fun handleOnBackPressed() {
                if (doubleBackToExit) {
                    finishAffinity()
                } else {
                    doubleBackToExit = true
                    Toast.makeText(
                        this@MainActivity,
                        "Press back again to exit",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Handler().postDelayed({
                        doubleBackToExit = false
                    }, 2000)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    private fun setViewModel(pref: UserPreference) {
        val viewModelFactory = ViewModelFactory(pref, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        viewModel.stories.observe(this) { storyList ->
            if (storyList.isNotEmpty()) {
                setStories(storyList)
            } else {
                Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(this) {
            showLoading(it)
        }


    }

    private fun setStories(storyList: List<ListStoryItem>) {
        val storyAdapter = StoryAdapter(storyList)
        binding.rvStories.adapter = storyAdapter

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: ListStoryItem) {
                val toDetail = Intent(this@MainActivity, DetailActivity::class.java)
                toDetail.putExtra("name", data.name)
                toDetail.putExtra("url", data.photoUrl)
                toDetail.putExtra("description", data.description)
                toDetail.putExtra("date", data.createdAt.take(10))
                startActivity(toDetail)
            }

        })

    }

    private fun setupNavigation(icon: BottomNavigationView) {
        icon.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_home -> {
                    false
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
                    android.app.AlertDialog.Builder(this).apply {
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

    private fun showLoading(isLoading: Boolean) {
        binding.pbHome.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    "Turn on all permission first, use precise location.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


}
