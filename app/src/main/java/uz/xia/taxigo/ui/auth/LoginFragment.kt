package uz.xia.taxigo.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.common.MASK_PHONE_NUMBER
import uz.xia.taxigo.databinding.FragmentLoginBinding
import uz.xia.taxigo.utils.lazyFast
private val CREDENTIAL_PICKER_REQUEST = 1  // Set to an unused request code
private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment:Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val navController by lazyFast {
        Navigation.findNavController(requireActivity(),
            R.id.nav_host_fragment_auth) }
    private val maskedListener = object : MaskedTextChangedListener.ValueListener {
        override fun onTextChanged(
            maskFilled: Boolean,
            extractedValue: String,
            formattedValue: String
        ) {
            binding.buttonConform.isEnabled = maskFilled
            //phoneNumber=extractedValue
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        val affineFormats = mutableListOf<String>()
        affineFormats.add(MASK_PHONE_NUMBER)
        val listener: MaskedTextChangedListener = MaskedTextChangedListener.installOn(
            binding.etPhone,
            MASK_PHONE_NUMBER, affineFormats,
            AffinityCalculationStrategy.PREFIX, maskedListener
        )
        binding.etPhone.hint = listener.placeholder()
        binding.buttonConform.setOnClickListener {
           // requestHint()
            navController.navigate(R.id.smsVerificationFragment)
        }
    }

    // Construct a request for phone numbers and show the picker
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient = Credentials.getClient(requireActivity())
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            CREDENTIAL_PICKER_REQUEST,
            null, 0, 0, 0, bundleOf())
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    Timber.d("$TAG task : ${credential?.idTokens},${credential?.id}, ${credential?.name}, ${credential?.password}")
                   val senderPhoneNumber = "+998900636690"
                    val task = SmsRetriever.getClient(requireContext()).startSmsUserConsent(senderPhoneNumber /* or null */)
                    task.addOnCompleteListener {
                        Timber.d("$TAG task result ${it.result}")
                    }
                    //Toast.makeText(requireActivity(), credential, Toast.LENGTH_SHORT).show()
                    // credential.getId();  <-- will need to process phone number string
                }
            // ...
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
