package chat.goulmima.com.letschat.DataAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import chat.goulmima.com.letschat.R;
import chat.goulmima.com.letschat.POJOS.TextMessage;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private static final int VIEW_TYPE_RECEIVED = 1;
    private static final int VIEW_TYPE_SENT = 0;
    public ArrayList<TextMessage> messagesList;
    private Context context;
    private String senderID;
    private String receiverPhoto;

    public MessagesAdapter(ArrayList<TextMessage> messages,Context pContext,String senderUid,String receiverPhotoUrl){
        context = pContext;
        senderID = senderUid;
        receiverPhoto = receiverPhotoUrl;
        messagesList=new ArrayList<>();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_SENT) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.rv_message_sent, viewGroup, false);
            return new MessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.rv_message_received, viewGroup, false);
            return new MessageViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesList.get(position).getmSenderUserID() == senderID)
        {
            return VIEW_TYPE_SENT;
        }
        else
            return VIEW_TYPE_RECEIVED;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        if(messagesList.get(i).getmSenderUserID() != senderID)
        {
            Glide.with(context).load(receiverPhoto).into(messageViewHolder.ivProfile);
        }
            messageViewHolder.messageText.setText(messagesList.get(i).getmText());

    }

    @Override
    public int getItemCount() {
        if (messagesList == null ) return 0;
        return messagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView ivProfile;
        public TextView messageText;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tv_MsgText_item);
            ivProfile = itemView.findViewById(R.id.iv_profile_rec);
        }
    }
}
