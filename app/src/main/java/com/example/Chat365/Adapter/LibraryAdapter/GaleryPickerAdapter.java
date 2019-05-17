package com.example.Chat365.Adapter.LibraryAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.Chat365.R;

import java.util.List;

public class GaleryPickerAdapter extends RecyclerView.Adapter<GaleryPickerAdapter.ViewHolder> {
    List<String> listHinh;
    Context mContext;
    OnCallBack mListener;
    public GaleryPickerAdapter(List<String> listHinh,OnCallBack mListener) {
        this.listHinh = listHinh;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext= viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.itemgalery,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(mContext)
                .load(listHinh.get(i))
                .into(viewHolder.imGalery);
    }

    @Override
    public int getItemCount() {
        return listHinh.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imGalery;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imGalery = itemView.findViewById(R.id.imGalery);
            imGalery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnItemSelect(getAdapterPosition());
                }
            });
        }
    }
    public interface OnCallBack{
         void OnItemSelect(int position);
    }
}
