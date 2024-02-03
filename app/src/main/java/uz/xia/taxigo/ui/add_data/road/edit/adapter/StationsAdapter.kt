package uz.xia.taxigo.ui.add_data.road.edit.adapter

import android.view.View
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.RoadData
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.data.local.remote.model.StationWithRegions
import uz.xia.taxigo.databinding.ItemLayoutRoadBinding
import uz.xia.taxigo.databinding.ItemLayoutStationBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class StationsAdapter(private val listener:ICallback): AppListAdapter<StationWithRegions>(
    R.layout.item_layout_station,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {

    override fun bind(item: StationWithRegions, view: View, adapterPosition: Int) {
        val binding = ItemLayoutStationBinding.bind(view)
        binding.name.text=item.stationData.nameUzLt
        binding.destination.text="${item.stationData.latitude}\n${item.stationData.longitude}"
        binding.description.text="${item.districtData.nameUzLt}"
        binding.root.setOnLongClickListener {
            listener.itemRemove(item.stationData.id)
            true
        }

    }
    interface ICallback {
        fun itemRemove(itemId: Long)
        //fun itemLongClicked(item: RoadData)
    }
}