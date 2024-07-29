package uz.xia.taxigo.ui.home.roads.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxigo.data.local.remote.model.RoadWithStations
import uz.xia.taxigo.databinding.ItemRoadBinding

class RoadsAdapter(private val listener:IRoadListener) : ListAdapter<RoadWithStations, RoadsAdapter.RoadVH>(ItemDiffer) {
    inner class RoadVH(binding: ItemRoadBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.name
        private val tvDescription = binding.description
        private val itemLayout = binding.constRoad

        fun onBind(roadData: RoadWithStations) {
            val stationsSize = roadData.stations.size
            var stationNames = ""
            val roadName =
                if (stationsSize > 0) "${roadData.stations[0]} -> ${roadData.stations[stationsSize - 1]}"
                else ""
            tvName.text = roadName
            roadData.stations.forEach {
                stationNames = "$stationNames$it, "
            }
            tvDescription.text = stationNames
            itemLayout.setOnClickListener {
                listener.itemRoadClick(roadData.roadData.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRoadBinding.inflate(layoutInflater, parent, false)
        return RoadVH(binding)
    }

    override fun onBindViewHolder(holder: RoadVH, position: Int) {
        holder.onBind(getItem(position))
    }
    interface IRoadListener{
        fun itemRoadClick(id:Long)
    }
}

private val ItemDiffer = object : ItemCallback<RoadWithStations>() {
    override fun areItemsTheSame(oldItem: RoadWithStations, newItem: RoadWithStations): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: RoadWithStations, newItem: RoadWithStations): Boolean {
        return oldItem.roadData.id == newItem.roadData.id
    }

}
