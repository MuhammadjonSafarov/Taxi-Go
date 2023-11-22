package uz.xia.taxi.ui.participants.chat.photo.photo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.xia.taxi.R
import uz.xia.taxi.databinding.ItemSelectPhotoBinding

class PhotoAdapter(private val listener: PhotoSelectListener) :
    RecyclerView.Adapter<PhotoAdapter.VideoVH>() {

    private val mDataSet = mutableListOf<PhotoInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectPhotoBinding.inflate(layoutInflater, parent, false)
        return VideoVH(binding, listener)
    }

    override fun getItemCount(): Int = mDataSet.size

    override fun onBindViewHolder(holder: VideoVH, position: Int) {
        holder.onBind(mDataSet[position])
    }

    fun setList(it: List<PhotoInfo>) {
        mDataSet.addAll(it)
        notifyDataSetChanged()
    }

    class VideoVH(
        binding: ItemSelectPhotoBinding, private val listener: PhotoSelectListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
        private val ivImage = binding.image
        private val checkBox = binding.checkBox
        private var imagePath: String = ""

        fun onBind(model: PhotoInfo) {
            imagePath = model.uri?.path ?: ""
            Glide.with(ivImage.context).load(model.uri).centerCrop()
                .placeholder(R.drawable.shape_placeholder).into(ivImage)
            ivImage.setOnClickListener(this)
            checkBox.setOnCheckedChangeListener(this)
        }

        override fun onClick(p0: View?) {
            val isChecked = checkBox.isChecked
            checkBox.isChecked = !isChecked
        }

        override fun onCheckedChanged(checkBox: CompoundButton, isChecked: Boolean) {
            listener.onItemSelect(imagePath, isChecked)
        }

    }
}

interface PhotoSelectListener {
    fun onItemSelect(imagePath: String, isChecked: Boolean)
}
