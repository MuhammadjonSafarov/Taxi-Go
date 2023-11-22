package uz.xia.taxi.ui.participants.chat

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxi.R
import uz.xia.taxi.common.webSocketAddress
import uz.xia.taxi.databinding.FragmentChatMessageBinding
import uz.xia.taxi.ui.participants.chat.adapter.ChatMessageAdapter
import uz.xia.taxi.utils.isCheckStoragePermission
import uz.xia.taxi.utils.lazyFast
import uz.xia.taxi.utils.websocket.ChatDeliver
import uz.xia.taxi.utils.websocket.ChatListener
import uz.xia.taxi.utils.websocket.StompMessageSerializer

private const val STORAGE_REQUEST_CODE = 1009

@AndroidEntryPoint
class ChatMessageFragment : Fragment() {
    private var _binding: FragmentChatMessageBinding? = null
    private val binding get() = _binding!!
    private val chatAdapter by lazyFast { ChatMessageAdapter("2") }
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMessage.adapter = chatAdapter
        val chatListener = ChatListener("2", "3")
        val topicHandler = chatListener.subscribe("/topics/event/2/3")
        topicHandler.addListener { message ->
            requireActivity().runOnUiThread {
                val stompMessageSerializer = StompMessageSerializer()
                val chats = stompMessageSerializer.putChatListStompMessageToListOfChats(message)
                chatAdapter.submitList(chats)
            }
        }
        chatListener.connect(webSocketAddress)

        binding.messageSendButton.setOnClickListener {
            val messageEditText = binding.etMessage
            if (messageEditText.text.toString() == "") {
                Toast.makeText(
                    requireContext(), "You can not send empty message", Toast.LENGTH_SHORT
                ).show()
            } else {
                val chatDeliver = ChatDeliver("2", "3", messageEditText.text.toString())
                chatDeliver.connect(webSocketAddress)
                chatDeliver.disconnect()
            }
            messageEditText.setText("")
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST_CODE && grantResults[0] == Activity.RESULT_OK) {
            navController.navigate(R.id.nav_add_choose_file)
        }else{
            //todo permission denid
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
