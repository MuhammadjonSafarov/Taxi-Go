package uz.xia.taxigo.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import uz.xia.taxigo.databinding.ItemLayoutSettingsBinding

class SettingsAdapter(private val listener:ISettingsListener)
    :RecyclerView.Adapter<SettingsAdapter.SettingsVH>() {
    private val mDataList = mutableListOf<Settings>()
    private var lastPosition = -1L

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val anim = ScaleAnimation(
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            anim.duration = 100 //to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim)
            lastPosition = position.toLong()
        }
    }
       private fun setScaleAnimation(view: View) {
           val anim = ScaleAnimation(
               1.0f,
               1.0f,
               0.0f,
               1.0f
           )
           anim.interpolator = OvershootInterpolator()
           anim.duration = 400
           view.startAnimation(anim)
       }

    override fun getItemCount(): Int = mDataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLayoutSettingsBinding.inflate(layoutInflater, parent, false)
        return SettingsVH(binding)
    }
    fun setList(list: List<Settings>){
        mDataList?.clear()
        mDataList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SettingsVH, position: Int) {
        holder.onBind(mDataList[position])
//        setScaleAnimation(holder.itemView)
    }

    inner class SettingsVH(binding: ItemLayoutSettingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvGridItem = binding.textView
        private val cardView = binding.cardView
        private val ivSwitcher = binding.switcher
        fun onBind(model: Settings) {
            tvGridItem.text = tvGridItem.context.getString(model.resId)
            ivSwitcher.isVisible = (model.type == SettingsType.CHECKBOX)
            val iconDrawable = AppCompatResources.getDrawable(tvGridItem.context, model.iconId)
            tvGridItem.setCompoundDrawablesRelativeWithIntrinsicBounds(
                iconDrawable,
                null,
                null,
                null
            )
            cardView.setOnClickListener {
                listener.onItemClick(model.id)
            }
        }
    }
}
private val ItemSettings=object:ItemCallback<Settings>(){
    override fun areItemsTheSame(oldItem: Settings, newItem: Settings): Boolean {
        return oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: Settings, newItem: Settings): Boolean {
        return oldItem.id==newItem.id
    }
}

interface ISettingsListener {
    fun onItemClick(id: Long)
}

data class Settings(
    val id: Long = 0,
    val resId: Int,
    val iconId: Int,
    val type: SettingsType
)

enum class SettingsType {
    LABEL, CHECKBOX
}
