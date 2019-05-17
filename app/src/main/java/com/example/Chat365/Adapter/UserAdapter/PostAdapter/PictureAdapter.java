package com.example.Chat365.Adapter.UserAdapter.PostAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.R;

import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>{
    private List<String> listHinh;
    private OnCallback mListener;
    private Context mContext;
    private int mselected = -1;

    public PictureAdapter(List<String> listHinh, OnCallback mListener) {
        this.listHinh = listHinh;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.itemposthinh, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        Glide.with(mContext)
                .load(listHinh.get(i))
                .into(viewHolder.imPosthinh);
        // Handle selected
        if (mselected == i) {
            viewHolder.imPosthinh.setAlpha(0.2f);
            viewHolder.btnSend.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imPosthinh.setAlpha(0.9f);
            viewHolder.btnSend.setVisibility(View.INVISIBLE);
        }
        viewHolder.imPosthinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mselected == i) {
                    mselected = -1;
                } else {
                    mselected = i;
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listHinh.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imPosthinh;
        TextView btnSend;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imPosthinh = itemView.findViewById(R.id.hinhpost);
            btnSend = itemView.findViewById(R.id.btnSendPicture);
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPictureClick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnCallback {
        void onPictureClick(int position);
    }
}
