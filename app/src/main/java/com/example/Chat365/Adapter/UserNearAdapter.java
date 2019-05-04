package com.example.Chat365.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.MapUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserNearAdapter extends RecyclerView.Adapter<UserNearAdapter.ViewHolder> {
    private List<User> listUser;
    private OnCallBack mListener;
    private User mCurrent;
    private Context mContext;

    public UserNearAdapter(List<User> listUser, OnCallBack mListener,User mCurrent) {
        this.listUser = listUser;
        this.mListener = mListener;
        this.mCurrent = mCurrent;
    }

    @NonNull
    @Override
    public UserNearAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemusernearlocation, viewGroup, false);
        mContext = viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserNearAdapter.ViewHolder viewHolder, int i) {
        User user = listUser.get(i);
        if(!user.getLinkAvatar().isEmpty()){
            Glide.with(mContext)
                    .load(user.getLinkAvatar())
                    .centerCrop()
                    .placeholder(R.drawable.spinne_loading)
                    .into(viewHolder.ciAvt);
        }
        viewHolder.tvName.setText(user.getName());
        viewHolder.tvStatus.setText(user.getStatus());
        LatLng locationUser = new LatLng(mCurrent.getLocationUser().getLatitude(),
                                        mCurrent.getLocationUser().getLongitude());
        LatLng locationOther = new LatLng(user.getLocationUser().getLatitude(),
                user.getLocationUser().getLongitude());
        viewHolder.tvLocation.setText("Cách " + MapUtils.CalculationByDistance(locationUser,locationOther) + " Km");
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvLocation,tvStatus;
        CircleImageView ciAvt;
        RelativeLayout line;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.location);
            tvStatus = itemView.findViewById(R.id.status);
            ciAvt = itemView.findViewById(R.id.imAvt);
            line = itemView.findViewById(R.id.line);
            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnCallBack{
        void onItemClick(int position);
    }
}
