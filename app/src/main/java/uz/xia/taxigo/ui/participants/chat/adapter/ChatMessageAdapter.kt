package uz.xia.taxigo.ui.participants.chat.adapter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import uz.xia.taxigo.R
import uz.xia.taxigo.data.remote.enumrition.ChatMessageType
import uz.xia.taxigo.data.remote.model.chat.ChatMessageData
import uz.xia.taxigo.data.remote.model.chat.EmptyMessagesData
import uz.xia.taxigo.data.remote.model.chat.GroupMessagesData
import uz.xia.taxigo.databinding.ItemChatGroupDateBinding
import uz.xia.taxigo.databinding.ItemChatLocationLeftBinding
import uz.xia.taxigo.databinding.ItemChatLocationRightBinding
import uz.xia.taxigo.databinding.ItemChatMessageLeftBinding
import uz.xia.taxigo.databinding.ItemChatMessageRightBinding
import uz.xia.taxigo.databinding.ItemEmptyAddressBinding
import uz.xia.taxigo.utils.dateFormat
import uz.xia.taxigo.utils.getBitmapFromVector
import java.text.SimpleDateFormat

private const val MSG_TYPE_LEFT = 0
private const val MSG_TYPE_RIGHT = 1
private const val LOCATION_TYPE_RIGHT = 2
private const val LOCATION_TYPE_LEFT = 3
private const val EMPTY_TYPE = 4
private const val GROUP_TYPE = 5

class ChatMessageAdapter(private val currentUserId: Long,
                         private val listener:IMessageListener) :
    ListAdapter<ChatMessageData, RecyclerView.ViewHolder>(ItemChatDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MSG_TYPE_LEFT -> {
                val binding = ItemChatMessageLeftBinding.inflate(layoutInflater, parent, false)
                TargetMessageVH(binding)
            }

            MSG_TYPE_RIGHT -> {
                val binding = ItemChatMessageRightBinding.inflate(layoutInflater, parent, false)
                CurrentMessageVH(binding)
            }

            GROUP_TYPE -> {
                val binding = ItemChatGroupDateBinding.inflate(layoutInflater, parent, false)
                GroupVH(binding)
            }

            LOCATION_TYPE_RIGHT -> {
                val binding = ItemChatLocationRightBinding.inflate(layoutInflater, parent, false)
                LocationRightVH(binding)
            }

            LOCATION_TYPE_LEFT -> {
                val binding = ItemChatLocationLeftBinding.inflate(layoutInflater, parent, false)
                LocationLeftVH(binding)
            }

            else -> {
                val binding = ItemEmptyAddressBinding.inflate(layoutInflater, parent, false)
                EmptyVH(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = getItem(position)
        if (holder is TargetMessageVH) {
            holder.onBind(model)
        } else if (holder is CurrentMessageVH) {
            holder.onBind(model)
        } else if (holder is GroupVH) {
            holder.onBind(model as GroupMessagesData)
        } else if (holder is LocationLeftVH) {
            holder.onBind(model)
        } else if (holder is LocationRightVH) {
            holder.onBind(model)
        }
    }

   /* fun setData(it: List<ChatMessageData>) {
        mDataSet.clear()
        mDataSet.addAll(it)
        notifyDataSetChanged()
    }*/

    //override fun getItemCount(): Int = mDataSet.size

    override fun getItemViewType(position: Int): Int {
        val model = getItem(position)
        return when (model) {
            is GroupMessagesData -> GROUP_TYPE
            is EmptyMessagesData -> EMPTY_TYPE
            else -> {
                when (model.type) {
                    ChatMessageType.TEXT.name -> if (model.sender == currentUserId)
                        MSG_TYPE_RIGHT else MSG_TYPE_LEFT

                    ChatMessageType.LOCATION.name -> if (model.sender == currentUserId)
                        LOCATION_TYPE_RIGHT else LOCATION_TYPE_LEFT

                    else -> LOCATION_TYPE_RIGHT
                }

            }
        }
    }

    class TargetMessageVH(binding: ItemChatMessageLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvMessage = binding.textMessageBody
        private val tvTime = binding.textMessageTime

        fun onBind(chat: ChatMessageData) {
            tvMessage.text = chat.content
            tvTime.text = "12:00"
        }
    }

    class GroupVH(binding: ItemChatGroupDateBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvDate = binding.llcNear

        private val dateFormat = SimpleDateFormat("dd MMMM Y")
        fun onBind(model: GroupMessagesData) {
            val time = dateFormat().parse(model.time)
            tvDate.text = dateFormat.format(time)
        }
    }

   inner class LocationLeftVH(private val binding: ItemChatLocationLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(chat: ChatMessageData) {
            binding.textMessageTime.text = "12:00"
            val context = binding.root.context
            val ctx: Context? = context?.applicationContext
            Configuration.getInstance().load(
                ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            )
            val map = binding.mapView
            map.setTileSource(TileSourceFactory.MAPNIK)
            map.setUserInteractionEnabled(false)
            val mapController = map.controller
            mapController?.setZoom(14.99)
            val point = GeoPoint(chat.latitude, chat.longitude)
            mapController?.setCenter(point)
            val marker = Marker(map)
            marker.position = point
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val bitmap = binding.root.context.getBitmapFromVector(R.drawable.ic_yandex_maps_icon)
            val dr: Drawable = BitmapDrawable(context.resources, bitmap)
            marker.icon = dr
            map.overlays?.add(marker)
            // mapController?.animateTo(startPoint)
            binding.root.setOnClickListener {
                listener.locationClick(chat.longitude,chat.latitude)
            }
        }
    }

   inner class LocationRightVH(private val binding: ItemChatLocationRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(chat: ChatMessageData) {
            binding.textMessageTime.text = "12:00"
            val context = binding.root.context
            val ctx: Context? = context?.applicationContext
            Configuration.getInstance().load(
                ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            )
            val map = binding.mapView
            map.setTileSource(TileSourceFactory.MAPNIK)
            map.setBuiltInZoomControls(false)
            map.setMultiTouchControls(false)
            map.isEnabled = false
            map.setUserInteractionEnabled(false)
            val mapController = map.controller
            mapController?.setZoom(14.99)
            val point = GeoPoint(chat.latitude, chat.longitude)
            mapController?.setCenter(point)
            val marker = Marker(map)
            marker.position = point
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val bitmap = binding.root.context.getBitmapFromVector(R.drawable.ic_yandex_maps_icon)
            val dr: Drawable = BitmapDrawable(context.resources, bitmap)
            marker.icon = dr
            map.overlays?.add(marker)
            // mapController?.animateTo(startPoint)
            binding.root.setOnClickListener {
              listener.locationClick(chat.longitude,chat.latitude)
            }
        }
    }

    class EmptyVH(binding: ItemEmptyAddressBinding) : RecyclerView.ViewHolder(binding.root)

    class CurrentMessageVH(binding: ItemChatMessageRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvMessage = binding.textMessageBody
        fun onBind(chat: ChatMessageData) {
            tvMessage.text = chat.content
        }
    }
}
interface IMessageListener{
    fun locationClick(longitude:Double,latitude:Double)
}
private val ItemChatDiffer = object : DiffUtil.ItemCallback<ChatMessageData>() {
    override fun areItemsTheSame(oldItem: ChatMessageData, newItem: ChatMessageData): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ChatMessageData, newItem: ChatMessageData): Boolean {
        return oldItem.id == newItem.id
    }
}
