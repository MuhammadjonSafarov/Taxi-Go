package uz.xia.taxigo.ui.add_data.parking.edit.adapter

import android.view.View
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.databinding.ItemLayoutRoadBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class RoadsAdapter(val listener: IRoadListener) : AppListAdapter<RoadData>(
    R.layout.item_layout_road,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {
    override fun bind(item: RoadData, view: View, adapterPosition: Int) {
        val binding=ItemLayoutRoadBinding.bind(view)
        binding.name.text = item.name
        binding.destination.text = "${item.destination}"
        binding.imageDelete.setOnClickListener {
            listener.onDelete(item.id)
        }
    }
}

interface IRoadListener {
    fun onDelete(id: Long)
}