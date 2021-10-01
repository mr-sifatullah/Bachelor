package com.sifat.bachelor.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sifat.bachelor.R
import com.sifat.bachelor.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var binding: FragmentSplashBinding? = null
    private var isTimeOut: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private var callback: Runnable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentSplashBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback = Runnable {
            isTimeOut = true
            goToDestination()
        }
        handler.postDelayed(callback!!,2000L)
    }

    override fun onResume() {
        super.onResume()
        if (isTimeOut) {
            goToDestination()
        }
    }

    override fun onStop() {
        super.onStop()
        callback?.let {
            isTimeOut = true
            handler.removeCallbacks(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun goToDestination() {
     /*   if (SessionManager.isLogin) {
            if (activity != null) {
                (activity as LoginActivity).goToHome()
            }
        } else {
            findNavController().navigate(R.id.action_splash_login)
        }*/
        findNavController().navigate(R.id.action_splash_login)
    }

}