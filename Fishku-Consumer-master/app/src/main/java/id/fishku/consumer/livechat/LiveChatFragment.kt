package id.fishku.consumer.livechat

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
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.domain.params.CreateParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.SellerReadData
import id.fishku.consumer.utils.DateFormatUtils.getFormattedTime
import id.fishku.consumer.databinding.FragmentLiveChatBinding
import id.fishku.consumer.databinding.UserListChatBinding
import id.fishku.consumer.utils.Constants.TIME_IN_MILLIS

@AndroidEntryPoint
class LiveChatFragment : Fragment() {

    private var _binding: FragmentLiveChatBinding? = null
    private val binding get() = _binding!!
    private var userEmail: String? = null
    private val viewModel: LiveChatViewModel by viewModels()
    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>

    companion object {
        const val CHAT_KEY = "CHAT_KEY_DATA"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserEmail()

        binding.swiperefresh.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue
            )
        )

        listenUserChats()
        setUpChats()
        binding.swiperefresh.setOnRefreshListener {
            getSellerUser()
        }


    }

    private fun listenUserChats() {
        viewModel.isListenUser.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    getSellerUser()
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun getSellerUser(){
        viewModel.getSellerChats(userEmail!!)
    }

    private fun setUpChats() {

        groupAdapter = GroupAdapter<GroupieViewHolder>()
        viewModel.sellerChats.observe(viewLifecycleOwner) { chats ->
            when (chats) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    getUserEmail()
                    if (chats.data!!.isNotEmpty()) {
                        groupAdapter.clear()
                        groupAdapter.addAll(chats.data!!.toUserSellerITem())

                        groupAdapter.setOnItemClickListener { item, _ ->
                            val data = item as SellerItem
                            setUpToRoomChat(data.seller.sellerData.email)
                        }
                        binding.recyclerviewMessage.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(context)
                            adapter = groupAdapter
                        }

                    }else{
                        binding.noChat.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {

                }
            }
        }

    }

    private fun getUserEmail() {
        viewModel.getEmail.observe(viewLifecycleOwner) {
            userEmail = it
            viewModel.getListenerUser(it)
        }
    }

    private fun setUpToRoomChat(sellerEmail: String) {
        if (userEmail != null) {
            val createParams = CreateParams(userEmail!!, sellerEmail)
            viewModel.createConnection(createParams).observe(viewLifecycleOwner) { args ->
                when (args) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                       if (args.data != null) {
                           readMessage(args.data!!.chatId)
                           val intent = Intent(activity, ChatRoomActivity::class.java)
                           intent.putExtra(CHAT_KEY, args.data)
                           startActivity(intent)
                       }

                    }
                    is Resource.Error -> {}
                }
            }
        }
    }

    private fun readMessage(chatId: String) {
        if (userEmail != null){
            val readParams =  ReadParams(chatId, userEmail!!)
            viewModel.readMessage(readParams)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

fun List<SellerReadData>.toUserSellerITem(): List<SellerItem> {
    return this.map {
        SellerItem(it)
    }
}

open class SellerItem(val seller: SellerReadData) : BindableItem<UserListChatBinding>() {
    override fun bind(viewBinding: UserListChatBinding, position: Int) {
        viewBinding.name.text = seller.sellerData.name
        viewBinding.subtitle.text = seller.sellerData.lastMessage
        if (seller.chatUser.total_unread != 0)
            viewBinding.tvUnread.text = seller.chatUser.total_unread.toString()
        else
            viewBinding.tvUnread.visibility = View.GONE
        viewBinding.tvTime.text = getFormattedTime(seller.sellerData.lastTime!!.toDate().time / TIME_IN_MILLIS)
    }

    override fun getLayout(): Int = R.layout.user_list_chat

    override fun initializeViewBinding(view: View): UserListChatBinding =
        UserListChatBinding.bind(view)

}