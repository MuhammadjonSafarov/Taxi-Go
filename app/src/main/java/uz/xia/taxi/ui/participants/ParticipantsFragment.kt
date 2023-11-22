package uz.xia.taxi.ui.participants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.xia.taxi.R
import uz.xia.taxi.common.webSocketAddress
import uz.xia.taxi.databinding.FragmentParticipantsBinding
import uz.xia.taxi.ui.participants.adapter.IParticipantsListener
import uz.xia.taxi.ui.participants.adapter.ParticipantsAdapter
import uz.xia.taxi.utils.lazyFast
import uz.xia.taxi.utils.websocket.StompMessageSerializer
import uz.xia.taxi.utils.websocket.UserListListener
import uz.xia.taxi.utils.websocket.model.User


private const val TAG = "ParticipantsFragment"

@AndroidEntryPoint
class ParticipantsFragment : Fragment(), IParticipantsListener {
    private var _binding: FragmentParticipantsBinding? = null
    private val binding get() = _binding!!
    private val participantAdapter by lazyFast { ParticipantsAdapter(this) }
    private val userListListener=UserListListener()
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParticipantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setObserver()
    }

    private fun setUpViews() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvParticipants.layoutManager = layoutManager
        binding.rvParticipants.adapter = participantAdapter
    }

    private fun setObserver() {
        val topicHandler = userListListener.subscribe("/topics/userList")
        topicHandler.addListener { message ->
            requireActivity().runOnUiThread {
                val stompMessageSerializer = StompMessageSerializer()
                val userList = stompMessageSerializer.putUserListStompMessageToListOfUsers(message, User())
                participantAdapter.submitList(userList)
                Timber.d("$TAG $userList message $message")
            }
        }
        userListListener.connect(webSocketAddress)
    }

    override fun onItemClick() {
        navController.navigate(R.id.nav_message)
    }

    override fun onPause() {
        super.onPause()
        if (userListListener.isConnected) {
            userListListener.disconnect()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
