package com.sifat.bachelor.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sifat.bachelor.databinding.FragmentRegistrationBinding
import com.sifat.bachelor.hideKeyboard
import com.sifat.bachelor.toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class RegistrationFragment : Fragment() {

    private var binding: FragmentRegistrationBinding? = null

    companion object {
        fun newInstance() : RegistrationFragment = RegistrationFragment().apply{}
        val tag: String = RegistrationFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentRegistrationBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.registerBtn?.setOnClickListener {
            login()
        }
    }

    private fun login() {
        hideKeyboard()
        val userName = binding?.userName?.text.toString().trim()
        val userMobile = binding?.userMobile?.text.toString().trim()
        val userEmail = binding?.userEmail?.text.toString().trim()
        val etLoginPassword =  binding?.etLoginPassword?.text.toString().trim()
        if (userName.isEmpty() || userMobile.isEmpty() || userEmail.isEmpty() || etLoginPassword.isEmpty() || etLoginPassword.isEmpty()){
            context?.toast("Please Fill All The Info.")
        }else{
            val db = FirebaseFirestore.getInstance()
            val user: MutableMap<String, Any> = HashMap()
            user["password"] = etLoginPassword
            user["userEmail"] = userEmail
            user["userId"] = userMobile
            user["userMobile"] = userMobile
            user["userName"] = userName

            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    context?.toast("Registration Successful")
                }
                .addOnFailureListener {
                    context?.toast("Something Went Wrong $it")
                }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}