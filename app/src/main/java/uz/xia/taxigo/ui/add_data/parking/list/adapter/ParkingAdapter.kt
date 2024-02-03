package uz.xia.taxigo.ui.add_data.parking.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxigo.data.local.remote.model.ParkingWithRegions
import uz.xia.taxigo.databinding.ItemLayoutParkingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ParkingAdapter(private val listener: IParkingListener) : ListAdapter<ParkingWithRegions, ParkingAdapter.ParkingVH>(itemParkingDiffer) {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val formatClock = SimpleDateFormat("HH:mm", Locale.getDefault())

    inner class ParkingVH(binding: ItemLayoutParkingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.name
        private val tvDestination = binding.destination
        private val tvDescription = binding.description
        private var mId:Long=0L
        init {
            binding.root.setOnClickListener{
                listener.onParkItemClick(mId)
            }
        }

        fun onBind(it: ParkingWithRegions) {
            mId=it.parkingData.id
            tvName.text = it.parkingData.nameUzLt
            tvDescription.text = "${it.districtData.nameUzLt}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLayoutParkingBinding.inflate(layoutInflater, parent, false)
        return ParkingVH(binding)
    }

    override fun onBindViewHolder(holder: ParkingVH, position: Int) {
        holder.onBind(getItem(position))
    }
    interface IParkingListener{
        fun onParkItemClick(id:Long)
    }
}

private val itemParkingDiffer = object : ItemCallback<ParkingWithRegions>() {
    override fun areItemsTheSame(oldItem: ParkingWithRegions, newItem: ParkingWithRegions): Boolean {
        return oldItem.parkingData.id == newItem.parkingData.id
    }

    override fun areContentsTheSame(oldItem: ParkingWithRegions, newItem: ParkingWithRegions): Boolean {
        return oldItem == newItem
    }

}