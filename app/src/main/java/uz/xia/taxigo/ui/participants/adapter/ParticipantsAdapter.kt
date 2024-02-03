package uz.xia.taxigo.ui.participants.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.user.UserData
import uz.xia.taxigo.databinding.ItemParticipantsBinding
import uz.xia.taxigo.utils.dateFormat
import uz.xia.taxigo.utils.dateMonthDayFormat

class ParticipantsAdapter(private val listener: IParticipantsListener) :
    ListAdapter<UserData, ParticipantsAdapter.UserVH>(ItemUserDiffer) {
    inner class UserVH(binding: ItemParticipantsBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.tvName
        private val tvPhone = binding.tvPhone
        private val tvDate = binding.tvDate
        private val ivImage = binding.avatarImage
        private var mId: Long = 0L

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(mId)
            }
        }

        fun onBind(model: UserData) {
            mId = model.id
            tvName.text = "${model.firstName} ${model.lastName}"
            tvPhone.text = model.phone
            val date = dateFormat().parse( if(model.createAt.isNullOrEmpty()) "2000-01-01" else  model.createAt)
            tvDate.text =  dateMonthDayFormat().format(date)
            Glide.with(ivImage.context)
                .load(model.image)
                .placeholder(R.drawable.ic_avatar)
                .circleCrop()
                .into(ivImage)
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

private val ItemUserDiffer = object : ItemCallback<UserData>() {
    override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
        return oldItem.id == newItem.id
    }

}

interface IParticipantsListener {
    fun onItemClick(id: Long)
}
