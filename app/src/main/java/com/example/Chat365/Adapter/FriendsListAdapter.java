package com.example.Chat365.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsListAdapter extends BaseAdapter {
    List<User> listUser;
    LayoutInflater layoutInflater;
    DatabaseReference mData;
    FirebaseAuth current;
    public FriendsListAdapter(Context context, List<User> list)
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
        final ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.itemslistfriends,parent,false);
            viewHolder.tvnamefr = convertView.findViewById(R.id.tvNamePrv);
            viewHolder.imlistfr =convertView.findViewById(R.id.imAvtPrv);
            viewHolder.tviewFriend=convertView.findViewById(R.id.tvTrangthaiPrv);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        User u = listUser.get(position);
        if(!u.getLinkAvatar().equals(""))
        {
            Picasso.get().load(u.getLinkAvatar()).into(viewHolder.imlistfr);
        }
        if(!u.getStatus().equals(""))
        {
            viewHolder.tviewFriend.setText(u.getStatus());
        }

        viewHolder.tvnamefr.setText(u.getName());
        return convertView;
    }
    private class ViewHolder
    {
        TextView tvnamefr,tviewFriend;
        ImageView imlistfr;
    }
}
