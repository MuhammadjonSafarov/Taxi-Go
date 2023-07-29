package uz.xia.taxi.ui.address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxi.R
import uz.xia.taxi.data.local.entity.UserAddress
import uz.xia.taxi.data.remote.enumrition.AddressStatus
import uz.xia.taxi.databinding.ItemAddressBinding
import uz.xia.taxi.databinding.ItemEmptyAddressBinding

class AddressAdapter : ListAdapter<UserAddress, AddressAdapter.AddressVH>(ItemAddressDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAddressBinding.inflate(layoutInflater, parent, false)
        return AddressVH(binding)
    }

    override fun onBindViewHolder(holder: AddressVH, position: Int) {
        holder.onBind(getItem(position))
    }
    class EmptyVH(binding:ItemEmptyAddressBinding)
        :RecyclerView.ViewHolder(binding.root)
    class AddressVH(binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tvName = binding.tvName
        private val tvDescription = binding.tvDescription
        private val tvDate = binding.tvDate
        private val ivIcon = binding.image

        fun onBind(userAddress: UserAddress) {
            tvName.text = userAddress.name
            tvDescription.text = userAddress.description
            tvDate.text = userAddress.updateAt.toString()
            when (userAddress.type) {
                AddressStatus.HOME -> ivIcon.setImageResource(R.drawable.ic_menu_home)
                AddressStatus.WORK -> ivIcon.setImageResource(R.drawable.ic_address)
                else -> ivIcon.setImageResource(R.drawable.ic_address)
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