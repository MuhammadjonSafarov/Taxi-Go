package uz.xia.taxigo.ui.participants.chat

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxigo.R
import uz.xia.taxigo.common.webSocketAddress
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.model.chat.ChatMessageData
import uz.xia.taxigo.data.remote.model.chat.EmptyMessagesData
import uz.xia.taxigo.data.remote.model.chat.GroupMessagesData
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.databinding.FragmentChatMessageBinding
import uz.xia.taxigo.ui.participants.chat.adapter.ChatMessageAdapter
import uz.xia.taxigo.ui.participants.chat.location.LocationSelectFragment
import uz.xia.taxigo.utils.isCheckStoragePermission
import uz.xia.taxigo.utils.isSomeDayInstant
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.websocket.ChatListener
import uz.xia.taxigo.utils.websocket.StompMessageSerializer
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject

private const val STORAGE_REQUEST_CODE = 1009

private const val TAG = "ChatMessageFragment"

@AndroidEntryPoint
class ChatMessageFragment : BaseFragment(R.layout.fragment_chat_message) {
    private val binding: FragmentChatMessageBinding by viewBinding()
    private val viewModel: ChatMessageViewModel by viewModels()

    @Inject
    lateinit var preference: IPreference

    private var senderId: Long = 0L
    private val chatAdapter by lazyFast { ChatMessageAdapter(1) }
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }

    private val dataObserver = Observer<ResourceUI<PagingResponse<List<ChatMessageData>>>?> {
        when (it) {
            is ResourceUI.Error -> toast(it.error.message ?: "")
            ResourceUI.Loading -> {}
            is ResourceUI.Resource -> {
                val  list = parseUiModel(it.data?.content.orEmpty())
                chatAdapter.submitList(list)
                binding.rvMessage.smoothScrollToPosition(list.size)
            }
            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        senderId = arguments?.getLong("key_receiver_id") ?: 0L
        viewModel.chatsData(senderId, preference.userId)
    }

    override fun setup() {
        super.setup()
        binding.rvMessage.adapter = chatAdapter
    }

    override fun observe() {
        super.observe()
        viewModel.liveChatsData.observe(viewLifecycleOwner, dataObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatListener = ChatListener("2", "3")
        val topicHandler = chatListener.subscribe("/topics/event/2/3")
        topicHandler.addListener { message ->
            requireActivity().runOnUiThread {
                Timber.d(TAG, "Message : $message")
                val stompMessageSerializer = StompMessageSerializer()
                // val chats = stompMessageSerializer.putChatListStompMessageToListOfChats(message)
                //  chatAdapter.submitList(chats)
            }
        }
        chatListener.connect(webSocketAddress)

        binding.messageSendButton.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(
                    requireContext(), "You can not send empty message", Toast.LENGTH_SHORT
                ).show()
            } else {
                // val chatDeliver = ChatDeliver("2", "3", messageEditText.text.toString())
                viewModel.sendData(
                    ChatMessageData(
                        sender = preference.userId,
                        receiver = 3,
                        content = message,
                        type = "TEXT"
                    )
                )
                binding.etMessage.setText("")
            }
        }
        binding.selectFile.setOnClickListener {
            if (requireContext().isCheckStoragePermission()) {
                navController.navigate(R.id.nav_add_choose_file)
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ), STORAGE_REQUEST_CODE
                )
            }
        }
        binding.selectLocation.setOnClickListener {
            LocationSelectFragment().show(childFragmentManager,"location")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST_CODE && grantResults[0] == Activity.RESULT_OK) {
            navController.navigate(R.id.nav_add_choose_file)
        } else {
            //todo permission denid
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
                model = GroupMessagesData(if (it[i].updateAt.isNullOrEmpty()) "2000-01-01" else it[i].updateAt.toString())
                mList.add(model)
            }
            if (isSomeDayInstant(it[i].updateAt,it[j].updateAt)) {
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
