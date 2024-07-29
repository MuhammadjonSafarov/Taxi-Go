package uz.xia.taxigo.ui.driver.home.dialog.adapter

import android.view.View
import androidx.core.view.isVisible
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.ItemDriverToolsBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class DriverCarToolsAdapter:AppListAdapter<String>(
    R.layout.item_driver_tools,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
){
    override fun bind(item: String, view: View, adapterPosition: Int) {
        val binding = ItemDriverToolsBinding.bind(view)
        binding.tvName.text = item
        binding.line.isVisible = adapterPosition != (currentList.size-1)
    }
}