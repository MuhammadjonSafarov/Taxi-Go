package uz.xia.taxi.ui.address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxi.R
import uz.xia.taxi.data.local.entity.UserAddress
import uz.xia.taxi.data.remote.enumrition.AddressStatus
import uz.xia.taxi.data.remote.model.EmptyUserAddress
import uz.xia.taxi.data.remote.model.GroupUserAddress
import uz.xia.taxi.databinding.ItemAddressBinding
import uz.xia.taxi.databinding.ItemAddressGroupDateBinding
import uz.xia.taxi.databinding.ItemEmptyAddressBinding
import java.text.SimpleDateFormat

private const val ITEM_ADDRESS_TYPE = 1
private const val ITEM_EMPTY_TYPE = 2
private const val ITEM_GROUP_TYPE = 3

class AddressAdapter : ListAdapter<UserAddress, RecyclerView.ViewHolder>(ItemAddressDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == ITEM_EMPTY_TYPE) {
            val binding = ItemEmptyAddressBinding.inflate(layoutInflater, parent, false)
            return EmptyVH(binding)
        } else  if (viewType == ITEM_ADDRESS_TYPE){
            val binding = ItemAddressBinding.inflate(layoutInflater, parent, false)
            return AddressVH(binding)
        }else {
            val binding = ItemAddressGroupDateBinding.inflate(layoutInflater, parent, false)
            return GroupVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupVH) {
            holder.onBind(getItem(position) as GroupUserAddress)
        }else if (holder is AddressVH){
            holder.onBind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position) is EmptyUserAddress) {
            ITEM_EMPTY_TYPE
        } else if (getItem(position) is GroupUserAddress) {
            ITEM_GROUP_TYPE
        } else {
            ITEM_ADDRESS_TYPE
        }
    }

    class GroupVH(binding: ItemAddressGroupDateBinding):RecyclerView.ViewHolder(binding.root){
        private val tvDate =binding.llcNear
        private val dateFormat=SimpleDateFormat("dd MMMM Y")
        fun onBind(model :GroupUserAddress){
            tvDate.text = dateFormat.format(model.time)
        }
    }
    class EmptyVH(binding: ItemEmptyAddressBinding) : RecyclerView.ViewHolder(binding.root)
    class AddressVH(binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.tvName
        private val tvDescription = binding.tvDescription
        private val tvDate = binding.tvDate
        private val ivIcon = binding.image
        private val dateFormat=SimpleDateFormat("HH:mm")
        fun onBind(userAddress: UserAddress) {
            tvDescription.text = userAddress.description
            tvDate.text = dateFormat.format(userAddress.updateAt)
            when (userAddress.type) {
                AddressStatus.HOME -> {
                    ivIcon.setImageResource(R.drawable.ic_menu_home)
                    tvName.text = tvName.context.getString(R.string.home)
                }

                AddressStatus.WORK -> {
                    ivIcon.setImageResource(R.drawable.ic_suitcase)
                    tvName.text = tvName.context.getString(R.string.workplace)
                }
                else -> {
                    ivIcon.setImageResource(R.drawable.ic_address)
                    tvName.text = userAddress.name
                }
            }
        }
    }
}

private val ItemAddressDiffer = object : ItemCallback<UserAddress>() {
    override fun areItemsTheSame(oldItem: UserAddress, newItem: UserAddress): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserAddress, newItem: UserAddress): Boolean {
        return oldItem == newItem
    }

}
