package uz.xia.taxi.ui.participants.chat.photo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.xia.taxi.ui.participants.chat.photo.audio.AudioSelectFragment
import uz.xia.taxi.ui.participants.chat.photo.photo.PhotoSelectFragment
import uz.xia.taxi.ui.participants.chat.photo.photo.adapter.PhotoSelectListener
import uz.xia.taxi.ui.participants.chat.photo.video.VideoSelectFragment

class FilesPagerAdapter(
    private val listener: PhotoSelectListener, fm: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PhotoSelectFragment(listener)
            1 -> VideoSelectFragment()
            else -> AudioSelectFragment()
        }
    }
}
