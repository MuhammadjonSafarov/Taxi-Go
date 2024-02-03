package uz.xia.taxigo.ui.participants.chat.tool.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import uz.xia.taxigo.ui.participants.chat.tool.adapter.EndlessRecyclerOnScrollListener
import uz.xia.taxigo.ui.participants.chat.tool.adapter.IAction
import uz.xia.taxigo.ui.participants.chat.tool.adapter.ThreeGridItemDecorator
import uz.xia.taxigo.ui.participants.chat.tool.photo.adapter.PhotoAdapter
import uz.xia.taxigo.ui.participants.chat.tool.photo.adapter.PhotoSelectListener
import uz.xia.taxigo.databinding.FragmentSelectFilesBinding
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.px

private const val TAG = "PhotoSelectFragment"

class PhotoSelectFragment(private val listener: PhotoSelectListener) : Fragment(), IAction,
    PhotoSelectListener {
    private var _binding: FragmentSelectFilesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: IPhotoSelectViewModel by viewModels<PhotoSelectViewModel>()
    private val itemDecorator by lazyFast { ThreeGridItemDecorator(16.px, 8.px) }
    private val photoAdapter: PhotoAdapter by lazyFast { PhotoAdapter(this) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectFilesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getImages(requireContext(), 15)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.filesRv.adapter = photoAdapter
        binding.filesRv.layoutManager = gridLayoutManager
        binding.filesRv.addOnScrollListener(
            EndlessRecyclerOnScrollListener(
                gridLayoutManager,
                this
            )
        )
        binding.filesRv.addItemDecoration(itemDecorator)
        viewModel.liveImages.observe(viewLifecycleOwner) {
            photoAdapter.setList(it)
        }
    }

    override fun onAction() {
        viewModel.getImages(requireContext(), 15)
    }

    override fun onItemSelect(imagePath: String, isChecked: Boolean) {
        listener.onItemSelect(imagePath, isChecked)
    }
}
