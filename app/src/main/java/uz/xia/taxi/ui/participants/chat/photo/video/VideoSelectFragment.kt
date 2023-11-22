package uz.xia.taxi.ui.participants.chat.photo.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import uz.xia.taxi.ui.participants.chat.photo.adapter.EndlessRecyclerOnScrollListener
import uz.xia.taxi.ui.participants.chat.photo.adapter.IAction
import uz.xia.taxi.ui.participants.chat.photo.adapter.ThreeGridItemDecorator
import uz.xia.taxi.ui.participants.chat.photo.video.adapter.VideoAdapter
import uz.xia.taxi.databinding.FragmentSelectFilesBinding
import uz.xia.taxi.utils.lazyFast
import uz.xia.taxi.utils.px

class VideoSelectFragment : Fragment(), IAction {
    private var _binding: FragmentSelectFilesBinding? = null
    private val binding get() = _binding!!
    private val itemDecorator by lazyFast { ThreeGridItemDecorator(16.px, 8.px) }
    private val viewModel: IVideoSelectViewModel by viewModels<VideoSelectViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getVideos(requireContext(), 15)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = VideoAdapter()
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.filesRv.adapter = adapter
        binding.filesRv.addItemDecoration(itemDecorator)
        binding.filesRv.addOnScrollListener(
            EndlessRecyclerOnScrollListener(
                gridLayoutManager,
                this
            )
        )
        viewModel.liveVideos.observe(viewLifecycleOwner) {
            adapter.setList(it)
        }
    }

    override fun onAction() {
        viewModel.getVideos(requireContext(), 15)
    }
}
