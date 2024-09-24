package com.sifat.bachelor

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun getCurrentMonth(): String {
    val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
    return dateFormat.format(Date())
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.toast(msg: String?, time: Int = Toast.LENGTH_SHORT) {
    if (!msg.isNullOrEmpty())
    Toast.makeText(this, msg, time).show()
}

fun Fragment.alert(title: CharSequence? = null, message: CharSequence? = null, showCancel: Boolean = false, positiveButtonText: String = "ঠিক আছে", negativeButtonText: String = "ক্যানসেল", listener: ((type: Int) -> Unit)? = null): AlertDialog {

    val builder = MaterialAlertDialogBuilder(requireContext())
    builder.setTitle(title)
    // Display a message on alert dialog
    builder.setMessage(message)
    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton(positiveButtonText) { dialog, which ->
        dialog.dismiss()
        listener?.invoke(AlertDialog.BUTTON_POSITIVE)
    }
    // Display a negative button on alert dialog
    if (showCancel) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
            listener?.invoke(AlertDialog.BUTTON_NEGATIVE)
        }
    }

    val dialog = builder.create()
    val typeface = ResourcesCompat.getFont(requireContext(), R.font.solaiman)
    val textView = dialog.findViewById<TextView>(android.R.id.message)
    textView?.typeface = typeface
    return dialog
}


fun Activity.alert(title: CharSequence? = null, message: CharSequence? = null, showCancel: Boolean = false, positiveButtonText: String = "ঠিক আছে", negativeButtonText: String = "ক্যানসেল", listener: ((type: Int) -> Unit)? = null): AlertDialog {

    val builder = MaterialAlertDialogBuilder(this)
    builder.setTitle(title)
    // Display a message on alert dialog
    builder.setMessage(message)
    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton(positiveButtonText) { dialog, which ->
        dialog.dismiss()
        listener?.invoke(AlertDialog.BUTTON_POSITIVE)
    }
    // Display a negative button on alert dialog
    if (showCancel) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
            listener?.invoke(AlertDialog.BUTTON_NEGATIVE)
        }
    }

    val dialog = builder.create()
    val typeface = ResourcesCompat.getFont(this, R.font.solaiman)
    val textView = dialog.findViewById<TextView>(android.R.id.message)
    textView?.typeface = typeface
    return dialog
}


fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_LONG){
    Snackbar.make(this, message, length).also { snackbar ->
        snackbar.setAction("Cancel") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_INDEFINITE, actionName: String, onClick: ((view: View) -> Unit)? = null): Snackbar {
    return Snackbar.make(this, message, length).also { snackbar ->
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
        snackbar.setActionTextColor(Color.YELLOW)
        snackbar.setAction(actionName) {
            onClick?.invoke(it)
            snackbar.dismiss()
        }
    }
}

fun Context.isConnectedToNetwork(): Boolean {
    var isConnected = false
    val connectivityManager = this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager?.let {
            val networkCapabilities = it.activeNetwork ?: return false
            val activeNetwork = it.getNetworkCapabilities(networkCapabilities) ?: return false
            isConnected = activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            /*activeNetwork.apply {
                isConnected = when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }*/
        }
    } else {
        isConnected = connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting == true
    }
    return isConnected
}

fun Fragment.progressDialog(message: String = getString(R.string.wait)): ProgressDialog {

    val dialog = ProgressDialog(requireContext())
    with(dialog) {
        setMessage(message)
    }
    return dialog
}

val <T> T.exhaustive: T
    get() = this

fun Number.spToPx(context: Context) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, this.toFloat(), context.resources.displayMetrics).toInt()

fun isPackageInstalled(packageManager: PackageManager, packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: Exception) {
        false
    }
}

fun isValidCoordinate(coordinate: String?): Boolean {
    if (coordinate.isNullOrEmpty()) return false
    if (coordinate.trim().isEmpty()) return false
    if (coordinate == "0.0") return false
    if (coordinate == "0") return false
    return true
}

fun Bundle.bundleToString(): String {
    return this.keySet().joinToString(", ", "{", "}") { key ->
        "$key=${this[key]}"
    }
}

fun Activity.appVersion(): String {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun Fragment.appVersion(): String {
    return try {
        val pInfo: PackageInfo? = this.context?.packageManager?.getPackageInfo(this.context?.packageName ?: "", 0)
        pInfo?.versionName ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun Activity.appVersionCode(): Int {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionCode
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun Fragment.appVersionCode(): Int {
    return try {
        val pInfo: PackageInfo? = this.context?.packageManager?.getPackageInfo(this.context?.packageName ?: "", 0)
        pInfo?.versionCode ?: 0
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun isValidDTCode(code: String): Boolean {
    return code.matches("^DT-[\\d]+\$".toRegex())
}