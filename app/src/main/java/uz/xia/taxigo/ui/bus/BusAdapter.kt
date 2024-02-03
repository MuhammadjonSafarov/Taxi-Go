package uz.xia.taxigo.ui.bus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.bus.Schedule
import uz.xia.taxigo.databinding.ItemBusScheduleBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BusAdapter : ListAdapter<Schedule, BusAdapter.ScheduleVH>(ItemScheduleDiffer) {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatClock = SimpleDateFormat("HH:mm", Locale.getDefault())

    inner class ScheduleVH(binding: ItemBusScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.tvName
        private val tvDate = binding.tvDate
        private val tvDescription = binding.tvDescription
        private val tvPrice = binding.tvPrice
        fun onBind(it: Schedule) {
            val date = simpleDateFormat.parse(it.departureAt) ?: Date()
            tvName.text = it.nameUz
            tvDate.text = formatClock.format(date)
            val formatter = DecimalFormat("#,###")
            val priceText = formatter.format(it.price).replace(",", " ")
            tvPrice.text = "$priceText ${tvPrice.context.getString(R.string.sum)}"
            val description = when (it.days) {
                is List<*> -> {
                    if (it.days.isEmpty()) {
                        "( Har kuni )"
                    } else {
                        "Haftaning kunlarida"
                    }
                }
                else -> {
                    "Oyning kunlarida"
                }
            }

            tvDescription.text = description
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBusScheduleBinding.inflate(layoutInflater, parent, false)
        return ScheduleVH(binding)
    }

    override fun onBindViewHolder(holder: ScheduleVH, position: Int) {
        holder.onBind(getItem(position))
    }
}

private val ItemScheduleDiffer = object : ItemCallback<Schedule>() {
    override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.id == newItem.id
    }
}
