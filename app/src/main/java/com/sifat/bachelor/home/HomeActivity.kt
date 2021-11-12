package com.sifat.bachelor.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sifat.bachelor.R.*
import com.sifat.bachelor.login.LoginActivity
import timber.log.Timber

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_home)
    }

    fun goToLogin() {
        Timber.d("Called")
        val intent = Intent(this, LoginActivity::class.java).apply {
            replaceExtras(this@HomeActivity.intent.extras)
        }
        startActivity(intent)
        finish()
    }
}