package uz.xia.taxigo.ui.auth

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.remote.model.login.UserLoginRequest
import uz.xia.taxigo.data.remote.model.login.UserLoginResponse
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService
):BaseViewModel() {
    val liveData = SingleLiveEvent<ResourceUI<UserLoginResponse>?>()

    fun login(phone:String,password:String){
        viewModelScope.launch {
            liveData.value = ResourceUI.Loading
            liveData.value = handleResponse {
                val request  = UserLoginRequest(phone,password)
                apiService.login(request)
            }
        }
    }
}