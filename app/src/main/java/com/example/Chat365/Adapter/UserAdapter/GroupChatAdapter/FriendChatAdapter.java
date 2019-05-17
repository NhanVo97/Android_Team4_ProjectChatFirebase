package com.example.Chat365.Adapter.UserAdapter.GroupChatAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.Chat365.Fragment.FragmentViewPicture;
import com.example.Chat365.Model.Message;
import com.example.Chat365.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendChatAdapter extends RecyclerView.Adapter<FriendChatAdapter.ViewHolder> {
    List<Message> listChat;
    Context mContext;

    public FriendChatAdapter(List<Message> listChat) {
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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Message message = listChat.get(i);
        if(!message.getUser().getLinkAvatar().isEmpty()){
            Picasso.get().load(message.getUser().getLinkAvatar()).into(viewHolder.cvAvt);
        }
        if(message.getMessage().startsWith("https://firebasestorage.googleapis.com/")){
            Glide.with(mContext).load(message.getMessage()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    viewHolder.tvMessage.setBackground(resource);
                }
            });
        } else {
            viewHolder.tvMessage.setText(message.getMessage());
        }
        viewHolder.tvTimeSend.setText(message.getTime());
        if(message.getIsSeen().equals("true")){
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
