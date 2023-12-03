import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android.sabsigan.R
import com.android.sabsigan.data.ChatMessage
import com.android.sabsigan.data.GlideApp
import com.android.sabsigan.databinding.AdapterMyMessageBinding
import com.android.sabsigan.databinding.AdapterOtherMessageBinding
import com.android.sabsigan.viewModel.ChatViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessageAdapter(private val context: Context, private val viewModel: ChatViewModel): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private var messageList = listOf<ChatMessage>()

    val TYPE_MY = 0
    val TYPE_OTHER = 1

    class MessageViewHolder private constructor(var binding: ViewDataBinding, val viewType: Int) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, viewModel: ChatViewModel, chatMessage: ChatMessage, isVisible: Int) {
            if (viewType == 0) {
                (binding as AdapterMyMessageBinding).isVisible = isVisible
                (binding as AdapterMyMessageBinding).chatMessage = chatMessage
                (binding as AdapterMyMessageBinding).viewModel = viewModel

                if (chatMessage.type == "img") {
                    val uri = viewModel.imgMap.get(chatMessage.id)
                    val imageView = (binding as AdapterMyMessageBinding).imgView

                    if (uri != null) {
                        Log.d("테스트", "$uri")

                        GlideApp.with(context)
                            .load(uri)
                            .override(800, 800)
                            .into(imageView)
                    }
                }

                binding.executePendingBindings()
            } else {
                (binding as AdapterOtherMessageBinding).isVisible = isVisible
                (binding as AdapterOtherMessageBinding).chatMessage = chatMessage
                (binding as AdapterOtherMessageBinding).viewModel = viewModel
                (binding as AdapterOtherMessageBinding).civ.setImageBitmap(viewModel.generateAvatar(chatMessage.uid))

                if (chatMessage.type == "img") {
                    val uri = viewModel.imgMap.get(chatMessage.id)
                    val imageView = (binding as AdapterOtherMessageBinding).imgView

                    if (uri != null) {
                        Log.d("테스트", "$uri")

                        GlideApp.with(context)
                            .load(uri)
                            .override(800, 800)
                            .into(imageView)
                    }
                }

                binding.executePendingBindings()
            }
        }


        companion object {
            fun from(parent: ViewGroup, viewType: Int) : MessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = if (viewType == 0) AdapterMyMessageBinding.inflate(layoutInflater, parent, false)
                else  AdapterOtherMessageBinding.inflate(layoutInflater, parent, false)

                return MessageViewHolder(binding, viewType)
            }
        }
    }
    fun setMessageList(list: List<ChatMessage>) {
        messageList = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if(messageList[position].uid.equals(viewModel.getUID())) {
            Log.d("ViewType", "0")
            return TYPE_MY;
        } else {
            Log.d("ViewType", "1")
            return TYPE_OTHER;
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.from(parent, viewType)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        // 비트연산 0은 아무것도 x, 0x1 시간, 0x2는 이미지, 이름, 0x3는 다
        val time = 0x1
        val image = 0x2
        val date = 0x4

        var isVisible = 0

        val currentMsgTime = messageList[position].getLastMsgTime()
        val currentMsgType = getItemViewType(position)
        val currentMsgUID = messageList[position].uid

        if (position > 0) {
            val preMsgTime = messageList[position-1].getLastMsgTime()
            val preMsgType = getItemViewType(position-1)
            val preMsgUID = messageList[position-1].uid

            val preDate = messageList[position-1].created_at.split(" ")[0]
            val currentDate = messageList[position].created_at.split(" ")[0]

            Log.d("test", "$preDate $currentDate")

            if (currentMsgType != preMsgType || !currentMsgTime.equals(preMsgTime) || currentMsgUID != preMsgUID)
                isVisible = isVisible or image

            if (preDate != currentDate)
                isVisible = isVisible or date
        } else
            isVisible = isVisible or date

        if (position < messageList.size-1 ) {
            val nextMsgTime = messageList[position+1].getLastMsgTime()
            val nextMsgType = getItemViewType(position+1)
            val nextMsgUID = messageList[position+1].uid

            if (currentMsgType != nextMsgType || !currentMsgTime.equals(nextMsgTime) || currentMsgUID != nextMsgUID)
                isVisible = isVisible or time

        } else
            isVisible = isVisible or time

        holder.bind(context, viewModel, messageList[position], isVisible)
    }

    override fun getItemCount() = messageList.size
}