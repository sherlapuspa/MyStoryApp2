package com.dicoding.mystoryapp.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.databinding.ActivityPostStoryBinding
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.ListStoryVM
import com.dicoding.mystoryapp.viewmodel.ListVMFactory
import com.dicoding.mystoryapp.viewmodel.UserVMFactory
import com.google.android.gms.maps.model.LatLng
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class PostStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var token: String

    private var imgFile: File? = null
    private lateinit var compImgFile: File
    private var latlng: LatLng? = null

    private val postStoryVM: ListStoryVM by lazy {
        ViewModelProvider(this, ListVMFactory(this))[ListStoryVM::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.actionBarPost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        handleUserActions()

        val userPref = UserPreferencesManager.getInstance(dataStore)
        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]

        authSplashVM.getToken().observe(this) {
            token = it
        }

        postStoryVM.isUploading.observe(this) {
            showToast(it)
        }

        postStoryVM.isLoad.observe(this) {
            showLoading(it)
        }
    }

    private val selectLocationResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val address = data.getStringExtra("address")
                    val lat = data.getDoubleExtra("lat", 0.0)
                    val lng = data.getDoubleExtra("lng", 0.0)
                    latlng = LatLng(lat, lng)

                    binding.detailLocation.text = address
                }
            }
        }

    private fun handleUserActions() {
        binding.postBtn.setOnClickListener {

            if (imgFile == null) {
                showToast(resources.getString(R.string.alertNullImg))
                return@setOnClickListener
            }

            val desc = binding.tvDesc.text.toString().trim()
            if (desc.isEmpty()) {
                binding.tvDesc.error = resources.getString(R.string.alertNullDesc)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val file = imgFile as File

                    var compFile: File? = null
                    var compFileSize = file.length()

                    while (compFileSize > 1 * 1024 * 1024) {
                        compFile = withContext(Dispatchers.Default) {
                            Compressor.compress(applicationContext, file)
                        }
                        compFileSize = compFile.length()
                    }

                    compImgFile = compFile ?: file

                }

                val reqImg = compImgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo", compImgFile.name, reqImg
                )

                val reqDesc = desc.toRequestBody("text/plain".toMediaType())

                postStoryVM.upload(
                    imgMultipart, reqDesc, latlng?.latitude, latlng?.longitude, token
                )
            }
        }

        binding.camBtn.setOnClickListener {
            launchCamera()
        }

        binding.galleryBtn.setOnClickListener {
            launchGallery()
        }
        binding.myLocation.setOnClickListener {
            val intent = Intent(this, SelectLocationActivity::class.java)
            selectLocationResultLauncher.launch(intent)
        }
    }

    private var img = false
    private lateinit var imgPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myImg = File(imgPath)
            imgFile = myImg
            val result = BitmapFactory.decodeFile(myImg.path)
            img = true
            binding.imgPreview.setImageBitmap(result)
            binding.tvDesc.requestFocus()
        }
    }

    private val currentDateTime: String = SimpleDateFormat(
        FILE_DATE_FORMAT, Locale.US
    ).format(System.currentTimeMillis())

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(currentDateTime, ".jpg", storageDir)
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val imgUri: Uri = FileProvider.getUriForFile(
                this@PostStoryActivity, getString(R.string.package_name), it
            )
            imgPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uriToFile(pickedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myImg = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(pickedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myImg)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myImg
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val pickedImg: Uri = result.data?.data as Uri
            val myImg = uriToFile(pickedImg, this@PostStoryActivity)
            imgFile = myImg
            binding.imgPreview.setImageURI(pickedImg)
            binding.tvDesc.requestFocus()
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val picker = Intent.createChooser(intent, getString(R.string.select_img))
        galleryLauncher.launch(picker)
    }

    private fun showToast(msg: String) {
        Toast.makeText(
            this@PostStoryActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()

        if (msg == getString(R.string.story_created_successfully)) {
            val intent = Intent(this@PostStoryActivity, ListStoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoad: Boolean) {
        binding.progressBarPostStory.visibility = if (isLoad) View.VISIBLE else View.GONE
    }

    companion object {
        const val FILE_DATE_FORMAT = "MMddyyyy"
    }
}