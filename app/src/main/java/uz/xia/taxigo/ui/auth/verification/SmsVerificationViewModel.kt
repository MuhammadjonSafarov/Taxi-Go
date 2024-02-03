package uz.xia.taxigo.ui.auth.verification

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.xia.taxigo.services.broadcast.SmsLiveData
import javax.inject.Inject


@HiltViewModel
class SmsVerificationViewModel @Inject constructor(
    val smsLiveData: SmsLiveData
) :ViewModel()
