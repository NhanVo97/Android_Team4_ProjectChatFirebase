package com.example.Chat365.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class FriendsAdapter extends BaseAdapter {
    List<User> listUser;
    LayoutInflater layoutInflater;
    DatabaseReference mData;
    FirebaseAuth current;
    public FriendsAdapter(Context context, List<User> list)
    {
        layoutInflater = LayoutInflater.from(context);
        mData=FirebaseDatabase.getInstance().getReference("Friends");
        mData.keepSynced(true);
        this.listUser = list;
        current = FirebaseAuth.getInstance();
    }
    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int position) {
            notifyDataSetChanged();
            return listUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FriendsAdapter.ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder = new FriendsAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.itemlistfriends,parent,false);
            viewHolder.tvnamefr = convertView.findViewById(R.id.tvnicknameuser);
            viewHolder.imlistfr =convertView.findViewById(R.id.imavataruser);
            viewHolder.tviewFriend=convertView.findViewById(R.id.tviewFriend);
            viewHolder.sttOnline = convertView.findViewById(R.id.sttOnline);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (FriendsAdapter.ViewHolder)convertView.getTag();
        }
        User u = listUser.get(position);
        if(current.getCurrentUser()!=null)
        {
            mData.child(current.getCurrentUser().getUid()).child(u.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        Friends friends = dataSnapshot.getValue(Friends.class);
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(!u.getAvatar().equals(""))
        {
            Picasso.get().load(u.getAvatar()).into(viewHolder.imlistfr);
        }
        if(!u.getStatus().equals(""))
        {
            viewHolder.tviewFriend.setText(u.getStatus());
        }
        viewHolder.sttOnline.setBackgroundResource(R.drawable.online);

        viewHolder.tvnamefr.setText(u.getName());
        return convertView;
    }
    private class ViewHolder
    {
        TextView tvnamefr,tviewFriend,sttOnline;
        ImageView imlistfr;
    }
}
