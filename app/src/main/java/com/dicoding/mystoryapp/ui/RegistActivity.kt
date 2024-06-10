package com.dicoding.mystoryapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.databinding.ActivityRegistBinding
import com.dicoding.mystoryapp.data.LoginUserData
import com.dicoding.mystoryapp.data.RegisterUserData
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.ListStoryVM
import com.dicoding.mystoryapp.viewmodel.ListVMFactory
import com.dicoding.mystoryapp.viewmodel.UserVMFactory

class RegistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistBinding


    private val loginVM: ListStoryVM by lazy {
        ViewModelProvider(this, ListVMFactory(this))[ListStoryVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.signupBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        handleUserActions()

        val userPref = UserPreferencesManager.getInstance(dataStore)
        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]
        authSplashVM.getLoginStatus().observe(this) { statusTrue ->
            if (statusTrue) {
                val intent = Intent(this@RegistActivity, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        loginVM.isRegistering.observe(this) { registerStatus ->
            handleRegisterResponse(
                registerStatus
            )
        }

        loginVM.isLoad.observe(this) {
            showLoading(it)
        }

        loginVM.isLoggingin.observe(this) { loginMessage ->
            handleLoginResponse(
                loginMessage, authSplashVM
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

    private fun handleRegisterResponse(
        message: String,
    ) {
        if (message.contains("Registration successful")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val authenticatedUser = LoginUserData(
                binding.EmailRegistration.text.toString(), binding.PassRegistration.text.toString()
            )
            loginVM.login(authenticatedUser)
        } else {
            if (message.contains("The email is already registered")) {
                binding.EmailRegistration.setErrorMessage(
                    resources.getString(R.string.emailIsInUse),
                    binding.EmailRegistration.text.toString()
                )
                Toast.makeText(this, resources.getString(R.string.emailIsInUse), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleUserActions() {
        binding.seeRegistPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.PassRegistration.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.confirmPassRegistration.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.PassRegistration.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.confirmPassRegistration.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }

            binding.PassRegistration.text?.let { binding.PassRegistration.setSelection(it.length) }
            binding.confirmPassRegistration.text?.let {
                binding.confirmPassRegistration.setSelection(
                    it.length
                )
            }
        }

        binding.registrationBtn.setOnClickListener {
            binding.apply {
                NameRegistration.clearFocus()
                EmailRegistration.clearFocus()
                PassRegistration.clearFocus()
                confirmPassRegistration.clearFocus()
            }

            if (binding.NameRegistration.nameMatched && binding.EmailRegistration.emailMatched && binding.PassRegistration.passMatched && binding.confirmPassRegistration.confirmPassMatched) {
                val registerUserData = RegisterUserData(
                    name = binding.NameRegistration.text.toString().trim(),
                    email = binding.EmailRegistration.text.toString().trim(),
                    password = binding.PassRegistration.text.toString().trim()
                )

                loginVM.register(registerUserData)
            } else {
                if (!binding.NameRegistration.nameMatched) binding.NameRegistration.error =
                    resources.getString(R.string.nullName)
                if (!binding.EmailRegistration.emailMatched) binding.EmailRegistration.error =
                    resources.getString(R.string.nullEmail)
                if (!binding.PassRegistration.passMatched) binding.PassRegistration.error =
                    resources.getString(R.string.nullPass)
                if (!binding.confirmPassRegistration.confirmPassMatched) binding.confirmPassRegistration.error =
                    resources.getString(R.string.nullConfirmPass)

                Toast.makeText(this, R.string.failedLogin, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoad: Boolean) {
        binding.progressBar2.visibility = if (isLoad) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}