package com.example.Chat365.Adapter.UserAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.Chat365.Fragment.FragmentViewPicture;
import com.example.Chat365.Model.Message;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagePrivateAdapter extends RecyclerView.Adapter<MessagePrivateAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;
    private List<Message> mChat;
    private Context mContext;
    private FragmentManager fragmentManager;
    FirebaseUser fuser;

    public MessagePrivateAdapter(List<Message> mChat, FragmentManager fragmentManager){
        this.mChat = mChat;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MessagePrivateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentmessage, parent, false);
            return new MessagePrivateAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receivemessage, parent, false);
            return new MessagePrivateAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagePrivateAdapter.ViewHolder holder, int position) {

        final Message chat = mChat.get(position);
        if(chat.getMessage().startsWith("https://"))
        {
            RequestOptions options = new RequestOptions();
            options.override(150,150);
            holder.show_message.setText("");
            Glide.with(mContext).load(chat.getMessage()).apply(options).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    holder.show_message.setBackground(resource);
                    holder.show_message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            FragmentViewPicture fragmentViewPicture = new FragmentViewPicture();
                            Bundle bundle = new Bundle();
                            bundle.putString("LinkAnh",chat.getMessage());
                            fragmentViewPicture.setArguments(bundle);
                            fragmentTransaction.addToBackStack(null).replace(R.id.layoutmessager,fragmentViewPicture);
                            fragmentTransaction.commit();
                        }
                    });
                }
            });

        }
        else
        {
            holder.show_message.setText(chat.getMessage());
        }
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (!chat.getUser().getLinkAvatar().equals("") && !chat.getUser().getId().equals(fuser.getUid()) && chat.getTime().equals("Đã Nhận")){
            Picasso.get().load(chat.getUser().getLinkAvatar()).into(holder.profile_image);
        }
        else if(mChat.get(position).getIsSeen().equals("true"))
        {
            if(!chat.getUser().getLinkAvatar().equals(""))
            {
                Picasso.get().load(chat.getUser().getLinkAvatar()).into(holder.profile_image);
            }

        }
        if (position == mChat.size()-1){
            if (chat.getIsSeen().equals("true")){
                holder.txt_seen.setText("Đã xem lúc "+chat.getTime());
            } else {
                holder.txt_seen.setText("Đã gửi");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.text_message_body);
            profile_image = itemView.findViewById(R.id.image_message_profile);
            txt_seen = itemView.findViewById(R.id.text_message_time);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (!mChat.get(position).getUser().getId().equals(fuser.getUid()) && mChat.get(position).getTime().equals("Đã Nhận")){
            return MSG_TYPE_LEFT;

        }
        else if(mChat.get(position).getIsSeen().equals("true"))
        {
            return MSG_TYPE_LEFT;
        }
        else {
            return MSG_TYPE_RIGHT;

        }
    }
}