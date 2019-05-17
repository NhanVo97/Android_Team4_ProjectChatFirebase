package com.example.Chat365.Adapter.RoomAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>{
    private List<Message> listMessage;

    public ChatRoomAdapter(List<Message> listMessage) {
        this.listMessage = listMessage;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemchatlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.ViewHolder holder, int position) {
        Message message = listMessage.get(position);
        User user = message.getUser();
        if(!user.getLinkAvatar().equals("")) {
            Picasso.get().load(message.getUser().getLinkAvatar()).into(holder.imHinh);
        }
        if(message.getMessage().startsWith("http://")){

        }
        holder.tvName.setText(message.getUser().getName());
        holder.tvMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvMessage;
        ImageView imHinh;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvnicknameuser);
            imHinh = itemView.findViewById(R.id.imavataruser);
            tvMessage=itemView.findViewById(R.id.tvNoidungchat);
        }
    }
}
