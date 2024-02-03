package uz.xia.taxigo.ui.participants.chat

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.xia.taxigo.data.remote.model.chat.ChatMessageData
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

private const val TAG = "ChatMessageViewModel"

@HiltViewModel
class ChatMessageViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {

    val liveChatsData = SingleLiveEvent<ResourceUI<PagingResponse<List<ChatMessageData>>>?>()
    val liveChatData = SingleLiveEvent<ResourceUI<ChatMessageData>?>()

    fun chatsData(senderId: Long, receiverId: Long) {
        viewModelScope.launch {
            liveChatsData.value = ResourceUI.Loading
            liveChatsData.value = handleResponse {
                apiService.getMessages(senderId, receiverId)
            }
        }
    }

    fun sendData(request: ChatMessageData) {
        viewModelScope.launch {
            liveChatData.value = ResourceUI.Loading
            liveChatData.value = handleResponse {
                apiService.sendData(request)
            }
        }
    }
}