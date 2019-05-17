package com.example.Chat365.Adapter.UserAdapter.FriendAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Fragment.FragmentGroup;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.RequestType;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendRequestAdapter extends BaseAdapter {
    private List<RequestType> listRequest;
    private Context mContext;
    private DatabaseReference mData;
    public FriendRequestAdapter(Context context, List<RequestType> listRequest) {
        mData = FirebaseDatabase.getInstance().getReference();
        this.mContext = context;
        mData.keepSynced(true);
        this.listRequest = listRequest;
    }

    @Override
    public int getCount() {
        return listRequest.size();
    }

    @Override
    public Object getItem(int position) {
        return listRequest.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final FriendRequestAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new FriendRequestAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.requestfriends,parent,false);
            viewHolder.tvNickRequest = convertView.findViewById(R.id.tvnickrequest);
            viewHolder.imAvatarRequest = convertView.findViewById(R.id.imAvatarequest);
            viewHolder.btnAccept= convertView.findViewById(R.id.btnAccept);
            viewHolder.btnClose= convertView.findViewById(R.id.btnRemove);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (FriendRequestAdapter.ViewHolder)convertView.getTag();
        }
        // Set data
        final RequestType requestType = listRequest.get(position);
        mData.child("Users").child(requestType.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                // set avatar
                if(!user.getLinkAvatar().isEmpty()) {
                    Picasso.get().load(user.getLinkAvatar()).into(viewHolder.imAvatarRequest);
                }
                // set name
                viewHolder.tvNickRequest.setText(user.getName());
                // Event button accept friend , close friends
                final FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Friends friendSent = new Friends(user.getId(),"Friends",false);
                        Friends friendsReceived = new Friends(currentUser.getUid(),"Friends",false);
                        mData.child("Friends").child(currentUser.getUid()).child(user.getId()).setValue(friendSent);
                        mData.child("Friends").child(user.getId()).child(currentUser.getUid()).setValue(friendsReceived);
                        mData.child("Friends_Req").child(currentUser.getUid()).child(user.getId());
                        mData.child("Friends_Req").removeValue();
                        mData.child("Friends_Req").child(user.getId()).child(currentUser.getUid());
                        mData.child("Friends_Req").removeValue();
                        listRequest.remove(requestType);
                        notifyDataSetChanged();
                    }
                });
                viewHolder.btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mData.child("Friends_Req").child(user.getId()).child(currentUser.getUid());
                        mData.child("Friends_Req").removeValue();
                        listRequest.remove(requestType);
                        mData.child("Friends_Req").child(user.getId()).child(currentUser.getUid()).child("request_type").setValue("Follow");
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return convertView;
    }
    private class ViewHolder {
        TextView tvNickRequest;
        ImageView imAvatarRequest;
        Button btnAccept,btnClose;
    }
}
