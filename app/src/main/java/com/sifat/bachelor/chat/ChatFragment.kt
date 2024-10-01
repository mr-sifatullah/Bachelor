package com.sifat.bachelor.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.api.model.Message
import com.sifat.bachelor.api.model.NotificationData
import com.sifat.bachelor.api.model.Notifications
import com.sifat.bachelor.databinding.FragmentChatBinding
import com.sifat.bachelor.hideKeyboard
import com.sifat.bachelor.home.HomeViewModel
import com.sifat.bachelor.toast
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment() {

    private var binding: FragmentChatBinding? = null
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()
    private val firestore = FirebaseFirestore.getInstance()
    private val chatCollectionRef = firestore.collection("chats")

    private val viewModel: HomeViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatAdapter(messageList, SessionManager.userId)

        binding?.recyclerViewChat?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        loadMessages()


        binding?.btnSend?.setOnClickListener {
            val message = binding?.writeYourMsgEt?.text.toString()
            if (message.isNotEmpty()) {
                hideKeyboard()
                sendMessage(message)
                binding?.writeYourMsgEt?.text?.clear()
                binding?.recyclerViewChat?.smoothScrollToPosition(messageList.size - 1)
            }else{
                context?.toast("Please Type Something.")
            }
        }

        binding?.writeYourMsgEt?.setOnFocusChangeListener { _, hasFocus ->
            binding?.recyclerViewChat?.smoothScrollToPosition(messageList.size - 1)
        }
    }

    private fun loadMessages() {
        chatCollectionRef.orderBy("timestamp").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            messageList.clear() // Clear existing messages
            snapshot?.documents?.forEach { document ->
                val chatMessage = document.toObject(ChatMessage::class.java)
                if (chatMessage != null) {
                    messageList.add(chatMessage)
                }
            }
            chatAdapter.notifyDataSetChanged()
            binding?.recyclerViewChat?.smoothScrollToPosition(messageList.size - 1)
        }
    }

    private fun sendMessage(message: String) {
        val chatMessage = ChatMessage(
            senderId = SessionManager.userId, // Ensure you use the sender ID
            senderName = SessionManager.userName, // Sender name
            message = message,
            time = getCurrentTime(),
            seenStatus = false,
            timestamp = FieldValue.serverTimestamp() // Timestamp for Firestore
        )

        // Add message to Firestore
        chatCollectionRef.add(chatMessage)
            .addOnSuccessListener {
                sendNotifications(message)
                Log.d(TAG, "Message sent successfully.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error sending message: ", e)
            }
    }

    private fun sendNotifications(message: String) {

        val multiCastRequest = NotificationData(
            message = Message(
                topic = "Bachelor",
                notification = Notifications(
                    title = message,
                    body = "${SessionManager.userName},  ${getCurrentTime()}"
                ),
                data = mapOf("51A10" to "Home")
            )
        )

        viewModel.sentPush(multiCastRequest).observe(viewLifecycleOwner, Observer { response ->

        })
    }


    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // Clear the binding reference
    }

    companion object {
        private const val TAG = "ChatFragment"
    }
}



