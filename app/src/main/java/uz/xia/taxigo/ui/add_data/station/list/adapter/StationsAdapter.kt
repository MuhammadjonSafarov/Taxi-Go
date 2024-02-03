package uz.xia.taxigo.ui.add_data.station.list.adapter

import android.view.View
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.StationData
import uz.xia.taxigo.data.local.remote.model.StationWithRegions
import uz.xia.taxigo.databinding.ItemLayoutStationBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class StationsAdapter(private val listener: Callback) : AppListAdapter<StationWithRegions>(
    R.layout.item_layout_station,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {

    override fun bind(item: StationWithRegions, view: View, adapterPosition: Int) {
        val binding = ItemLayoutStationBinding.bind(view)
        binding.name.text = item.stationData.nameUzLt
        binding.destination.text = "${item.stationData.latitude}\n${item.stationData.longitude}"
        binding.description.text = item.districtData.nameUzLt
        binding.root.setOnClickListener {
            listener.itemClicked(item.stationData)
        }
        binding.root.setOnLongClickListener{
            listener.itemLongClicked(item.stationData)
            true
        }
    }

    interface Callback {
        fun itemClicked(item: StationData)
        fun itemLongClicked(item: StationData)
    }
}