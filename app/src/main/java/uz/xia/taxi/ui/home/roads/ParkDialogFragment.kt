package uz.xia.taxi.ui.home.roads

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxi.R
import uz.xia.taxi.data.local.entity.StationData
import uz.xia.taxi.databinding.DialogPostDetailBinding
import uz.xia.taxi.ui.home.roads.adapter.RoadsAdapter
import uz.xia.taxi.utils.lazyFast

@AndroidEntryPoint
class ParkDialogFragment : BottomSheetDialogFragment(), RoadsAdapter.IRoadListener {

    private var _binding: DialogPostDetailBinding? = null
    private var itemCallback: MyDialogCloseListener?=null
    private val viewModel by viewModels<ParkDetailViewModel>()
    private val mAdapter by lazyFast { RoadsAdapter(this) }
    fun setListener(callback: MyDialogCloseListener){
        this.itemCallback=callback
    }
    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parkingId=arguments?.getLong("key_id")?:0
        viewModel.loadRoads(parkingId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()


    }
    private fun setUpViews() {
        binding.rvRoads.adapter=mAdapter
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        binding.buttonNavigator.setOnClickListener {
            itemCallback?.onClickListener(41.257016, 69.192334)
            dismiss()
        }
    }
    private fun setUpObserver() {
        viewModel.roadsLiveData.observe(viewLifecycleOwner){
            mAdapter.submitList(it)
        }
    }



    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        itemCallback?.handleDialogClose(dialog)
    }

    interface MyDialogCloseListener{
        fun onClickListener(latitude:Double,longitude:Double)
        fun onRoadClickListener(id:Long)
        fun handleDialogClose(dialog:DialogInterface)
    }

    companion object{
        fun newInstaince(id:Long):ParkDialogFragment{
            val bundle= bundleOf(Pair("key_id",id))
            val fragment=ParkDialogFragment()
            fragment.arguments=bundle
            return fragment
        }
    }

    override fun itemRoadClick(id: Long) {
       itemCallback?.onRoadClickListener(id)
       dismiss()
    }
}
