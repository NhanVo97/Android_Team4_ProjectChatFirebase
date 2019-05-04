package com.example.Chat365.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListGroupAdapter extends RecyclerView.Adapter<ListGroupAdapter.ViewHolder> {
    List<GroupFriends> listGroup;
    Context mContext;
    OnCallBack onCallBack;

    public ListGroupAdapter(List<GroupFriends> listGroup,OnCallBack onCallBack) {
        this.listGroup = listGroup;
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public ListGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.itemgroup,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListGroupAdapter.ViewHolder viewHolder, int i) {
        GroupFriends groupFriends = listGroup.get(i);
        if(groupFriends.getLinkAvatar().isEmpty() && groupFriends.getLinkAvatar()!=null){
            Glide.with(mContext)
                    .asGif()
                    .load(groupFriends.getLinkAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.spinne_loading)
                    .into(viewHolder.cvAvt);
        }
    }

    @Override
    public int getItemCount() {
        return listGroup.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cvAvt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvAvt = itemView.findViewById(R.id.crAvt);
            cvAvt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCallBack.onItemClick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnCallBack{
        void onItemClick(int position);
    }
}
