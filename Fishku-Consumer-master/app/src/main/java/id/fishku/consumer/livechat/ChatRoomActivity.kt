package id.fishku.consumer.livechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.consumer.R
import id.fishku.consumer.core.data.Resource
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.data.source.remote.request.DataNotification
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.domain.params.NewChatParams
import id.fishku.consumer.core.domain.params.ReadParams
import id.fishku.consumer.core.domain.model.ChatArgs
import id.fishku.consumer.core.domain.model.Message
import id.fishku.consumer.utils.DateFormatUtils.getFormattedTimeChatLog
import id.fishku.consumer.databinding.ActivityChatRoomBinding
import id.fishku.consumer.databinding.ChatFromRowBinding
import id.fishku.consumer.databinding.ChatToRowBinding
import id.fishku.consumer.utils.Constants.MIN_SCROLL_POSITION
import id.fishku.consumer.utils.Constants.TIME_IN_MILLIS
import javax.inject.Inject

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding

    private val viewModel: ChatRoomViewModel by viewModels()
    private var userEmail: String? = null
    private var token: String? = null
    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>

    @Suppress("DEPRECATION")
    private val sellerArgs: ChatArgs?
        get() = intent.getParcelableExtra(LiveChatFragment.CHAT_KEY)

    @Inject
    lateinit var saveToLocal: LocalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarChat)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        getUserEmail()
        listenIsMessageSend()
        getTextMessage()
        populateMessage()
        setUpConsumerData()
        getConsumerData()
        println(sellerArgs?.sellerEmail)
        binding.sendButtonChatLog.setOnClickListener {
            sendMessage()
        }
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun getConsumerData(){
        viewModel.seller.observe(this){
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    supportActionBar?.title = it.data?.name
                    token = it.data?.token
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun setUpConsumerData(){
        if (sellerArgs!=null){
            val consumerEmail = sellerArgs!!.sellerEmail
            viewModel.getSellerChat(consumerEmail)
        }
    }

    private fun populateMessage(){
        groupAdapter = GroupAdapter<GroupieViewHolder>()
        viewModel.messages.observe(this){ message ->
            when(message){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    groupAdapter.clear()
                    for (msg in message.data!!){
                        readMessage()
                        if (msg.sender == userEmail){
                            groupAdapter.add(ChatToItem(msg))
                        }else{
                            groupAdapter.add(ChatFromItem(msg))
                        }
                    }

                    binding.recyclerviewChatLog.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = groupAdapter
                    }
                    binding.recyclerviewChatLog.scrollToPosition(groupAdapter.itemCount -1)
                }
                is Resource.Error -> {}
            }
        }
    }

    private fun readMessage(){
        if (sellerArgs != null && userEmail != null){
            val readParams = ReadParams(sellerArgs!!.chatId, userEmail!!)
            viewModel.readMessage(readParams)
        }
    }

    private fun getTextMessage() {
        if (sellerArgs != null) {
            viewModel.getMessageList(sellerArgs!!.chatId)
        }
    }

    private fun sendMessage() {
        val msg = binding.edittextChatLog.text.trim().toString()
        if (userEmail != null && sellerArgs != null) {
            val newChatParams = NewChatParams(userEmail!!, sellerArgs!!, msg)
            if (msg.isNotEmpty()){
                viewModel.writeMessage(newChatParams)
                sendNotify(msg)
            }
            binding.edittextChatLog.apply {
                clearFocus()
                text.clear()
            }
            if (groupAdapter.itemCount >= MIN_SCROLL_POSITION){
                binding.recyclerviewChatLog.smoothScrollToPosition(groupAdapter.itemCount -1)
            }
        }
    }

    private fun sendNotify(msg: String) {
        val user = saveToLocal.getDataUser()
        if (token != null){
            val notification = NotificationRequest(
                data = DataNotification(user.name!!, msg,user.email, sellerArgs!!.chatId),
                registration_ids = listOf("$token")
            )
            viewModel.pushNotification(notification)
        }
    }

    private fun getUserEmail() {
        viewModel.getEmail.observe(this) {
            userEmail = it
        }
    }

    private fun listenIsMessageSend() {
        viewModel.newMessage.observe(this) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {}
                is Resource.Error -> {}
            }
        }
    }
}

class ChatFromItem(private val fromMessage: Message) : BindableItem<ChatFromRowBinding>() {
    override fun bind(viewBinding: ChatFromRowBinding, position: Int) {
        viewBinding.textviewFromRow.text = fromMessage.msg
        viewBinding.fromMsgTime.text = getFormattedTimeChatLog(fromMessage.timeUpdate!!.toDate().time /TIME_IN_MILLIS)
    }

    override fun getLayout(): Int = R.layout.chat_from_row

    override fun initializeViewBinding(view: View): ChatFromRowBinding =
        ChatFromRowBinding.bind(view)
}

class ChatToItem(private val toMessage: Message) : BindableItem<ChatToRowBinding>() {
    override fun bind(viewBinding: ChatToRowBinding, position: Int) {
        viewBinding.textviewToRow.text = toMessage.msg
        viewBinding.toMsgTime.text = getFormattedTimeChatLog(toMessage.timeUpdate!!.toDate().time /TIME_IN_MILLIS)
    }

    override fun getLayout() = R.layout.chat_to_row

    override fun initializeViewBinding(view: View): ChatToRowBinding =
        ChatToRowBinding.bind(view)
}