package com.sifat.bachelor.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.sifat.bachelor.R
import com.sifat.bachelor.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_login)

        navController = findNavController(R.id.navHostFragment)

    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == navController.graph.startDestination) {
            super.onBackPressed()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000L)
        }
    }

    fun goToHome() {
        Timber.d("Called")
        val intent = Intent(this, HomeActivity::class.java).apply {
            replaceExtras(this@LoginActivity.intent.extras)
        }
        startActivity(intent)
        finish()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is TextInputEditText || view is EditText) {
                if (!isPointInsideView(event.rawX, event.rawY, view)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun isPointInsideView(x: Float, y: Float, view: View): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val viewX = location[0]
        val viewY = location[1]

        // point is inside view bounds
        return ((x > viewX && x < (viewX + view.width)) && (y > viewY && y < (viewY + view.height)))
    }

}
