package com.sifat.bachelor.login

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.sifat.bachelor.R
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentLoginBinding
import com.sifat.bachelor.hideKeyboard
import com.sifat.bachelor.toast

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    private fun loginUser(mobileNumber: String, inputPassword: String) {

        firestore.collection("users").document(mobileNumber)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val storedPassword = document.getString("password")
                    val email = document.getString("email")
                    val name = document.getString("name")

                    if (storedPassword == inputPassword) {
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()

                        val list = listOf(mobileNumber, name)
                        SessionManager.createSession(list)
                        if (activity != null) {
                            (activity as LoginActivity).goToHome()
                        }
                        updateFcmToken(mobileNumber)

                    } else {
                        Toast.makeText(context, "Invalid password!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "User does not exist!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Update the FCM token when user logs in
    private fun updateFcmToken(mobileNumber: String) {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result
                val updates = hashMapOf<String, Any>(
                    "fcmToken" to fcmToken
                )

                firestore.collection("users").document(mobileNumber)
                    .update(updates)
                    .addOnSuccessListener {
                        Log.d(TAG, "FCM token updated successfully.")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error updating FCM token: ${e.message}")
                    }
            } else {
                Log.e(TAG, "Failed to get FCM token")
            }
        }
    }


    private fun onLoginButtonClick() {
        val mobileNumber = binding.etUserId.text.toString()
        val password = binding.etLoginPassword.text.toString()

        if (mobileNumber.isNotEmpty() && password.isNotEmpty()) {
            loginUser(mobileNumber, password)
        } else {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            onLoginButtonClick()
        }
        binding?.registration?.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.nav_registration)
        }
    }
}

/*class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private val viewModel: LoginViewModel by inject()

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
        }else{
            viewModel.userLogin().observe(viewLifecycleOwner, Observer { model->
                var matched: Boolean = false
                if (model.isNotEmpty() ){
                    model.forEach { list->
                        if (list.first() == userId && list.last() == password){
                            SessionManager.createSession(list)
                            matched = true
                        }
                    }
                    if (matched){
                        if (activity != null) {
                            (activity as LoginActivity).goToHome()
                        }
                    }else{
                        context?.toast("User আইডি অথবা পাসওয়ার্ড সঠিক নয়")
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}*/
