package uz.xia.taxi.ui.auth.verification

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.xia.taxi.broadcast.SmsLiveData
import javax.inject.Inject


@HiltViewModel
class SmsVerificationViewModel @Inject constructor(
    val smsLiveData: SmsLiveData
) :ViewModel()
