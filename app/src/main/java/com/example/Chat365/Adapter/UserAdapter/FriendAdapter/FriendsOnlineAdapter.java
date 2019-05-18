package com.example.Chat365.Adapter.UserAdapter.FriendAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsOnlineAdapter extends BaseAdapter {
    List<Friends> listFriend;
    LayoutInflater layoutInflater;
    DatabaseReference mData;

    public FriendsOnlineAdapter(Context context, List<Friends> listFriend) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = FirebaseDatabase.getInstance().getReference();
        this.listFriend = listFriend;
    }

    @Override
    public int getCount() {
        return listFriend.size();
    }

    @Override
    public Object getItem(int position) {
        return listFriend.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FriendsOnlineAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new FriendsOnlineAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.itemlistfriends, parent, false);
            viewHolder.tvnamefr = convertView.findViewById(R.id.tvnicknameuser);
            viewHolder.imlistfr = convertView.findViewById(R.id.imavataruser);
            viewHolder.tviewFriend = convertView.findViewById(R.id.tviewFriend);
            viewHolder.sttOnline = convertView.findViewById(R.id.sttOnline);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FriendsOnlineAdapter.ViewHolder) convertView.getTag();
        }
        Friends friends = listFriend.get(position);
        mData.child("Users").child(friends.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user!=null){
                    // set name
                    viewHolder.tvnamefr.setText(user.getName());
                    // set avatar
                    if(!user.getLinkAvatar().isEmpty()){
                        Picasso.get().load(user.getLinkAvatar()).into(viewHolder.imlistfr);
                    }
                    // set status
                    if (!user.getStatus().isEmpty()) {
                        viewHolder.tviewFriend.setText(user.getStatus());
                    }
                    // set online active status
                    if(user.getIsOnline().equals("true")){
                        viewHolder.sttOnline.setBackgroundResource(R.drawable.online);
                    } else {
                        viewHolder.sttOnline.setBackgroundResource(R.drawable.offline);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView tvnamefr, tviewFriend, sttOnline;
        ImageView imlistfr;
    }
}
