package com.example.Chat365.Adapter.RoomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.Chat365.Model.Room;
import com.example.Chat365.R;

import java.util.List;

public class RoomPublicAdapter extends BaseAdapter {
    List<Room> listRoom;
    LayoutInflater layoutInflater;

    public RoomPublicAdapter(Context context, List<Room> list)
    {
        layoutInflater = LayoutInflater.from(context);
        this.listRoom = list;
    }
    @Override
    public int getCount() {
        return listRoom.size();
    }

    @Override
    public Room getItem(int position) {
        return listRoom.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null)
        {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.itemlistroom,parent,false);
                viewHolder.textNickName = convertView.findViewById(R.id.textNickName);
                viewHolder.ImageAvt = convertView.findViewById(R.id.ImageAvt);
              convertView.setTag(viewHolder);

        }
        else
        {
          viewHolder = (ViewHolder) convertView.getTag();
        }
        Room o = listRoom.get(position);
        viewHolder.ImageAvt.setImageResource(o.getIcon());
        viewHolder.textNickName.setText(o.getStr());
        return convertView;
    }
    private class ViewHolder
    {
        TextView textNickName;
        ImageView ImageAvt;
    }
}
