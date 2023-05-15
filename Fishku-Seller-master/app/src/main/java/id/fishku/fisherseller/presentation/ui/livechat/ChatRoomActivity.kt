package id.fishku.fisherseller.presentation.ui.livechat

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.viewbinding.BindableItem
import dagger.hilt.android.AndroidEntryPoint
import id.fishku.fisherseller.R
import id.fishku.fisherseller.databinding.ActivityChatRoomBinding
import id.fishku.fisherseller.databinding.ChatFromRowBinding
import id.fishku.fisherseller.databinding.ChatToRowBinding
import id.fishku.fisherseller.seller.domain.params.CreateParams
import id.fishku.fisherseller.seller.domain.params.NewChatParams
import id.fishku.fisherseller.seller.domain.params.ReadParams
import id.fishku.fisherseller.seller.services.SessionManager
import id.fishku.fishersellercore.core.ResourceState
import id.fishku.fishersellercore.model.ChatArgs
import id.fishku.fishersellercore.model.Message
import id.fishku.fishersellercore.requests.DataNotification
import id.fishku.fishersellercore.requests.NotificationRequest
import id.fishku.fishersellercore.util.Constants.MIN_SCROLL_POSITION
import id.fishku.fishersellercore.util.Constants.TIME_IN_MILLIS
import id.fishku.fishersellercore.util.DateFormatUtils.getFormattedTimeChatLog
import javax.inject.Inject

@AndroidEntryPoint
class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding

    private val viewModel: ChatRoomViewModel by viewModels()

    private lateinit var groupAdapter: GroupAdapter<GroupieViewHolder>

    private var token: String? = null
    private var chatArgs: ChatArgs? = null

    @Suppress("DEPRECATION")
    private val consumerArgs: String?
        get() = intent.getStringExtra(LiveChatFragment.CHAT_KEY)

    @Inject
    lateinit var prefs: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarChat)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)

        }

        listenIsMessageSend()

        populateMessage()
        setUpConsumerData()
        getConsumerData()
        initConnection()
        binding.sendButtonChatLog.setOnClickListener {
            sendMessage()
        }
    }

    private fun initConnection(){
        if (consumerArgs != null){
            val userEmail = prefs.getUser().email
            val createParams = userEmail?.let { CreateParams(it, consumerArgs!!) }
            if (createParams != null) {
                viewModel.createConnection(createParams).observe(this) { args ->
                    when (args) {
                        is ResourceState.Loading -> {}
                        is ResourceState.Success -> {
                            if (args.data != null){
                                readMessage(args.data!!.chatId)
                                this.chatArgs = args.data
                                getTextMessage(args.data!!.chatId)
                            }
                        }
                        is ResourceState.Error -> {}
                    }
                }
            }
        }

    }

    private fun readMessage(chatId: String) {
        val userEmail = prefs.getUser().email!!
        val readParams = ReadParams(chatId, userEmail)
        viewModel.readMessage(readParams)
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun getConsumerData() {
        viewModel.consumer.observe(this) {
            when (it) {
                is ResourceState.Loading -> {}
                is ResourceState.Success -> {
                    supportActionBar?.title = it.data?.name
                    token = it.data?.token
                }
                is ResourceState.Error -> {}
            }
        }
    }

    private fun setUpConsumerData() {
        val consumerEmail = consumerArgs
        println(consumerEmail)
        if (consumerEmail != null) {
            viewModel.getConsumerChat(consumerEmail)
        }
    }

    private fun populateMessage() {
        groupAdapter = GroupAdapter<GroupieViewHolder>()
        viewModel.messages.observe(this) { message ->
            when (message) {
                is ResourceState.Loading -> {}
                is ResourceState.Success -> {
                    groupAdapter.clear()
                    val userEmail = prefs.getUser().email!!
                    for (msg in message.data!!) {

                        if (msg.sender == userEmail) {
                            groupAdapter.add(ChatToItem(msg))
                        } else {
                            groupAdapter.add(ChatFromItem(msg))
                        }
                    }

                    binding.recyclerviewChatLog.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(context)
                        adapter = groupAdapter
                    }
                    binding.recyclerviewChatLog.scrollToPosition(groupAdapter.itemCount - 1)
                }
                is ResourceState.Error -> {}
            }
        }
    }

//    private fun readMessage() {
//        val userEmail = prefs.getUser().email
//        if (consumerArgs != null && userEmail != null) {
//            val readParams = ReadParams(chatArgs!!.chatId, userEmail)
//            viewModel.readMessage(readParams)
//        }
//    }

    private fun getTextMessage(chatId: String?) {
        chatId?.let { viewModel.getMessageList(it) }

    }

    private fun sendMessage() {
        val msg = binding.edittextChatLog.text.trim().toString()
        val userEmail = prefs.getUser().email
        val newChatParams = userEmail?.let { NewChatParams(it, chatArgs!!, msg) }
        if (newChatParams != null && msg.isNotEmpty()) {
            viewModel.writeMessage(newChatParams)
            sendNotify(msg)
        }
        binding.edittextChatLog.apply {
            clearFocus()
            text.clear()
        }
        if (groupAdapter.itemCount >= MIN_SCROLL_POSITION) {
            binding.recyclerviewChatLog.smoothScrollToPosition(groupAdapter.itemCount - 1)
        }
    }

    private fun sendNotify(msg: String) {
        val user = prefs.getUser()

        if (token != null){
            val notification = NotificationRequest(
                data = DataNotification(user.name!!, msg, user.email!!, chatArgs!!.chatId),
                registration_ids = listOf("$token")
            )
            viewModel.pushNotification(notification)
        }
    }


    private fun listenIsMessageSend() {
        viewModel.newMessage.observe(this) {
            when (it) {
                is ResourceState.Loading -> {}
                is ResourceState.Success -> {}
                is ResourceState.Error -> {}
            }
        }
    }
}

class ChatFromItem(private val fromMessage: Message) : BindableItem<ChatFromRowBinding>() {
    override fun bind(viewBinding: ChatFromRowBinding, position: Int) {
        viewBinding.textviewFromRow.text = fromMessage.msg
        viewBinding.fromMsgTime.text =
            getFormattedTimeChatLog(fromMessage.timeUpdate!!.toDate().time / TIME_IN_MILLIS)
    }

    override fun getLayout(): Int = R.layout.chat_from_row

    override fun initializeViewBinding(view: View): ChatFromRowBinding =
        ChatFromRowBinding.bind(view)
}

class ChatToItem(private val toMessage: Message) : BindableItem<ChatToRowBinding>() {
    override fun bind(viewBinding: ChatToRowBinding, position: Int) {
        viewBinding.textviewToRow.text = toMessage.msg
        viewBinding.toMsgTime.text =
            getFormattedTimeChatLog(toMessage.timeUpdate!!.toDate().time / TIME_IN_MILLIS)
    }

    override fun getLayout() = R.layout.chat_to_row

    override fun initializeViewBinding(view: View): ChatToRowBinding =
        ChatToRowBinding.bind(view)
}