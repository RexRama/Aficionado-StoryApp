package com.rexrama.aficionado.ui.main.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rexrama.aficionado.R
import com.rexrama.aficionado.adapter.LoadingAdapter
import com.rexrama.aficionado.adapter.ProfileAdapter
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.databinding.ActivityProfileBinding
import com.rexrama.aficionado.ui.auth.welcome.WelcomeActivity
import com.rexrama.aficionado.ui.main.detail.DetailActivity
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.Util
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation: BottomNavigationView = binding.bottomNavigation
        bottomNavigation.selectedItemId = R.id.to_profile
        setupNavigation(bottomNavigation)

        setView()


    }

    private fun setupNavigation(icon: BottomNavigationView) {
        icon.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_home -> {
                    Util().toHome(this)
                    finish()
                    false
                }

                R.id.add_story -> {
                    Util().toUpload(this)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.to_location -> {
                    Util().toLocation(this)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.to_profile -> {
                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }

        binding.fabLogout.setOnClickListener{
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
        }
    }

    private fun setViewModel(profileAdapter: ProfileAdapter, dataStore: UserPreference) {
        val viewModelFactory = ViewModelFactory(dataStore, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        viewModel.getUser()?.observe(this) { user ->
            val token = "Bearer ${user.token}"
            viewModel.setToken(token)
            viewModel.fetchStories().observe(this) { pagingData ->
                profileAdapter.submitData(lifecycle, pagingData)
            }
        }
    }

    private fun setView() {
        val profileAdapter = ProfileAdapter()
        binding.rvProfile.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            adapter = profileAdapter.withLoadStateFooter(
                footer = LoadingAdapter { profileAdapter.retry() }
            )

        }

        val dataStore = UserPreference.getInstance(dataStore)
        setViewModel(profileAdapter, dataStore)

        profileAdapter.setOnItemClickCallback(object : Util.OnItemClickCallBack {
            override fun onItemClicked(data: ListStoryItem) {
                val toDetail = Intent(this@ProfileActivity, DetailActivity::class.java).apply {
                    putExtra("name", data.name)
                    putExtra("url", data.photoUrl)
                    putExtra("description", data.description)
                    putExtra("date", data.createdAt.take(10))
                }
                startActivity(toDetail)

            }

        })

        profileAdapter.addLoadStateListener {
            showLoading(it)
        }
    }


    private fun showLoading(loadState: CombinedLoadStates) {
        binding.pbHome.visibility =
            if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE
    }

}