package uz.xia.taxigo.ui.auth.verification

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ViewSwitcher
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.services.broadcast.AppSignatureHelper
import uz.xia.taxigo.common.MASK_SMS_NUMBER
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.databinding.FragmentSmsVerificationBinding
import uz.xia.taxigo.ui.MainActivity
import uz.xia.taxigo.utils.inflateLayout
import javax.inject.Inject

private const val TAG = "SmsVerificationFragment"
@AndroidEntryPoint
class SmsVerificationFragment : Fragment(), ViewSwitcher.ViewFactory, View.OnClickListener {
    private var _binding: FragmentSmsVerificationBinding? = null
    private val viewModel by viewModels<SmsVerificationViewModel>()
    @Inject
    lateinit var preference:IPreference
    private val binding get() = _binding!!
    private var timer: CountDownTimer? = null
    private val maskedListener = object : MaskedTextChangedListener.ValueListener {
        override fun onTextChanged(
            maskFilled: Boolean, extractedValue: String, formattedValue: String
        ) {
            binding.buttonConform.isEnabled = maskFilled
            //phoneNumber=extractedValue
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmsVerificationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObserver()
        setupTimer()
        countDownTimerStart()
    }

    private fun setUpObserver() {
      val signature = AppSignatureHelper(requireContext()).appSignatures[0]
      Timber.d("$TAG signature:$signature")
        viewModel.smsLiveData.observe(viewLifecycleOwner){
            binding.etSms.setText(it)
        }
    }

    private fun setUpViews() {
        val affineFormats = mutableListOf<String>()
        affineFormats.add(MASK_SMS_NUMBER)
        val listener: MaskedTextChangedListener = MaskedTextChangedListener.installOn(
            binding.etSms,
            MASK_SMS_NUMBER,
            affineFormats,
            AffinityCalculationStrategy.PREFIX,
            maskedListener
        )
        binding.etSms.hint = listener.placeholder()
        binding.buttonConform.setOnClickListener(this)
    }

    private fun setupTimer() {
        val downAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_down)
        val upAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_up)
        binding.tswMinuteDigit.inAnimation = downAnim
        binding.tswMinuteDigit.outAnimation = upAnim
        binding.tswMinuteDecimal.inAnimation = downAnim
        binding.tswMinuteDecimal.outAnimation = upAnim
        binding.tswSecondDigit.inAnimation = downAnim
        binding.tswSecondDigit.outAnimation = upAnim
        binding.tswSecondDecimal.inAnimation = downAnim
        binding.tswSecondDecimal.outAnimation = upAnim
        binding.tswMinuteDigit.setFactory(this)
        binding.tswMinuteDecimal.setFactory(this)
        binding.tswSecondDigit.setFactory(this)
        binding.tswSecondDecimal.setFactory(this)
    }

    private fun countDownTimerStart() {
        var secondUntil: Int
        var second: Int
        var lastSecond = 6
        var minute: Int
        var lastMinuteDigital = 6
        var lastMinuteDecimal = 10
        val totalTime = 60L
        timer = object : CountDownTimer(totalTime * 1_000L, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                secondUntil = (millisUntilFinished / 1_000L).toInt()
                minute = secondUntil / 60
                second = secondUntil % 60
                if (minute / 10 != lastMinuteDigital) {
                    lastMinuteDigital = minute / 10
                    binding.tswMinuteDigit.setText(lastMinuteDigital.toString())
                }
                if (minute % 10 != lastMinuteDecimal) {
                    lastMinuteDecimal = minute % 10
                    binding.tswMinuteDecimal.setText((minute % 10).toString())
                }
                if (second / 10 != lastSecond) {
                    lastSecond = second / 10
                    binding.tswSecondDigit.setText((second / 10).toString())
                }
                binding.tswSecondDecimal.setText((second % 10).toString())
            }

            override fun onFinish() {
                //todo finish task
            }
        }
        timer?.start()
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun makeView(): View = requireContext().inflateLayout(R.layout.timer_view) as AppCompatTextView

    override fun onClick(p0: View?) {
        preference.accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJATVNhaWRhaG1lZG92Iiwicm9sZXMiOlt7Im5hbWUiOiJST0xFX1VTRVIifSx7Im5hbWUiOiJST0xFX0FETUlOIn1dLCJpYXQiOjE3MDY3NzcxNDAsImV4cCI6MTcwNjg2MzU0MH0.VX3dTFNtMROlJFNSuFjcDQxfVoqyuCy1-j6gXfdVevQ"
        val intent = Intent(requireContext(),MainActivity::class.java)
        startActivity(intent)
    }
}
