package com.example.Chat365.Adapter.RoomAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ListUserOnlineAdapter extends BaseAdapter {
    List<User> listUser;
    LayoutInflater layoutInflater;
    DatabaseReference mFriends;
    public ListUserOnlineAdapter(Context context, List<User> list)
    {
        layoutInflater = LayoutInflater.from(context);
        this.listUser = list;
    }
    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int position) {
        return listUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ListUserOnlineAdapter.ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder = new ListUserOnlineAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.itemlistonline,parent,false);
            viewHolder.tvNickName = convertView.findViewById(R.id.tvnicknameuser);
            viewHolder.ivAvatar =convertView.findViewById(R.id.imavataruser);
            viewHolder.tt = convertView.findViewById(R.id.tvChucvu);
            viewHolder.tvAddress = convertView.findViewById(R.id.tvAddress);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ListUserOnlineAdapter.ViewHolder)convertView.getTag();
        }
        User u = listUser.get(position);
       FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriends=FirebaseDatabase.getInstance().getReference("Friends");
        mFriends.child(currentUser.getUid()).child(u.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    viewHolder.tt.setText("Bạn bè");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(currentUser.getEmail().equals(u.getEmail()))
        {
           viewHolder.tt.setText("Bạn");
        }
        else
        {
            viewHolder.tt.setText("Người lạ");
        }
        if(!u.getLinkAvatar().equals(""))
        {
            Picasso.get().load(u.getLinkAvatar()).into(viewHolder.ivAvatar);
        }

        viewHolder.tvNickName.setText(u.getName());
        viewHolder.tvAddress.setText(u.getLocationUser().getAddress()!=null ?u.getLocationUser().getAddress() : "" );
        return convertView;
    }
    private class ViewHolder
    {
        TextView tvNickName,tt,tvAddress;
        ImageView ivAvatar;
    }
}
