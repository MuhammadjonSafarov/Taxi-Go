package uz.xia.taxigo.ui.add_data.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.xia.taxigo.R
import uz.xia.taxigo.data.local.entity.RegionData
import uz.xia.taxigo.databinding.DialogAddressAddBinding
import uz.xia.taxigo.databinding.DialogSelectRegionBinding
import uz.xia.taxigo.ui.add_data.location.adapter.RegionAdapter
import uz.xia.taxigo.utils.lazyFast

class DialogRegions : BottomSheetDialogFragment(), RegionAdapter.Callback {
    private var _binding: DialogSelectRegionBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazyFast { RegionAdapter(this) }
    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    private val data = mutableListOf<RegionData>()
    var onItemClickListener: ((item: RegionData) -> Unit)? = null

    fun setData(
        items: List<RegionData>
    ) {
        data.clear()
        data.addAll(items)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogSelectRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet: FrameLayout? =
                bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            val bottomSheetBehavior: BottomSheetBehavior<*> =
                BottomSheetBehavior.from(bottomSheet!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.rvRegions.adapter = adapter
        adapter.submitList(data)

        binding.buttonClose.setOnClickListener {
            dismiss()
        }
    }

    override fun itemClicked(id: RegionData) {
        onItemClickListener?.invoke(id)
        dismiss()
    }
}