package uz.xia.taxigo.ui.add_data.road.list.adapter

import android.view.View
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.DistrictData
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.databinding.ItemLayoutRoadBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class RoadsAdapter(private val listener:Callback): AppListAdapter<RoadData>(
    R.layout.item_layout_road,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {

    override fun bind(item: RoadData, view: View, adapterPosition: Int) {
        val binding = ItemLayoutRoadBinding.bind(view)
        binding.name.text=item.name
        binding.destination.text="${item.destination} km"
        binding.root.setOnClickListener {
            listener.itemClicked(item)
        }
        binding.root.setOnLongClickListener {
            listener.itemLongClicked(item)
            true
        }
    }
    interface Callback {
        fun itemClicked(item: RoadData)
        fun itemLongClicked(item: RoadData)
    }
}