package com.example.Chat365.Adapter.UserAdapter.GroupChatAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.TimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserStatusAdapter extends RecyclerView.Adapter<UserStatusAdapter.ViewHolder> {
    private List<String> listUser;
    private Context mContext;
    private DatabaseReference mData;
    private onCallBack onCallBack;

    public UserStatusAdapter(List<String> listUser, DatabaseReference mData,onCallBack onCallBack) {
        this.listUser = listUser;
        this.mData = mData;
        this.onCallBack = onCallBack;
    }

    @NonNull
    @Override
    public UserStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemuser, viewGroup, false);
        mContext = viewGroup.getContext();
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final UserStatusAdapter.ViewHolder viewHolder, int i) {
        mData.child("Users").child(listUser.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.getLinkAvatar().isEmpty()){
                    Glide.with(mContext)
                            .load(user.getLinkAvatar())
                            .centerCrop()
                            .placeholder(R.drawable.spinne_loading)
                            .into(viewHolder.cvAvt);
                }
                long lastTime = user.getTimestamp();
                String lastSeenTime = TimeUtils.getTimeAgo(lastTime, mContext);
                viewHolder.tvLastActivity.setText(lastSeenTime);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView cvAvt;
        TextView tvLastActivity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvAvt = itemView.findViewById(R.id.cvAvt);
            tvLastActivity = itemView.findViewById(R.id.tvTimeOnline);
            cvAvt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCallBack.onItemClick(getAdapterPosition());
                }
            });
        }
    }
    public interface onCallBack{
        void onItemClick(int position);
    }
}
