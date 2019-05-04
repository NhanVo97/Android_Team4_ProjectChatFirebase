package com.example.Chat365.Adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.Message;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListMessagerAdapter extends RecyclerView.Adapter<ListMessagerAdapter.ViewHolder> {
    private List<Message> listMessager;
    private Oncallback mListener;
    DatabaseReference mData;
    FirebaseAuth mAuth;
    public ListMessagerAdapter(Oncallback mListener,List<Message> listMessager) {
        this.mListener = mListener;
        this.listMessager = listMessager;
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }
    @NonNull
    @Override
    public ListMessagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlistfriends,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListMessagerAdapter.ViewHolder holder, int position) {
        Message m = listMessager.get(position);
        if(!m.getUser().getLinkAvatar().equals(""))
        {
            Picasso.get().load(m.getUser().getLinkAvatar()).into(holder.imlistfr);
        }
        if(m.getUser().getIsOnline().equals("true"))
        {
            holder.sttOnline.setBackgroundResource(R.drawable.online);
        }
        holder.tvnamefr.setText(m.getUser().getName());
        if(m.getMessage().startsWith("https://"))
        {
            holder.tviewFriend.setText("Đã gửi 1 ảnh");

        }
        else
        {
            holder.tviewFriend.setText(m.getMessage());

        }

    }

    @Override
    public int getItemCount() {
        return listMessager.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvnamefr,tviewFriend,sttOnline;
        ImageView imlistfr;
        ConstraintLayout constraintLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            tvnamefr = itemView.findViewById(R.id.tvnicknameuser);
            imlistfr =itemView.findViewById(R.id.imavataruser);
            tviewFriend=itemView.findViewById(R.id.tviewFriend);
            sttOnline =itemView.findViewById(R.id.sttOnline);
            constraintLayout=itemView.findViewById(R.id.tvTTFR);
//            constraintLayout.setBackgroundColor(itemView.getResources().getColor(R.color.browser_actions_bg_grey));
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
