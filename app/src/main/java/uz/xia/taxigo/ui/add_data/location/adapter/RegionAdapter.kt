package uz.xia.taxigo.ui.add_data.location.adapter

import android.view.View
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.databinding.ItemRegionBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class RegionAdapter(private val listener:Callback) : AppListAdapter<RegionData>(
    R.layout.item_region,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {

    override fun bind(item: RegionData, view: View, adapterPosition: Int) {
        val binding = ItemRegionBinding.bind(view)
        binding.tvDropDownItem.text = item.nameUzLt
        binding.root.setOnClickListener {
            listener.itemClicked(item)
        }
    }
    interface Callback {
        fun itemClicked(item: RegionData)
    }
}