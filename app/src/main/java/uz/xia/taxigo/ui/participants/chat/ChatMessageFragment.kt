package uz.xia.taxigo.ui.participants.chat

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.model.chat.ChatMessageData
import uz.xia.taxigo.data.remote.model.chat.ChatMessageRequest
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.databinding.FragmentChatMessageBinding
import uz.xia.taxigo.ui.participants.chat.adapter.ChatMessageAdapter
import uz.xia.taxigo.ui.participants.chat.adapter.IMessageListener
import uz.xia.taxigo.utils.isCheckStoragePermission
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI
import java.util.Date
import javax.inject.Inject

private const val STORAGE_REQUEST_CODE = 1009

private const val TAG = "ChatMessageFragment"

@AndroidEntryPoint
class ChatMessageFragment : BaseFragment(R.layout.fragment_chat_message), IMessageListener {
    private val binding: FragmentChatMessageBinding by viewBinding()
    private val viewModel: ChatMessageViewModel by viewModels()

    @Inject
    lateinit var preference: IPreference

    private var receiverId: Long = 0L
    private val chatAdapter by lazyFast { ChatMessageAdapter(preference.userId, this) }
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }

    private val statusObserver = Observer<ResourceUI<PagingResponse<List<ChatMessageData>>>?> {
        when (it) {
            is ResourceUI.Error -> toast(it.error.message ?: "")
            ResourceUI.Loading -> {}
            is ResourceUI.Resource -> {}
            else -> {}
        }
    }
    private val dataObserver = Observer<List<ChatMessageData>> {
        chatAdapter.submitList(it)
        binding.rvMessage.smoothScrollToPosition(it.size)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiverId = arguments?.getLong("key_receiver_id") ?: 0L
        viewModel.chatsData(receiverId, preference.userId)
    }

    override fun setup() {
        super.setup()
        binding.rvMessage.adapter = chatAdapter
        setFragmentResultListener("location_data") { _, bundle ->
            val longitude = bundle.getDouble("key_longitude")
            val latitude = bundle.getDouble("key_latitude")
            val chat = ChatMessageRequest(
                sender = preference.userId,
                receiver = receiverId,
                content = "",
                type = "LOCATION",
                longitude = longitude,
                latitude = latitude,
                createAt = Date().time,
                updateAt = Date().time
            )
            viewModel.sendLocation(chat)
        }
        binding.messageSendButton.setOnClickListener {
            sendMessage()
        }
        binding.selectFile.setOnClickListener {
            sendFileChoose()
        }
        binding.selectLocation.setOnClickListener {
            navController.navigate(R.id.locationMessageSelectFragment)
        }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString()
        if (message.isEmpty()) {
            Toast.makeText(
                requireContext(), "Xabar maydoni bo'sh", Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.sendMessage(
                ChatMessageRequest(
                    sender = preference.userId,
                    receiver = receiverId,
                    content = message,
                    type = "TEXT",
                    createAt = Date().time,
                    updateAt = Date().time
                )
            )
            binding.etMessage.setText("")
        }
    }

    private fun sendFileChoose() {
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

    override fun observe() {
        super.observe()
        viewModel.liveChatsData.observe(viewLifecycleOwner, statusObserver)
        viewModel.liveData.observe(viewLifecycleOwner, dataObserver)
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

    override fun locationClick(longitude: Double, latitude: Double) {
        val bundle = bundleOf(
            Pair("key_longitude", longitude),
            Pair("key_latitude", latitude)
        )
        navController.navigate(R.id.locationMessageDetailFragment, bundle)
    }
}
