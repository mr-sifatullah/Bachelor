package com.sifat.bachelor.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sifat.bachelor.R
import com.sifat.bachelor.SessionManager

class ChatAdapter(private val messages: List<ChatMessage>, private val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_chat_message, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_others_chat_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = messages[position]

        if (holder is SentMessageViewHolder) {
            holder.rightMessageText.text = chatMessage.message
            //holder.seenStatus.text = if (chatMessage.seenStatus) "Seen" else "Unseen"
            holder.messageTime.text = chatMessage.time
            holder.userName.text = chatMessage.senderName
        } else if (holder is ReceivedMessageViewHolder) {
            holder.leftMessageText.text = chatMessage.message
            holder.messageTime.text = chatMessage.time
            holder.othersUserName.text = chatMessage.senderName
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rightMessageText: TextView = itemView.findViewById(R.id.userMessage)
        val messageTime: TextView = itemView.findViewById(R.id.userMsgDate)
        val userName: TextView = itemView.findViewById(R.id.userName)
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftMessageText: TextView = itemView.findViewById(R.id.othersMessage)
        val messageTime: TextView = itemView.findViewById(R.id.othersMsgDate)
        val othersUserName: TextView = itemView.findViewById(R.id.othersUserName)
    }
}
