package com.example.Chat365.Adapter.UserAdapter.GroupChatAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendSelectAdapter extends RecyclerView.Adapter<FriendSelectAdapter.ViewHolder> {
    List<Friends> listFriends;
    OnCallBack mListener;
    DatabaseReference mData;
    Context mContext;
    boolean isPanelRight;
    String searchKey;
    public FriendSelectAdapter(List<Friends> listFriends, OnCallBack mListener,
                               boolean isPanelRight, String searchKey) {
        this.listFriends = listFriends;
        this.mListener = mListener;
        this.isPanelRight = isPanelRight;
        this.searchKey = searchKey;
        mData= FirebaseDatabase.getInstance().getReference("Users");
    }

    @NonNull
    @Override
    public FriendSelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemlistfriend,viewGroup,false);
        mContext = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendSelectAdapter.ViewHolder viewHolder, int i) {
            // Handle both Panel binding
            Friends friends = listFriends.get(i);
            Query query =  searchKey.isEmpty() ? mData.child(friends.getId()).orderByChild("name")
                    : mData.child(friends.getId()).startAt(searchKey);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user!=null){
                        String name = user.getName().length()>9 ? user.getName().substring(0,9) :  user.getName();
                        viewHolder.tvName.setText(name);
                        if(!user.getLinkAvatar().isEmpty()){
                            Glide.with(mContext)
                                    .load(user.getLinkAvatar())
                                    .centerCrop()
                                    .placeholder(R.drawable.spinne_loading)
                                    .into(viewHolder.cAvt);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            // Handle Panel Left
                if(!isPanelRight){
                    if(friends.isCheck()){
                        viewHolder.tvCheck.setBackgroundResource(R.drawable.success);
                    } else {
                        viewHolder.tvCheck.setBackgroundResource(R.drawable.verified);
                    }
                } else {
                    // Handle Panel Right
                    viewHolder.tvCheck.setBackgroundResource(R.drawable.delete);
                }

    }

    @Override
    public int getItemCount() {
        return listFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvCheck;
        CircleImageView cAvt;
        RelativeLayout layoutSelect;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCheck = itemView.findViewById(R.id.tvCheck);
            cAvt = itemView.findViewById(R.id.imAvt);
            layoutSelect = itemView.findViewById(R.id.layoutSelect);
            if(!isPanelRight){
                layoutSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemSelect(getAdapterPosition(),isPanelRight);
                    }
                });
            } else {
                tvCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemSelect(getAdapterPosition(),isPanelRight);
                    }
                });
            }

        }
    }
    public interface OnCallBack{
        void onItemSelect(int postion,boolean isPanelRight);
    }
}
