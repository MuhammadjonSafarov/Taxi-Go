package uz.xia.taxigo.ui.participants

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.data.remote.model.user.UserData
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

interface IParticipantsViewModel {
    val liveData: LiveData<ResourceUI<PagingResponse<List<UserData>>>?>
    fun loadAllUsers()
}

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel(), IParticipantsViewModel {
    override val liveData = MutableLiveData<ResourceUI<PagingResponse<List<UserData>>>?>()

    override fun loadAllUsers() {
        viewModelScope.launch {
            liveData.value = ResourceUI.Loading
            liveData.value = handleResponse {
                apiService.getUsers()
            }
        }
    }

}