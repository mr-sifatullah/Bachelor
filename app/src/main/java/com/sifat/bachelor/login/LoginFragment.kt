package com.sifat.bachelor.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BuildConfig
import androidx.navigation.fragment.findNavController
import com.sifat.bachelor.R
import com.sifat.bachelor.databinding.FragmentLoginBinding
import com.sifat.bachelor.hideKeyboard
import com.sifat.bachelor.toast

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null

    companion object {
        fun newInstance() : LoginFragment = LoginFragment().apply{}
        val tag: String = LoginFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentLoginBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (BuildConfig.DEBUG) {
            binding?.etUserId?.setText("admin")
            binding?.etLoginPassword?.setText("123456")
        }

        binding?.loginBtn?.setOnClickListener {
            login()
        }

        binding?.registration?.setOnClickListener {
            findNavController().navigate(R.id.nav_registration)
        }
    }

    private fun login() {
        hideKeyboard()
        val userId = binding?.etUserId?.text.toString().trim()
        val password =  binding?.etLoginPassword?.text.toString().trim()
        if (userId.isEmpty() || password.isEmpty()){
            context?.toast("User আইডি অথবা পাসওয়ার্ড দিন")
        }
        if (userId == "admin" && password == "1234"){
            goToHomeFragment()
        } else{
            context?.toast("User আইডি অথবা পাসওয়ার্ড সঠিক নয়")
        }
    }

    private fun goToHomeFragment(){
        findNavController().navigate(R.id.home_fragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}