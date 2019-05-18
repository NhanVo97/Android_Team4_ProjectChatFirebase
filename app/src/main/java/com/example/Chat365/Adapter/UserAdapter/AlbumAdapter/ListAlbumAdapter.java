package com.example.Chat365.Adapter.UserAdapter.AlbumAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Model.Album;
import com.example.Chat365.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListAlbumAdapter extends BaseAdapter {
    List<Album> listCustoms;
    LayoutInflater layoutInflater;
    DatabaseReference mData;

    public ListAlbumAdapter(List<Album> listCustoms, Context mContext) {
        this.listCustoms = listCustoms;
        this.layoutInflater = LayoutInflater.from(mContext);
        mData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return listCustoms.size();
    }

    @Override
    public Object getItem(int position) {
        return listCustoms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.itemalbums, parent, false);
            viewHolder.imHinh = convertView.findViewById(R.id.imhinhnencuoi);
            viewHolder.tvName = convertView.findViewById(R.id.tenalbums);
            viewHolder.tvSohinh = convertView.findViewById(R.id.soluonganh);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ListAlbumAdapter.ViewHolder) convertView.getTag();
        }
        Album listCustom = listCustoms.get(position);
        if (listCustom.getListHinh() == null) {
            viewHolder.tvSohinh.setText("Trống");
        } else {
            List<String> list = new ArrayList<>();
            list.addAll(listCustom.getListHinh());
            Glide.with(convertView.getContext()).load((list.get(0))).into(viewHolder.imHinh);
            viewHolder.tvSohinh.setText(listCustom.getListHinh().size() + " Hình");
        }
        viewHolder.tvName.setText(listCustom.getName());

        return convertView;

    }

    private class ViewHolder {
        ImageView imHinh;
        TextView tvName, tvSohinh;
    }

}
