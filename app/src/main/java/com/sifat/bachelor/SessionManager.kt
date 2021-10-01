package com.sifat.bachelor

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.core.content.edit

object SessionManager {

    private const val prefName = "${BuildConfig.APPLICATION_ID}.session"
    private lateinit var pref: SharedPreferences

    fun init(@NonNull context: Context) {
        pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }



    var isLogin: Boolean
        get() {
            return pref.getBoolean("isLogin", false)
        }
        set(value) {
            pref.edit {
                putBoolean("isLogin", value)
            }
        }

    var userId: Int
        get() {
            return pref.getInt("userId", 0)
        }
        set(value) {
            pref.edit {
                putInt("userId", value)
            }
        }


    var userName: String
        get() {
            return pref.getString("username", "")!!
        }
        set(value) {
            pref.edit {
                putString("username", value)
            }
        }

    var userFullName: String
        get() {
            return pref.getString("fullName", "")!!
        }
        set(value) {
            pref.edit {
                putString("fullName", value)
            }
        }

    var mobile: String
        get() {
            return pref.getString("mobile", "")!!
        }
        set(value) {
            pref.edit {
                putString("mobile", value)
            }
        }

    var email: String
        get() {
            return pref.getString("email", "")!!
        }
        set(value) {
            pref.edit {
                putString("email", value)
            }
        }

    var gender: String
        get() {
            return pref.getString("gender", "")!!
        }
        set(value) {
            pref.edit {
                putString("gender", value)
            }
        }

    var address: String
        get() {
            return pref.getString("address", "")!!
        }
        set(value) {
            pref.edit {
                putString("address", value)
            }
        }

    var blood: String
        get() {
            return pref.getString("blood", "")!!
        }
        set(value) {
            pref.edit {
                putString("blood", value)
            }
        }

    fun clearSession() {
        pref.edit {
            clear()
        }
    }

    var profileImage: String
        get() {
            return pref.getString("profileImage", "https://static.ajkerdeal.com/images/admin_users/$userId.jpg")!!
        }
        set(value) {
            pref.edit {
                putString("profileImage", value)
            }
        }

    var firebaseToken: String
        get() {
            return pref.getString("firebaseToken", "")!!
        }
        set(value) {
            pref.edit {
                putString("firebaseToken", value)
            }
        }

    var deviceId: String
        get() {
            return pref.getString("deviceId", "")!!
        }
        set(value) {
            pref.edit {
                putString("deviceId", value)
            }
        }

    var latitude: String
        get() {
            return pref.getString("latitude", "0.0")!!
        }
        set(value) {
            pref.edit {
                putString("latitude", value)
            }
        }

    var longitude: String
        get() {
            return pref.getString("longitude", "0.0")!!
        }
        set(value) {
            pref.edit {
                putString("longitude", value)
            }
        }

    var locationAddress: String
        get() {
            return pref.getString("locationAddress", "")!!
        }
        set(value) {
            pref.edit {
                putString("locationAddress", value)
            }
        }

    var isRemainder: Boolean
        get() {
            return pref.getBoolean("isRemainder", false)
        }
        set(value) {
            pref.edit {
                putBoolean("isRemainder", value)
            }
        }

    var remainderTime: String
        get() {
            return pref.getString("remainderTime", "")!!
        }
        set(value) {
            pref.edit {
                putString("remainderTime", value)
            }
        }

    var sessionId: String
        get() {
            return pref.getString("sessionId", "0")!!
        }
        set(value) {
            pref.edit {
                putString("sessionId", value)
            }
        }
}