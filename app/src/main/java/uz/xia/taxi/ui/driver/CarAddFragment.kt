package uz.xia.taxi.ui.driver

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import uz.xia.taxi.R
import uz.xia.taxi.databinding.FragmentDriverCarAddBinding
import uz.xia.taxi.utils.MaskWatcher
import uz.xia.taxi.utils.Validation
import uz.xia.taxi.utils.errorCheckingTextChanges

@AndroidEntryPoint
class CarAddFragment : Fragment() {

    private var _binding: FragmentDriverCarAddBinding? = null

    private val cd = CompositeDisposable()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val autoNumber
        get() = binding.etAutoNumber.errorCheckingTextChanges(
            binding.InputLayoutAutoNumber,R.string.auto_number_not_fount
        ) {
            Validation.isValidAutoNumber(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverCarAddBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
    }

    private fun setUpViews() {
        binding.spAutoType.setSpinnerData(resources.getStringArray(R.array.cars).toList(), true)
        binding.spAutoColor.setSpinnerData(resources.getStringArray(R.array.colors).toList(), true)
        binding.etAutoNumber.addTextChangedListener(MaskWatcher.buildCpf())
        binding.etAutoNumber.filters = arrayOf<InputFilter>(AllCaps())
    }

    private fun setUpObserver() {

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
