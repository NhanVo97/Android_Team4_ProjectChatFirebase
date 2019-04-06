package com.example.Chat365.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.Chat365.Model.Quyen;
import com.example.Chat365.R;

import java.util.List;

public class QuyenAdapter extends BaseAdapter {
    List<Quyen> listQuyen;
    LayoutInflater layoutInflater;
    private RadioButton selected = null;
    private String currentQuyen;
    int vitri;

    public QuyenAdapter(List<Quyen> listQuyen, Context mContext, String quyen) {
        layoutInflater = LayoutInflater.from(mContext);
        this.listQuyen=listQuyen;
        this.currentQuyen=quyen;
    }

    public int getVitri() {
        return vitri;
    }

    public void setVitri(int vitri) {
        this.vitri = vitri;
    }

    @Override
    public int getCount() {
        return listQuyen.size();
    }

    @Override
    public Object getItem(int position) {
        return listQuyen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final viewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder = new viewHolder();
            convertView = layoutInflater.inflate(R.layout.quyenitems,parent,false);
            viewHolder.tvCap = convertView.findViewById(R.id.tvThongtinquyen);
            viewHolder.rdQuyen =convertView.findViewById(R.id.rdQuyen);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (QuyenAdapter.viewHolder)convertView.getTag();
        }
        final Quyen q = listQuyen.get(position);
        viewHolder.tvCap.setText(q.getCap());
        viewHolder.rdQuyen.setText(q.getName());
        if (position==Integer.parseInt(currentQuyen)) {
            if (selected == null) {
                viewHolder.rdQuyen.setChecked(true);
                selected = viewHolder.rdQuyen;
            }
        }
        viewHolder.rdQuyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != null) {
                    selected.setChecked(false);
                    setVitri(position);
                }
                viewHolder.rdQuyen.setChecked(true);
                selected = viewHolder.rdQuyen;

            }
        });
        if(q.getName().equals("Công Khai"))
        {
            viewHolder.rdQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.congkhai,0,0,0);
        }
        else if(q.getName().equals("Bạn Bè"))
        {
            viewHolder.rdQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.withmyfriends,0,0,0);
        }
        else if(q.getName().equals("Chỉ Mình Tôi"))
        {
            viewHolder.rdQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.riengtu,0,0,0);
        }
        return convertView;
    }
    public class viewHolder
    {
        TextView tvCap;
        RadioButton rdQuyen;
    }
}
