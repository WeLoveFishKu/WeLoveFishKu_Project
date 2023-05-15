package id.fishku.fisherseller.presentation.ui.livechat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.FragmentLiveChatBinding
import id.fishku.fisherseller.databinding.UserListChatBinding
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.ConsumerReadData
import id.fishku.fishersellercore.util.Constants.TIME_IN_MILLIS
import id.fishku.fishersellercore.util.DateFormatUtils.getFormattedTime
import javax.inject.Inject

@AndroidEntryPoint
class LiveChatFragment : Fragment() {

    private var _binding: FragmentLiveChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LiveChatViewModel by viewModels()
    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>

    companion object {
        const val CHAT_KEY = "CHAT_KEY"
    }

    @Inject
    lateinit var prefs: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swiperefresh.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )

        listenUserChats()
        setUpListenUser()

        binding.swiperefresh.setOnRefreshListener {
            setUpChats()
        }


    }

    private fun listenUserChats() {
        viewModel.isListenUser.observe(viewLifecycleOwner) {

            when (it) {
                is ResourceState.Loading -> {}
                is ResourceState.Success -> {
                    setUpChats()
                }
                is ResourceState.Error -> {}
            }
        }
    }


    private fun setUpListenUser() {
        val userEmail = prefs.getUser().email
        if (userEmail != null) {
            viewModel.getListenerUser(userEmail)
        }
    }

    private fun setUpChats() {
        groupAdapter = GroupAdapter<GroupieViewHolder>()
        val userEmail = prefs.getUser().email
        if (userEmail != null) {
            viewModel.getSellerChats(userEmail).observe(viewLifecycleOwner) { chats ->
                when (chats) {
                    is ResourceState.Loading -> {}
                    is ResourceState.Success -> {
                        if (chats.data!!.isNotEmpty()){
                            groupAdapter.clear()
                            groupAdapter.addAll(chats.data!!.toUserSellerITem())
                            groupAdapter.setOnItemClickListener { item, _ ->
                                val data = item as SellerItem
                                setUpToRoomChat(data.consumer.consumerData.email)
                            }
                            binding.recyclerviewMessage.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(context)
                                adapter = groupAdapter
                            }
                            binding.swiperefresh.isRefreshing = false
                        }else {
                            binding.noChat.visibility = View.VISIBLE
                        }
                    }
                    is ResourceState.Error -> {
                        binding.swiperefresh.isRefreshing = false
                    }
                }
            }
        }

    }

    private fun setUpToRoomChat(consumerEmail: String) {
        val intent = Intent(activity, ChatRoomActivity::class.java)
        intent.putExtra(CHAT_KEY, consumerEmail)
        startActivity(intent)
    }
}

fun List<ConsumerReadData>.toUserSellerITem(): List<SellerItem> {
    return this.map {
        SellerItem(it)
    }
}

open class SellerItem(val consumer: ConsumerReadData) : BindableItem<UserListChatBinding>() {
    override fun bind(viewBinding: UserListChatBinding, position: Int) {
        viewBinding.name.text = consumer.consumerData.name
        viewBinding.subtitle.text = consumer.consumerData.lastMessage
        if (consumer.chatUser.total_unread != 0)
            viewBinding.tvUnread.text = consumer.chatUser.total_unread.toString()
        else
            viewBinding.tvUnread.visibility = View.GONE
        viewBinding.tvTime.text =
            getFormattedTime(consumer.consumerData.lastTime!!.toDate().time / TIME_IN_MILLIS)
    }

    override fun getLayout(): Int = R.layout.user_list_chat

    override fun initializeViewBinding(view: View): UserListChatBinding =
        UserListChatBinding.bind(view)

}