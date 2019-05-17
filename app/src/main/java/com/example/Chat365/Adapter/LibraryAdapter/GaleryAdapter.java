package com.example.Chat365.Adapter.LibraryAdapter;

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
import com.example.Chat365.Model.Gallery;
import com.example.Chat365.R;

import java.util.List;

public class GaleryAdapter extends BaseAdapter {
    List<Gallery> galleryList;
    Context context;
    int Layout;
    LayoutInflater layoutInflater;
    boolean isSelectGaleryPage;
    public GaleryAdapter(Context context, int Layout, List<Gallery> list,boolean isSelectGaleryPage) {
        this.galleryList = list;
        this.context = context;
        this.Layout = Layout;
        this.isSelectGaleryPage = isSelectGaleryPage;
    }
    @Override
    public int getCount() {
        return galleryList.size();
    }

    @Override
    public Object getItem(int position) {
        return galleryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(Layout, null);
            viewHolder.imGalery = convertView.findViewById(R.id.imGalery);
            viewHolder.textNum = convertView.findViewById(R.id.numberstt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(isSelectGaleryPage){
            if (galleryList.get(position).isCheck()) {
                viewHolder.textNum.setVisibility(View.VISIBLE);
                viewHolder.textNum.setText(String.valueOf(galleryList.get(position).getNumber()));
            } else {
                viewHolder.textNum.setText("");
                viewHolder.textNum.setVisibility(View.GONE);
            }
        }
        if (galleryList.get(position).getLink() != null) {
            Glide.with(convertView.getContext())
                    .load(galleryList.get(position).getLink())
                    .into(viewHolder.imGalery);
        } else {
            Bitmap bmp = BitmapFactory.decodeByteArray(galleryList.get(position).getByteArray(), 0, galleryList.get(position).getByteArray().length);
            viewHolder.imGalery.setImageBitmap(bmp);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView imGalery;
        TextView textNum;
    }
}
