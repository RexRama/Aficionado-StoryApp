package com.rexrama.aficionado.ui.main.upload

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rexrama.aficionado.R
import com.rexrama.aficionado.databinding.ActivityUploadStoryBinding
import com.rexrama.aficionado.ui.auth.welcome.WelcomeActivity
import com.rexrama.aficionado.ui.main.home.MainActivity
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.Util
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadStoryBinding
    private lateinit var viewModel: UploadStoryViewmodel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var doubleBackToExit = false
    private var getFile: File? = null
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bottomNavigation: BottomNavigationView = binding.bottomNavigation
        bottomNavigation.selectedItemId = R.id.add_story
        setupNavigation(bottomNavigation)

        val dataStore = UserPreference.getInstance(dataStore)
        setViewModel(dataStore)
        setBackButton()
        setProcess()
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
                    "Turn on the permission first.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun setProcess() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btUploadCamera.setOnClickListener { startCamera() }
        binding.btUploadGallery.setOnClickListener { startGallery() }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.getUserLocation(this, fusedLocationClient)

        viewModel.getUser().observe(this) { user ->
            val token = "Bearer ${user.token}"

            binding.btUploadUpload.setOnClickListener {
                val description = binding.edAddDescription
                description.error = if (description.text.toString().isEmpty()) {
                    "Enter the description!"
                } else {
                    null
                }

                if (description.error == null) {
                    val location = if (binding.cbSetLocation.isChecked) {
                        viewModel.location.value
                    } else {
                        null
                    }

                    postStory(
                        token,
                        description.text.toString(),
                        location?.latitude,
                        location?.longitude
                    )
                }
            }
        }
    }

    private fun postStory(token: String, descriptionText: String, lat: Double?, lon: Double?) {
        if (getFile != null) {
            val imageFile = reduceFileImage(getFile as File)
            val description = descriptionText.toRequestBody("text/plain".toMediaType())
            val requestImage = imageFile.asRequestBody("image/jpg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImage
            )
            if (lat != null && lon != null) {
                viewModel.postStory(token, imageMultipart, description, lat, lon)
            } else {
                viewModel.postStory(token, imageMultipart, description, null, null)
            }
        } else {
            Toast.makeText(this, "Please choose an Image to Upload!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        createTemporaryFiles(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this@UploadStoryActivity,
                "${packageName}.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intentCamera)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val newFile = File(currentPhotoPath)
            newFile.let { file ->
                getFile = file
                rotateFile(file)
                binding.ivImagePreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun startGallery() {
        val intentGallery = Intent()
        intentGallery.action = ACTION_GET_CONTENT
        intentGallery.type = "image/*"
        val intentChooser = Intent.createChooser(intentGallery, "Choose an image")
        launcherIntentGallery.launch(intentChooser)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImage = result.data?.data as Uri
                selectedImage.let { uri ->
                    val myFile = uriToFile(uri, this@UploadStoryActivity)
                    getFile = myFile
                    rotateFile(myFile)
                    binding.ivImagePreview.setImageURI(uri)
                }
            }
        }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun setViewModel(pref: UserPreference) {
        val viewModelFactory = ViewModelFactory(pref, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[UploadStoryViewmodel::class.java]

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.finishActivity.observe(this) {
            if (it == true) {
                val toHome = Intent(this, MainActivity::class.java)
                toHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(toHome)
                finish()
            }
        }
    }

    private fun setupNavigation(icon: BottomNavigationView) {
        icon.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_home -> {
                    Util().toHome(this)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.add_story -> {
                    false
                }

                R.id.to_location -> {
                    Util().toLocation(this)
                    finish()
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

    private fun showLoading(isLoading: Boolean) {
        binding.pbUploadStory.visibility = if (isLoading) View.VISIBLE else View.GONE
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
                        this@UploadStoryActivity,
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
}