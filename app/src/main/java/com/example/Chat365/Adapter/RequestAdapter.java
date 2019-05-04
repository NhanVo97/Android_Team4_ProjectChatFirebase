package com.example.Chat365.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Fragment.FragmentGroup;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RequestAdapter extends BaseAdapter {
    List<User> listUser;
    LayoutInflater layoutInflater;
    private DatabaseReference mData;
    public RequestAdapter(Context context, List<User> list)
    {
        layoutInflater = LayoutInflater.from(context);
        mData= FirebaseDatabase.getInstance().getReference();
        mData.keepSynced(true);
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
        RequestAdapter.ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder = new RequestAdapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.requestfriends,parent,false);
            viewHolder.tvnickrequest = convertView.findViewById(R.id.tvnickrequest);
            viewHolder.imAvatarequest =convertView.findViewById(R.id.imAvatarequest);
            viewHolder.btnAccept=convertView.findViewById(R.id.btnAccept);
            viewHolder.btnClose=convertView.findViewById(R.id.btnRemove);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (RequestAdapter.ViewHolder)convertView.getTag();
        }
        final User u = listUser.get(position);
        if(!u.getLinkAvatar().equals(""))
        {
            Picasso.get().load(u.getLinkAvatar()).into(viewHolder.imAvatarequest);
        }
        viewHolder.tvnickrequest.setText(u.getName());
        final FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.child("Friends").child(currentUser.getUid()).child(u.getId()).child("Status").setValue("Friends");
                mData.child("Friends").child(currentUser.getUid()).child(u.getId()).child("id").setValue(u.getId());
                mData.child("Friends").child(u.getId()).child(currentUser.getUid()).child("Status").setValue("Friends");
                mData.child("Friends").child(u.getId()).child(currentUser.getUid()).child("id").setValue(currentUser.getUid());
                mData.child("Friends_Req").child(currentUser.getUid()).child(u.getId());
                mData.child("Friends_Req").removeValue();
                mData.child("Friends_Req").child(u.getId()).child(currentUser.getUid());
                mData.child("Friends_Req").removeValue();
                listUser.remove(u);
                FragmentGroup.listFriends.add(u);
                notifyDataSetChanged();
            }
        });
        viewHolder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mData.child("Friends_Req").child(u.getId()).child(currentUser.getUid());
                mData.child("Friends_Req").removeValue();
                listUser.remove(u);
                mData.child("Friends_Req").child(u.getId()).child(currentUser.getUid()).child("request_type").setValue("Follow");
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
    private class ViewHolder
    {
        TextView tvnickrequest;
        ImageView imAvatarequest;
        Button btnAccept,btnClose;
    }
}
