package uz.xia.taxigo.ui.participants

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.xia.taxigo.R
import uz.xia.taxigo.data.IPreference
import uz.xia.taxigo.data.remote.model.paging.PagingResponse
import uz.xia.taxigo.data.remote.model.user.UserData
import uz.xia.taxigo.databinding.FragmentParticipantsBinding
import uz.xia.taxigo.ui.participants.adapter.IParticipantsListener
import uz.xia.taxigo.ui.participants.adapter.ParticipantsAdapter
import uz.xia.taxigo.utils.lazyFast
import uz.xia.taxigo.utils.websocket.UserListListener
import uz.xia.taxigo.utils.widget.base.BaseFragment
import uz.xia.taxigo.utils.widget.base.ResourceUI
import javax.inject.Inject


private const val TAG = "ParticipantsFragment"

@AndroidEntryPoint
class ParticipantsFragment : BaseFragment(R.layout.fragment_participants), IParticipantsListener {
    private val binding: FragmentParticipantsBinding by viewBinding()
    private val participantAdapter by lazyFast { ParticipantsAdapter(this) }
    private val viewModel: IParticipantsViewModel by viewModels<ParticipantsViewModel>()
    private val userListListener = UserListListener()
    @Inject
    lateinit var preference: IPreference
    private val navController by lazyFast {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment_content_main
        )
    }


    private val usersObserver = Observer<ResourceUI<PagingResponse<List<UserData>>>?> {
        when (it) {
            is ResourceUI.Error -> {}
            ResourceUI.Loading -> {}
            is ResourceUI.Resource -> {
                val list = mutableListOf<UserData>()
                it.data?.content.orEmpty().forEach {
                    if (it.id != preference.userId){
                        list.add(it)
                    }
                }
                participantAdapter.submitList(list)
            }

            else -> {}
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadAllUsers()
    }

    override fun setup() {
        super.setup()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvParticipants.layoutManager = layoutManager
        binding.rvParticipants.adapter = participantAdapter

        // Resource (res/raw)
        //binding.waveformSeekBar.setSampleFrom(R.raw.song)
        // Url
        /*val mediaPlayer = MediaPlayer.create(requireContext(),R.raw.song)
        try {
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    override fun observe() {
        super.observe()
        viewModel.liveData.observe(viewLifecycleOwner, usersObserver)

        /*val topicHandler = userListListener.subscribe("/topics/userList")
        topicHandler.addListener { message ->
            requireActivity().runOnUiThread {
                Timber.d("$TAG message $message")
                val stompMessageSerializer = StompMessageSerializer()
                val userList = stompMessageSerializer.putUserListStompMessageToListOfUsers(message, User())
                participantAdapter.submitList(userList)

            }
        }
        userListListener.connect(webSocketAddress)*/
    }

    override fun onItemClick(id: Long) {
        val bundle = bundleOf(Pair("key_receiver_id", id))
        navController.navigate(R.id.nav_message, bundle)
    }

    override fun onPause() {
        super.onPause()
        if (userListListener.isConnected) {
            userListListener.disconnect()
        }
    }

}
