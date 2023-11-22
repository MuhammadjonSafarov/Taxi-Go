package uz.xia.taxi.ui.participants.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxi.databinding.ChatItemLeftBinding
import uz.xia.taxi.databinding.ChatItemRightBinding
import uz.xia.taxi.utils.OnSwipeTouchListener
import uz.xia.taxi.utils.websocket.model.Chat

private const val MSG_TYPE_LEFT = 0
private const val MSG_TYPE_RIGHT = 1

class ChatMessageAdapter(private val currentUserId: String) :
    ListAdapter<Chat, RecyclerView.ViewHolder>(ItemChatDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == MSG_TYPE_LEFT) {
            val binding = ChatItemLeftBinding.inflate(layoutInflater, parent, false)
            return TargetMessageVH(binding)
        } else {
            val binding = ChatItemRightBinding.inflate(layoutInflater, parent, false)
            return CurrentMessageVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = getItem(position)
        if (holder is TargetMessageVH) {
            holder.onBind(model)
        } else if (holder is CurrentMessageVH) {
            holder.onBind(model)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).sender == currentUserId) {
            return MSG_TYPE_RIGHT
        } else {
            return MSG_TYPE_LEFT
        }
    }

    class TargetMessageVH(binding: ChatItemLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvMessage = binding.textMessageBody
        private val tvTime = binding.textMessageTime

        fun onBind(chat: Chat) {
            tvMessage.text = chat.content
            tvTime.text="12:00"
        }
    }

    class CurrentMessageVH(binding: ChatItemRightBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvMessage = binding.textMessageBody
        fun onBind(chat: Chat) {
            tvMessage.text = chat.content
        }
    }
}

private val ItemChatDiffer = object : ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.id == newItem.id
    }
}
