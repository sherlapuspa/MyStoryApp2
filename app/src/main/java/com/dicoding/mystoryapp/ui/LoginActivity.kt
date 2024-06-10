package com.dicoding.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.mystoryapp.data.LoginUserData
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.ListStoryVM
import com.dicoding.mystoryapp.viewmodel.ListVMFactory
import com.dicoding.mystoryapp.viewmodel.UserVMFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginVM: ListStoryVM by lazy {
        ViewModelProvider(this, ListVMFactory(this))[ListStoryVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.loginBar)
        handleUserActions()
        playAnimation()

        val userPref = UserPreferencesManager.getInstance(dataStore)
        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]

        authSplashVM.getLoginStatus().observe(this) { statusTrue ->
            if (statusTrue) {
                val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        loginVM.isLoggingin.observe(this) { message ->
            handleLoginResponse(
                message, authSplashVM
            )
        }

        loginVM.isLoad.observe(this) {
            showLoading(it)
        }
    }

    private fun handleLoginResponse(
        message: String, authSplashVM: AuthSplashVM
    ) {
        if (message.contains("Welcome")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val authUser = loginVM.userlogin.value
            authSplashVM.storeLoginStatus(true)
            authSplashVM.storeToken(authUser?.loginResult!!.token)
            authSplashVM.storeName(authUser.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.tvLoginDescription, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 7000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val tvLoginDescriptionAnn =
            ObjectAnimator.ofFloat(binding.tvLoginDescription, View.ALPHA, 1f).setDuration(400)
        val iconAnn = ObjectAnimator.ofFloat(binding.loginIcon, View.ALPHA, 1f).setDuration(400)
        val tellAnn = ObjectAnimator.ofFloat(binding.tellYourStory, View.ALPHA, 1f).setDuration(400)
        val emailAnn = ObjectAnimator.ofFloat(binding.CVEmail, View.ALPHA, 1f).setDuration(400)
        val passAnn = ObjectAnimator.ofFloat(binding.PasswordLogin, View.ALPHA, 1f).setDuration(400)
        val visibleAnn =
            ObjectAnimator.ofFloat(binding.vissiblePass, View.ALPHA, 1f).setDuration(400)
        val btnLoginAnn = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(400)
        val descRegistAnn =
            ObjectAnimator.ofFloat(binding.tvRegistDescription, View.ALPHA, 1f).setDuration(400)
        val btnRegistAnn =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(
                tvLoginDescriptionAnn,
                iconAnn,
                tellAnn,
                emailAnn,
                passAnn,
                visibleAnn,
                btnLoginAnn,
                descRegistAnn,
                btnRegistAnn
            )
            start()
        }
    }

    private fun handleUserActions() {
        binding.btnLogin.setOnClickListener {
            binding.CVEmail.clearFocus()
            binding.PasswordLogin.clearFocus()

            if (isDataTrue()) {
                val requestLogin = LoginUserData(
                    binding.CVEmail.text.toString().trim(),
                    binding.PasswordLogin.text.toString().trim()
                )
                loginVM.login(requestLogin)
            } else {
                if (!binding.CVEmail.emailMatched) binding.CVEmail.error =
                    getString(R.string.nullEmail)
                if (!binding.PasswordLogin.passMatched) binding.PasswordLogin.error =
                    getString(R.string.nullPass)

                Toast.makeText(this, R.string.failedLogin, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegistActivity::class.java)
            startActivity(intent)
        }

        binding.vissiblePass.setOnCheckedChangeListener { _, isChecked ->
            binding.PasswordLogin.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.PasswordLogin.text?.let { binding.PasswordLogin.setSelection(it.length) }
        }
    }

    private fun isDataTrue(): Boolean {
        return binding.CVEmail.emailMatched && binding.PasswordLogin.passMatched
    }

    private fun showLoading(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
    }
}
