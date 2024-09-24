package com.sifat.bachelor.notification

import android.view.LayoutInflater
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.api.model.Message
import com.sifat.bachelor.api.model.NotificationData
import com.sifat.bachelor.api.model.Notifications
import com.sifat.bachelor.databinding.FragmentNotificationDashboardBinding
import com.sifat.bachelor.hideKeyboard
import com.sifat.bachelor.home.HomeViewModel
import com.sifat.bachelor.toast
import org.koin.android.ext.android.inject

class NotificationDashboardFragment : Fragment() {
    private var binding: FragmentNotificationDashboardBinding? = null

    private val viewModel: HomeViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentNotificationDashboardBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnsendPush?.setOnClickListener {
            if (binding?.messageTV?.text?.isNotEmpty() == true){
                hideKeyboard()
                sendNotifications()
            }else{
                context?.toast("Please Write something")
            }
        }

    }

    private fun sendNotifications() {

        val multiCastRequest = NotificationData(
            message = Message(
                topic = "Bachelor",
                notification = Notifications(
                    title = "From ${SessionManager.userName}",
                    body = binding?.messageTV?.text?.toString() ?: ""
                ),
                data = mapOf("51A10" to "Home")
            )
        )

        viewModel.sentPush(multiCastRequest).observe(viewLifecycleOwner, Observer { response ->
            findNavController().popBackStack()
        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
