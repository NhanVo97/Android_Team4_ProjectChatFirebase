package com.example.Chat365.Adapter.UserAdapter.PostAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.Chat365.Model.ThongBao;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.TimeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<ThongBao> listThongBao;
    private Oncallback mListener;
    DatabaseReference mData;
    Context mContext;
    public NotificationAdapter(Oncallback mListener, List<ThongBao> listThongBao) {
        this.mListener = mListener;
        this.listThongBao = listThongBao;
        mData = FirebaseDatabase.getInstance().getReference();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemnoti,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ThongBao thongBao = listThongBao.get(position);
        mData.child("Users").child(thongBao.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.tvName.setText(user.getName());
                if(!user.getLinkAvatar().equals("")) {
                    Picasso.get().load(user.getLinkAvatar()).into(holder.imHinh);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        long lastTime = thongBao.getTimestamp();
        String lastSeenTime = TimeUtils.getTimeAgo(lastTime,mContext);
        holder.tvTimestamp.setText(lastSeenTime);
        holder.tvNDTB.setText(thongBao.getMessage());

    }

    @Override
    public int getItemCount() {
        return listThongBao.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvNDTB,tvTimestamp;
        ImageView imHinh;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameTB);
            imHinh = itemView.findViewById(R.id.imAvtTb);
            tvNDTB = itemView.findViewById(R.id.tvNDTB);
            tvTimestamp = itemView.findViewById(R.id.tvTimeStamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(getPosition());
                }
            });
        }
    }
    public interface Oncallback{
        void onItemClick(int position);
    }
}
