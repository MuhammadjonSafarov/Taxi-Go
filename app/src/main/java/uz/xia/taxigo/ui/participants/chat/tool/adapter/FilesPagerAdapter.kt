package uz.xia.taxigo.ui.participants.chat.tool.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.xia.taxigo.ui.participants.chat.tool.audio.AudioSelectFragment
import uz.xia.taxigo.ui.participants.chat.location.LocationSelectFragment
import uz.xia.taxigo.ui.participants.chat.tool.photo.PhotoSelectFragment
import uz.xia.taxigo.ui.participants.chat.tool.photo.adapter.PhotoSelectListener
import uz.xia.taxigo.ui.participants.chat.tool.video.VideoSelectFragment

class FilesPagerAdapter(
    private val listener: PhotoSelectListener,
    fm: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PhotoSelectFragment(listener)
            1 -> VideoSelectFragment()
            else ->   AudioSelectFragment()
        }
    }
}
