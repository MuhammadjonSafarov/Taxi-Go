package uz.xia.taxigo.ui.driver.auto.list.adapter

import android.view.View
import androidx.core.text.HtmlCompat
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.databinding.ItemDriverCarBinding
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.show
import uz.xia.taxigo.utils.widget.AppListAdapter

class DriverCarsAdapter(private val listener: Callback) : AppListAdapter<CarDataResponse>(
    R.layout.item_driver_car,
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
) {
    private var mainCarId:Long=0L

    fun setCarId(carID:Long){
        this.mainCarId=carID
    }
    override fun bind(item: CarDataResponse, view: View, adapterPosition: Int) {
        val binding = ItemDriverCarBinding.bind(view)
        binding.tvName.text = item.autoData?.fullName
        binding.tvDescription.text = HtmlCompat.fromHtml( item.autoData?.text?:"", HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvNumber.text = item.number
        binding.root.setOnClickListener {
            listener.itemClicked(item)
        }

        if (item.id==mainCarId) {
            binding.mainCard.gone()
            binding.consLayout.setBackgroundResource(R.drawable.shape_for_driver_car_selected_bg)
        }else{
            binding.consLayout.setBackgroundResource(R.color.white)
            binding.mainCard.show()
        }
        binding.mainCard.setOnClickListener {
            listener.itemMainCardChange(item.id)
        }
    }

    interface Callback {
        fun itemClicked(item: CarDataResponse)
        fun itemMainCardChange(cardId: Long)
    }
}