package com.sifat.bachelor.home

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fondesa.kpermissions.allDenied
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.allPermanentlyDenied
import com.fondesa.kpermissions.anyDenied
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sifat.bachelor.R
import com.sifat.bachelor.databinding.ActivityHomeBinding
import com.sifat.bachelor.login.LoginActivity
import timber.log.Timber

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBar.toolbar)

        navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_dashboard), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigationView.setupWithNavController(navController)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsBuilder(Manifest.permission.POST_NOTIFICATIONS).build()
                    .send { result ->

                        when {

                            result.allGranted() -> {

                            }

                            result.allDenied() || result.anyDenied() -> {
                                openSettingsDialog()
                            }

                            result.allPermanentlyDenied() || result.anyPermanentlyDenied() -> {
                                openSettingsDialog()
                            }

                        }

                    }
            }

        }



    }

    private fun openSettingsDialog() {
        try {
            if (applicationContext != null) {
                val dialog = MaterialAlertDialogBuilder(applicationContext).create()

                dialog.apply {
                    setTitle("Enable Notification Permission")
                    setMessage("Please allow Notification permission to receive latest updates.")
                    setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { _, _ ->
                        dismiss()
                    }
                    setButton(DialogInterface.BUTTON_POSITIVE, "Go to Settings") { _, _ ->
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        startActivity(intent)
                        dismiss()
                    }
                    show()
                }
            }
        } catch (e: Exception) {
            Timber.d("$e")
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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