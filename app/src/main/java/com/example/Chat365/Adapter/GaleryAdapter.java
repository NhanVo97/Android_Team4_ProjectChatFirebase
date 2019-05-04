package com.example.Chat365.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Model.Galery;
import com.example.Chat365.R;

import java.util.List;

public class GaleryAdapter extends BaseAdapter {
    List<Galery> galeryList;
    Context context;
    int Layout;
    LayoutInflater layoutInflater;
    public GaleryAdapter(Context context,int Layout, List<Galery> list) {
        this.galeryList = list;
        this.context=context;
        this.Layout=Layout;
    }
    @Override
    public int getCount() {
        return galeryList.size();
    }

    @Override
    public Object getItem(int position) {
        return galeryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null) {
            viewHolder = new ViewHolder();
            layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(Layout,null);
            viewHolder.imGalery = convertView.findViewById(R.id.imGalery);
            viewHolder.textNum = convertView.findViewById(R.id.numberstt);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if(galeryList.get(position).isCheck()) {
            viewHolder.textNum.setVisibility(View.VISIBLE);
            viewHolder.textNum.setText(galeryList.get(position).getNum());
        }
        else {
            viewHolder.textNum.setText("");
            viewHolder.textNum.setVisibility(View.GONE);
        }
        if(galeryList.get(position).getLink()!=null) {
            Glide.with(convertView.getContext())
                    .load(galeryList.get(position).getLink())
                    .into(viewHolder.imGalery);
        }
        else
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(galeryList.get(position).getByteArray(), 0, galeryList.get(position).getByteArray().length);
            viewHolder.imGalery.setImageBitmap(bmp);
        }

        return convertView;
    }
    private class ViewHolder
    {
        ImageView imGalery;
        TextView textNum;
    }
}
