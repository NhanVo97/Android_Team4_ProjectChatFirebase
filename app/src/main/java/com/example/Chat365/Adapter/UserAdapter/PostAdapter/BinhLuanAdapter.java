package com.example.Chat365.Adapter.UserAdapter.PostAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Model.Comment;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.Chat365.Utils.TimeUtils.getTimeAgo;

public class BinhLuanAdapter extends RecyclerView.Adapter<BinhLuanAdapter.ViewHolder> {
    private List<Comment> listComment;
    private Oncallback mListener;
    DatabaseReference mData;
    Context mContext;
    FirebaseAuth mAuth;

    public BinhLuanAdapter(Oncallback mListener,List<Comment> listComment) {
        this.mListener = mListener;
        this.listComment = listComment;
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public BinhLuanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itembinhluan,parent,false);
        mContext=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BinhLuanAdapter.ViewHolder holder, int position) {
       final Comment comment = listComment.get(position);
           mData.child("Users").child(comment.getUser()).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(dataSnapshot.exists())
                   {
                       User user = dataSnapshot.getValue(User.class);
                       holder.tvName.setText(user.getName());
                       if(!user.getLinkAvatar().equals(""))
                       {
                           Picasso.get().load(user.getLinkAvatar()).into(holder.imHinh);
                       }
                       if(user.getIsOnline().equals("true"))
                       {
                           holder.sttol.setBackgroundResource(R.drawable.online);
                       }
                       else
                       {
                           holder.sttol.setBackgroundResource(R.drawable.offline);
                       }
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });

        holder.tvND.setText(comment.getND());
        String time = getTimeAgo(comment.getTime(),mContext);
        holder.tvTime.setText(time);


    }

    @Override
    public int getItemCount() {
        return listComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvND,tvTime,sttol;
        ImageView imHinh;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTenBinhLuan);
            imHinh = itemView.findViewById(R.id.cvavatar);
            tvND = itemView.findViewById(R.id.tvNDbinhluan);
            tvTime=itemView.findViewById(R.id.tvTimeBinhLuan);
            sttol = itemView.findViewById(R.id.sttol);
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
