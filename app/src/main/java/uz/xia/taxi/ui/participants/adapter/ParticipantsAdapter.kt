package uz.xia.taxi.ui.participants.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxi.databinding.ItemParticipantsBinding
import uz.xia.taxi.utils.websocket.model.User

class ParticipantsAdapter(private val listener: IParticipantsListener) :
    ListAdapter<User, ParticipantsAdapter.UserVH>(ItemUserDiffer) {
    inner class UserVH(binding: ItemParticipantsBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.textView

        init {
            binding.root.setOnClickListener {
                listener.onItemClick()
            }
        }

        fun onBind(model: User) {
            tvName.text = model.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemParticipantsBinding.inflate(layoutInflater, parent, false)
        return UserVH(binding)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        holder.onBind(getItem(position))
    }
}

private val ItemUserDiffer = object : ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

}

interface IParticipantsListener {
    fun onItemClick()
}
