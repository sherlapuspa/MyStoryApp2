package com.dicoding.mystoryapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.adapt.ListStoryAdapt
import com.dicoding.mystoryapp.databinding.ActivityDetailStoryBinding
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.helper.LocationConverter

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyInfo = intent.getParcelableExtra<DetailPostStory>(STORY_DATA) as DetailPostStory
        displayStoryDetails(storyInfo)

        supportActionBar?.title = getString(R.string.usersStory, storyInfo.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun displayStoryDetails(storyDetails: DetailPostStory) {
        binding.apply {
            tvDescription.text = storyDetails.description
            tvDateTime.text = ListStoryAdapt.formatDateToString(storyDetails.createdAt.toString())
        }
        binding.tvAddress.text = LocationConverter.getStringAddressDetail(
            LocationConverter.toLatlng(storyDetails.lat, storyDetails.lon), this
        )

        Glide.with(this).load(storyDetails.photoUrl).placeholder(R.drawable.mystory)
            .into(binding.imgStoryDetail)
    }

    companion object {
        const val STORY_DATA = "story_data"
    }
}