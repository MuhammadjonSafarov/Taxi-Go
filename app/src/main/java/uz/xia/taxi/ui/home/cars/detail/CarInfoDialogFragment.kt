package uz.xia.taxi.ui.home.cars.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.xia.taxi.R
import uz.xia.taxi.databinding.DialogCarDetailBinding

class CarInfoDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogCarDetailBinding? = null
    private var itemCallback: MyDialogCloseListener? = null
    private val viewModel by viewModels<CarDetailViewModel>()

    fun setListener(callback: MyDialogCloseListener) {
        this.itemCallback = callback
    }

    private val binding get() = _binding!!

    override fun getTheme(): Int = R.style.CustomBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogCarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()


    }

    private fun setUpViews() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
        /*  binding.buttonNavigator.setOnClickListener {
              itemCallback?.onClickListener(41.257016, 69.192334)
              dismiss()
          }*/
    }

    private fun setUpObserver() {

    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        itemCallback?.handleDialogClose(dialog)
    }

    interface MyDialogCloseListener {
        fun onClickListener(latitude: Double, longitude: Double)
        fun handleDialogClose(dialog: DialogInterface)
    }

    companion object {
        fun newInstaince(id: Long): CarInfoDialogFragment {
            val bundle = bundleOf(Pair("key_id", id))
            val fragment = CarInfoDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
