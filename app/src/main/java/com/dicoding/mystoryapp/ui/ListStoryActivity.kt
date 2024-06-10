package com.dicoding.mystoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.adapt.ListStoryAdapt
import com.dicoding.mystoryapp.adapt.LoadingStateAdapter
import com.dicoding.mystoryapp.databinding.ActivityListStoryBinding
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.ListStoryVM
import com.dicoding.mystoryapp.viewmodel.ListVMFactory
import com.dicoding.mystoryapp.viewmodel.UserVMFactory


class ListStoryActivity : AppCompatActivity() {
    private val userPref by lazy { UserPreferencesManager.getInstance(dataStore) }
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var token: String
    private val listStoryVM: ListStoryVM by lazy {
        ViewModelProvider(this, ListVMFactory(this))[ListStoryVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setCustomView(R.layout.custom_toolbar)
        }
        handleUserActions()

        val layout = LinearLayoutManager(this)
        binding.rvStoryList.layoutManager = layout
        val storyDecoration = DividerItemDecoration(this, layout.orientation)
        binding.rvStoryList.addItemDecoration(storyDecoration)

        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]
        authSplashVM.getToken().observe(this) {
            token = it
            setUserStory(it)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setUserStory(token: String) {

        val adapter = ListStoryAdapt()
        binding.rvStoryList.adapter = adapter.withLoadStateFooter(footer = LoadingStateAdapter {
            adapter.retry()
        })

        listStoryVM.getPagingStories(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : ListStoryAdapt.OnItemClickCallback {
            override fun onItemClicked(detail: DetailPostStory) {
                viewUserDetail(detail)
            }
        })
    }

    private fun viewUserDetail(data: DetailPostStory) {
        val intent = Intent(this, DetailStoryActivity::class.java)
        intent.putExtra(DetailStoryActivity.STORY_DATA, data)
        startActivity(intent)
    }

    private fun handleUserActions() {
        binding.floatingBtn.setOnClickListener {
            startActivity(Intent(this, PostStoryActivity::class.java))
        }

    }

    private fun logout() {
        val authVM = ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]
        authVM.resetLoginData()
        Toast.makeText(this, R.string.logoutSucceed, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mapsNav -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }

            R.id.switchLang -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            R.id.logout -> {
                showAlertDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder.setTitle(getString(R.string.logout)).setMessage(getString(R.string.logoutWarn))
            .setPositiveButton(getString(R.string.logoutNo)) { _, _ ->
                alert.cancel()
            }.setNegativeButton(getString(R.string.logoutYes)) { _, _ ->
                logout()
            }.show()
    }
}