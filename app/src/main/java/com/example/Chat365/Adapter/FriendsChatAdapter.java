package com.example.Chat365.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Model.Message;
import com.example.Chat365.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsChatAdapter extends RecyclerView.Adapter<FriendsChatAdapter.ViewHolder> {
    List<Message> listChat;
    Context mContext;

    public FriendsChatAdapter(List<Message> listChat) {
        this.listChat = listChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.itemchatgroupprivate,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Message message = listChat.get(i);
        if(!message.getUser().getLinkAvatar().isEmpty()){
            Glide.with(mContext)
                    .load(message.getUser().getLinkAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.spinne_loading)
                    .into(viewHolder.cvAvt);

        }
        viewHolder.tvTimeSend.setText(message.getTime());
        viewHolder.tvMessage.setText(message.getMessage());
        if(message.getIsseen().equals("true")){
            viewHolder.tvPeopleSend.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage,tvTimeSend,tvPeopleSend;
        CircleImageView cvAvt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvPeopleSend = itemView.findViewById(R.id.tvPeopleWatch);
            tvTimeSend = itemView.findViewById(R.id.tvTimeSend);
            cvAvt = itemView.findViewById(R.id.cvAvt);
        }
    }
}
