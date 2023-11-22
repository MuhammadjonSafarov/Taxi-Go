package uz.xia.taxi.ui.address.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.xia.taxi.R
import uz.xia.taxi.common.EMPTY_STRING
import uz.xia.taxi.databinding.DialogAddressAddBinding
import uz.xia.taxi.widget.SimpleSpinner


class AddAddressDialog : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: DialogAddressAddBinding? = null
    private val binding get() = _binding!!
    private var addressTypePosition: Int = 0

    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddressAddBinding.inflate(inflater, container, false)
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
        val addressText = arguments?.getString("key_address") ?: EMPTY_STRING
        binding.etAddressDescription.setText(addressText)
        binding.spAddressType.setSpinnerData(listOf("Uy", "Ishxona", "Boshqa"), true)
        binding.spAddressType.itemSelectedListener = object : SimpleSpinner.ItemSelectedListener {
            override fun onItemSelected(position: Int) {
                addressTypePosition = position
                if (position>1){
                    binding.addressNameInputLayout.visibility=View.VISIBLE
                    binding.tvAddressName.visibility=View.VISIBLE
                }else{
                    binding.addressNameInputLayout.visibility=View.GONE
                    binding.tvAddressName.visibility=View.GONE
                }
            }
        }
        binding.buttonCancel.setOnClickListener(this)
        binding.buttonConform.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_cancel -> dismiss()
            R.id.button_conform -> {
                val address = binding.etAddressDescription.text.toString()
                val addressName = binding.etAddressName.text.toString()
                if (address.isEmpty()) {
                    binding.addressDescriptionInputLayout.error = getString(R.string.is_empty)
                }
                if (addressName.isEmpty()) {
                    binding.addressNameInputLayout.error = getString(R.string.is_empty)
                }
                if ((addressTypePosition<=1 ||addressName.isNotEmpty()) && address.isNotEmpty()) {
                    val bundle = bundleOf(
                        Pair("key_address_type", addressTypePosition),
                        Pair("key_address_name", addressName),
                        Pair("key_address", address)
                    )
                    setFragmentResult("key_data", bundle)
                    dismiss()
                }
            }
        }
    }
}