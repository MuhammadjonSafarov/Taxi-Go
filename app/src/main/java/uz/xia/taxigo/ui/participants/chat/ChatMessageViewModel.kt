package uz.xia.taxigo.ui.participants.chat

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import uz.xia.taxigo.data.remote.model.chat.ChatMessageData
import uz.xia.taxigo.data.remote.model.chat.ChatMessageRequest
import uz.xia.taxigo.data.remote.model.chat.EmptyMessagesData
import uz.xia.taxigo.data.remote.model.chat.GroupMessagesData
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.network.ApiService
import uz.xia.taxigo.utils.SingleLiveEvent
import uz.xia.taxigo.utils.isSomeDayInstant
import uz.xia.taxigo.utils.widget.base.BaseViewModel
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

private const val TAG = "ChatMessageViewModel"

@HiltViewModel
class ChatMessageViewModel @Inject constructor(
    private val apiService: ApiService
) : BaseViewModel() {

    val liveChatsData = SingleLiveEvent<ResourceUI<PagingResponse<List<ChatMessageData>>>?>()
    val liveData = SingleLiveEvent<List<ChatMessageData>>()
    val liveChatData = SingleLiveEvent<ResourceUI<ChatMessageData>?>()
    private val chatsData = mutableListOf<ChatMessageData>()

    fun chatsData(senderId: Long, receiverId: Long) {
        viewModelScope.launch {
            liveChatsData.value = ResourceUI.Loading
            liveChatsData.value = handleResponse {
                apiService.getMessages(senderId, receiverId)
            }.also {
                when (it) {
                    is ResourceUI.Error -> it
                    ResourceUI.Loading -> it
                    is ResourceUI.Resource -> {
                        chatsData.addAll(it.data?.content.orEmpty())
                    }
                }
            }

            liveData.postValue(parseUiModel(chatsData))
        }
    }

    fun sendMessage(request: ChatMessageRequest) {
        viewModelScope.launch {
            liveChatData.value = ResourceUI.Loading
            liveChatData.value = handleResponse {
                apiService.sendData(request)
            }.also {
                when (it) {
                    is ResourceUI.Error -> it
                    ResourceUI.Loading -> it
                    is ResourceUI.Resource -> {
                        chatsData.add(it.data?:ChatMessageData())
                    }
                }
            }
            liveData.postValue(parseUiModel(chatsData))
        }
    }
    fun sendLocation(request: ChatMessageRequest){
        viewModelScope.launch {
            liveChatData.value = ResourceUI.Loading
            liveChatData.value = handleResponse {
                apiService.sendData(request)
            }.also {
                when (it) {
                    is ResourceUI.Error -> it
                    ResourceUI.Loading -> it
                    is ResourceUI.Resource -> {
                        chatsData.add(it.data?:ChatMessageData())
                    }
                }
            }
            liveData.postValue(parseUiModel(chatsData))
        }
    }

    private fun parseUiModel(it: List<ChatMessageData>): List<ChatMessageData> {
        val mList = mutableListOf<ChatMessageData>()
        var i = 0
        var j = 0
        var model = if (it.isEmpty()){
            EmptyMessagesData()
        }else{
            ChatMessageData()
        }
        while (j < it.size && i < it.size) {
            if (i == j){
                model = GroupMessagesData(if (it[i].createAt.isNullOrEmpty()) "2000-01-01" else it[i].createAt.toString())
                mList.add(model)
            }
            if (isSomeDayInstant(it[i].createAt,it[j].createAt)) {
                mList.add(it[j])
                Timber.d("$TAG ${model.createAt}")
                j += 1
            } else {
                i = j
            }
        }
        if (mList.isEmpty()) mList.add(model)
        return mList
    }

}