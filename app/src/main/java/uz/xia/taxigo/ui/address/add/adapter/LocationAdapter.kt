package uz.xia.taxigo.ui.address.add.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.model.nomination.NearEmpty
import uz.xia.taxigo.data.remote.model.nomination.NearLoading
import uz.xia.taxigo.data.remote.model.nomination.NearbyPlace
import uz.xia.taxigo.databinding.ItemNearbyCityBinding

class LocationAdapter(
    private val placeClickListener: OnPlaceClickListener
) : ListAdapter<NearbyPlace, RecyclerView.ViewHolder>(NearbyDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_loading -> LoadingViewHolder(
                inflater.inflate(
                    R.layout.item_loading,
                    parent,
                    false
                )
            )
            R.layout.item_empty -> EmptyViewHolder(
                inflater.inflate(
                    R.layout.item_empty,
                    parent,
                    false
                )
            )
            else -> ViewHolder(
                ItemNearbyCityBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val place = getItem(position)
        if (holder is ViewHolder)
            holder.bind(place)
    }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is NearLoading -> R.layout.item_loading
        is NearEmpty -> R.layout.item_empty
        else -> R.layout.item_nearby_city
    }

    fun getNearItem(position: Int): NearbyPlace = getItem(position)

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ViewHolder(private val itemBinding: ItemNearbyCityBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        private val distanceFormat = itemBinding.root.context.getString(R.string.distance)
        private val distanceMeterFormat = itemBinding.root.context.getString(R.string.distance_meter)
        private val tvDistance = itemBinding.tvDistance
        private val tvPlaceName = itemBinding.tvPlaceName

        fun bind(place: NearbyPlace) {
            itemBinding.root.setOnClickListener(this)
            tvPlaceName.text = place.name
            if (place.distance < 1000.0)
                tvDistance.text = String.format(distanceMeterFormat, place.distance)
            else
                tvDistance.text = String.format(distanceFormat, place.distance / 1000)
        }

        override fun onClick(view: View) {
            placeClickListener.onNearPlace(getItem(adapterPosition))
        }
    }

    object NearbyDiffer : DiffUtil.ItemCallback<NearbyPlace>() {
        override fun areItemsTheSame(oldItem: NearbyPlace, newItem: NearbyPlace) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: NearbyPlace, newItem: NearbyPlace) =
            oldItem == newItem
    }

    interface OnPlaceClickListener {
        fun onNearPlace(place: NearbyPlace)
    }
}
