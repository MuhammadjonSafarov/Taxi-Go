package uz.xia.taxigo.ui.participants.chat.tool.video.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.xia.taxigo.R
import uz.xia.taxigo.databinding.ItemSelectVideoBinding

class VideoAdapter:RecyclerView.Adapter<VideoAdapter.VideoVH>() {
    private val mDataSet = mutableListOf<VideoInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectVideoBinding.inflate(layoutInflater,parent,false)
        return VideoVH(binding)
    }

    override fun getItemCount(): Int = mDataSet.size

    override fun onBindViewHolder(holder: VideoVH, position: Int) {
        holder.onBind(mDataSet[position])
    }

    fun setList(it:List<VideoInfo>){
        mDataSet.addAll(it)
        notifyDataSetChanged()
    }

    class VideoVH(binding:ItemSelectVideoBinding)
        :RecyclerView.ViewHolder(binding.root){
        private val ivImage = binding.image
        private val tvDuration = binding.duration

        fun onBind(model: VideoInfo){
            val minutes: Int = (model.duration?:0) / (60 * 1000) % 60
            val seconds: Int = (model.duration?:0) / 1000 % 60
            tvDuration.text = "${String.format("%02d",minutes)}:${String.format("%02d",seconds)}"
            Glide.with(ivImage.context)
                .load(model.uri)
                .centerCrop()
                .placeholder(R.drawable.shape_placeholder)
                .into(ivImage)
        }
    }
}
