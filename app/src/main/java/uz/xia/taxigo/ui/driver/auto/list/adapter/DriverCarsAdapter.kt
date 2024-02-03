package uz.xia.taxigo.ui.driver.auto.list.adapter

import android.view.View
import androidx.core.text.HtmlCompat
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.data.remote.model.car.CarData
import uz.xia.taxigo.databinding.ItemAutoTypeBinding
import uz.xia.taxigo.databinding.ItemDriverCarBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class DriverCarsAdapter(private val listener: Callback) : AppListAdapter<CarData>(
    R.layout.item_driver_car,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {
    override fun bind(item: CarData, view: View, adapterPosition: Int) {
        val binding = ItemDriverCarBinding.bind(view)
        binding.tvName.text = item.autoData?.fullName
        binding.tvDescription.text = HtmlCompat.fromHtml( item.autoData?.text?:"", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvNumber.text = item.number
        binding.root.setOnClickListener {
            listener.itemClicked(item)
        }
    }

    interface Callback {
        fun itemClicked(item: CarData)
    }
}