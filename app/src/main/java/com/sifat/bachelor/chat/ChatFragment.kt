package com.sifat.bachelor.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sifat.bachelor.SessionManager
import com.sifat.bachelor.databinding.FragmentChatBinding
import com.sifat.bachelor.toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment() {

    private var binding: FragmentChatBinding? = null
    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()
    private val firestore = FirebaseFirestore.getInstance()
    private val chatCollectionRef = firestore.collection("chats")

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
                sendMessage(message)
                binding?.writeYourMsgEt?.text?.clear()
            }else{
                context?.toast("Please Type Something.")
            }
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
            chatAdapter.notifyDataSetChanged() // Update the RecyclerView
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
                Log.d(TAG, "Message sent successfully.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error sending message: ", e)
            }
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



