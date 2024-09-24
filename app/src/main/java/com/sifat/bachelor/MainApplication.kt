package com.sifat.bachelor

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import com.google.firebase.messaging.FirebaseMessaging
import com.sifat.bachelor.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication: Application() {

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        SessionManager.init(applicationContext)
        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(appModule))
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.d(task.exception)
            } else {
                val token = task.result
                SessionManager.firebaseToken = token
                Timber.d("FirebaseToken:\n$token")
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic("Bachelor")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to topic"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Timber.d(msg)
            }

        SessionManager.deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
}