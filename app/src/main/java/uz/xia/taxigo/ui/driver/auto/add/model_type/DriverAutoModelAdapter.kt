package uz.xia.taxigo.ui.driver.auto.add.model_type

import android.text.Html
import android.view.View
import androidx.core.text.HtmlCompat
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.car.AutoModel
import uz.xia.taxigo.databinding.ItemAutoTypeBinding
import uz.xia.taxigo.utils.widget.AppListAdapter

class DriverAutoModelAdapter(private val listener: Callback) : AppListAdapter<AutoModel>(
    R.layout.item_auto_type,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {
    override fun bind(item: AutoModel, view: View, adapterPosition: Int) {
        val binding = ItemAutoTypeBinding.bind(view)
        binding.name.text = item.fullName
        binding.description.text = HtmlCompat.fromHtml(item.text, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.destination.text = "${item.seats}"
        binding.root.setOnClickListener {
            listener.itemClicked(item)
        }
    }

    interface Callback {
        fun itemClicked(item: AutoModel)
    }
}