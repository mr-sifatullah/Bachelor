package com.sifat.bachelor.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sifat.bachelor.databinding.FragmentRegistrationBinding
import com.sifat.bachelor.hideKeyboard
import com.sifat.bachelor.toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.sifat.bachelor.R
import java.util.*
import kotlin.collections.HashMap

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    private fun registerUser(mobileNumber: String, password: String, email: String, name: String) {
        // Fetch the current FCM token
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result

                // Create user data to store in Firestore
                val userData = hashMapOf(
                    "mobileNumber" to mobileNumber,
                    "password" to password,
                    "email" to email,
                    "name" to name,
                    "fcmToken" to fcmToken
                )

                // Save user data to Firestore
                firestore.collection("users").document(mobileNumber)
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "User registered successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Failed to get FCM token", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Example of handling registration button click
    private fun onRegisterButtonClick() {
        val mobileNumber = binding.userMobile.text.toString()
        val password = binding.etLoginPassword.text.toString()
        val email = binding.userEmail.text.toString()
        val name = binding.userName.text.toString()

        if (mobileNumber.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty() && name.isNotEmpty()) {
            registerUser(mobileNumber, password, email, name)
        } else {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.setOnClickListener {
            onRegisterButtonClick()
        }
        binding?.login?.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.nav_login)
        }
    }
}


/*
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

}*/
