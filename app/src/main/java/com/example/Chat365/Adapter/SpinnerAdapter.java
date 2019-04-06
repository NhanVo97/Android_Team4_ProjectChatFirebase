package com.example.Chat365.Adapter;

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

public class SpinnerAdapter extends BaseAdapter {
    List<Other> listspinner;
    Context context;
    int myLayout;
    public SpinnerAdapter(Context context,int MyLayout,List<Other> listspinner)
    {
        this.listspinner=listspinner;
        this.context=context;
        this.myLayout=MyLayout;
    }
    @Override
    public int getCount() {
        return listspinner.size();
    }

    @Override
    public Object getItem(int position) {
        return listspinner.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(myLayout,null);
        TextView tvSpinner = convertView.findViewById(R.id.tvSpinner);
        ImageView imSpinner = convertView.findViewById(R.id.imSpinner);
        tvSpinner.setText(listspinner.get(position).getStr());
        imSpinner.setImageResource(listspinner.get(position).getIcon());
        return convertView;
    }
}
