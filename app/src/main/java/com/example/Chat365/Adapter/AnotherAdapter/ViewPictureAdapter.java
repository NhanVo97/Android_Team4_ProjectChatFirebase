package com.example.Chat365.Adapter.AnotherAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.Chat365.Fragment.FragmentViewPicture;
import com.example.Chat365.R;

import java.util.List;

public class ViewPictureAdapter extends RecyclerView.Adapter<ViewPictureAdapter.ViewHolder> {
    private List<String> listhinh;
    private Oncallback mListener;
    private Context mContext;
    FragmentManager fragmentManager;
    public ViewPictureAdapter(Oncallback mListener, List<String> listhinh, FragmentManager fragmentManager) {
        this.mListener = mListener;
        this.listhinh = listhinh;
        this.fragmentManager=fragmentManager;
    }

    @NonNull
    @Override
    public ViewPictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemposthinh,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewPictureAdapter.ViewHolder holder, final int position) {
        if(listhinh!=null)
        {
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            Glide.with(mContext).load(listhinh.get(position)).apply(options).into(holder.imPosthinh);
            holder.imPosthinh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    FragmentViewPicture fragmentViewPicture = new FragmentViewPicture();
                    Bundle bundle = new Bundle();
                    bundle.putString("LinkAnh",listhinh.get(position));
                    fragmentViewPicture.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null).replace(R.id.layoutpost2,fragmentViewPicture);
                    fragmentTransaction.commit();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return listhinh.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imPosthinh;
        TextView btnSend;

        public ViewHolder(View itemView) {
            super(itemView);
            imPosthinh = itemView.findViewById(R.id.hinhpost);
            btnSend = itemView.findViewById(R.id.btnSendPicture);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
    public interface Oncallback{
        void onItemClick(int position);
    }
}