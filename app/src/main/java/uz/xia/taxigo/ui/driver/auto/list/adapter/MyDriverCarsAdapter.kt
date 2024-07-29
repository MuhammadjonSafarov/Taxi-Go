package uz.xia.taxigo.ui.driver.auto.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.car.CarDataResponse
import uz.xia.taxigo.databinding.ItemDriverCarBinding
import uz.xia.taxigo.utils.gone
import uz.xia.taxigo.utils.show

class MyDriverCarsAdapter(private val listener: Callback) :
    RecyclerView.Adapter<MyDriverCarsAdapter.CarsVH>() {
    private val mDataSet = mutableListOf<CarDataResponse>()
    private var mainCarId = 0L

    fun setCarData(carId:Long){
        this.mainCarId=carId
    }

    fun setList(it:List<CarDataResponse>?){
        mDataSet?.clear()
        mDataSet.addAll(it.orEmpty())
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDriverCarBinding.inflate(layoutInflater, parent, false)
        return CarsVH(binding)
    }

    override fun getItemCount(): Int = mDataSet.size

    override fun onBindViewHolder(holder: CarsVH, position: Int) {
        holder.onBind(mDataSet[position])
    }

    inner class CarsVH(private val binding: ItemDriverCarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CarDataResponse) {
            binding.tvName.text = item.autoData?.fullName
            binding.tvDescription.text =
                HtmlCompat.fromHtml(item.autoData?.text ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.tvNumber.text = item.number
            binding.root.setOnClickListener {
                listener.itemClicked(item)
            }
            if (item.id == mainCarId) {
                binding.mainCard.gone()
                binding.consLayout.setBackgroundResource(R.drawable.shape_for_driver_car_selected_bg)
            } else {
                binding.consLayout.setBackgroundResource(R.color.white)
                binding.mainCard.show()
            }
            binding.mainCard.setOnClickListener {
                listener.itemMainCardChange(item.id)
            }
        }
    }

    interface Callback {
        fun itemClicked(item: CarDataResponse)
        fun itemMainCardChange(cardId: Long)
    }
}