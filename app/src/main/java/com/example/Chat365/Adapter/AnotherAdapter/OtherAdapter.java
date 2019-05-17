package com.example.Chat365.Adapter.AnotherAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.Other;
import com.example.Chat365.R;

import java.util.List;

public class OtherAdapter extends BaseAdapter {
    List<Other> listMenuOther;
    LayoutInflater layoutInflater;

    public OtherAdapter(Context context,List<Other> list)
    {
        layoutInflater = LayoutInflater.from(context);
       this.listMenuOther =list;
    }
    @Override
    public int getCount() {
        return listMenuOther.size();
    }

    @Override
    public Other getItem(int position) {
        return listMenuOther.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.otheritems,parent,false);
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.imIcon =convertView.findViewById(R.id.imIcon);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }
            Other o = listMenuOther.get(position);
            viewHolder.imIcon.setImageResource(o.getIcon());
            viewHolder.tvName.setText(o.getStr());
        return convertView;
    }
    private class ViewHolder
    {
        TextView tvName;
        ImageView imIcon;
    }
}
