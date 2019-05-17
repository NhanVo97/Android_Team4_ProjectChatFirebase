package com.example.Chat365.Adapter.UserAdapter.SearchBoxAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistorySearchAdapter extends BaseAdapter {
    List<User> listUser;
    LayoutInflater layoutInflater;
    public HistorySearchAdapter(Context context, List<User> listUser)
    {
        layoutInflater = LayoutInflater.from(context);
        this.listUser = listUser;
    }
    @Override
    public int getCount() {
        return listUser.size();
    }

    @Override
    public Object getItem(int i) {
        return listUser.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view==null)
        {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.otheritems,viewGroup,false);
            viewHolder.tvName = view.findViewById(R.id.tvName);
            viewHolder.imIcon =view.findViewById(R.id.imIcon);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)view.getTag();
        }
        User o = listUser.get(i);
        if(!o.getLinkAvatar().equals(""))
        {
            Picasso.get().load(o.getLinkAvatar()).into(viewHolder.imIcon);
        }
        else
        {
            viewHolder.imIcon.setImageResource(R.drawable.noavt);
        }
        viewHolder.tvName.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
        viewHolder.tvName.setText(o.getName());
        return view;
    }
    private class ViewHolder
    {
        TextView tvName;
        ImageView imIcon;
    }
}
