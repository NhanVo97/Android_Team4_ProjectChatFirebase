package com.example.Chat365.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.Model.Comment;
import com.example.Chat365.Model.PostStatus;
import com.example.Chat365.Model.ThongBao;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.Chat365.Utils.TimeUtils.getTimeAgo;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> implements HinhAdapter.Oncallback{
    private List<PostStatus> listPost;
    private Oncallback mListener;
    private DatabaseReference mData;
    HinhAdapter galeryAdapter;
    List<String> listHinh;
    private Context mContext;
    private int CountLike=0,CountComment=0,CountShare=0;
    private FirebaseAuth mAuth;
    boolean check;
    int mselected = -1;
    String keyRemove="";
    List<Comment> commentList;
    BinhLuanAdapter binhLuanAdapter;
    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public PostAdapter(Oncallback mListener, List<PostStatus> listPost) {
        this.mListener = mListener;
        this.listPost = listPost;
        this.mData=FirebaseDatabase.getInstance().getReference();
        listHinh=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        commentList = new ArrayList<>();
    }
    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postitems,parent,false);
        mContext=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, final int position) {
        final PostStatus postStatus = listPost.get(position);
        mData.child("Users").child(postStatus.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.tvName.setText(user.getName());
                if (!user.equals("")) {
                    Picasso.get().load(user.getLinkAvatar()).into(holder.imAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String time = getTimeAgo(postStatus.getTime(), mContext);
        holder.tvTime.setText(time);
        if (postStatus.getAccess().equals("0")) {
            holder.tvQuyen.setBackgroundResource(R.drawable.congkhai);
        } else if (postStatus.getAccess().equals("1")) {
            holder.tvQuyen.setBackgroundResource(R.drawable.withmyfriends);
        } else if (postStatus.getAccess().equals("2")) {
            holder.tvQuyen.setBackgroundResource(R.drawable.riengtu);
        }

        if (postStatus.getLinkAnh() == null) {
            holder.tvNoiDung.setText(postStatus.getContent());
            holder.grHinh.setVisibility(View.INVISIBLE);
        } else {
            holder.tvNoiDung.setText(postStatus.getContent());
            holder.grHinh.setVisibility(View.VISIBLE);
            listHinh = postStatus.getLinkAnh();
            holder.grHinh.setHasFixedSize(true);
            holder.grHinh.setLayoutManager(new GridLayoutManager(mContext, 3));
            galeryAdapter = new HinhAdapter(this, listHinh,null,null,null);
            holder.grHinh.setAdapter(galeryAdapter);
        }
        if (postStatus.getListBinhLuan() != null)
        {
            holder.tvComment.setText(postStatus.getListBinhLuan().size()+" bình luận");
        }
        if(postStatus.getListLike()!=null)
        {
            holder.tvLike.setText(postStatus.getListLike().size()+" lượt thích");
            for (Map.Entry<String, String> entry : postStatus.getListLike().entrySet()) {
                String value = entry.getValue();
                if (value.equals(mAuth.getCurrentUser().getUid())) {
                    holder.btnLike.setTextColor(Color.BLUE);
                    listPost.get(position).setCheck(true);
                    setCheck(true);
                    break;
                }
            }
        }
        else
        {
            setCheck(false);
        }
        if(postStatus.getListShare()!=null)
        {
            holder.tvShare.setText(postStatus.getListShare().size()+" lượt chia sẻ");
        }
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountShare++;
                mData.child("PostActivity").child(postStatus.getKey()).child("CountShare").setValue(CountShare);
                mData.child("PostActivity").child(postStatus.getKey()).child("listShare").push().setValue(mAuth.getCurrentUser().getUid());
                holder.tvShare.setText(CountShare+" chia sẻ");
                holder.btnShare.setTextColor(Color.BLUE);
                if(!postStatus.getId().equals(mAuth.getCurrentUser().getUid()))
                {
                    ThongBao thongBao = new ThongBao(mAuth.getCurrentUser().getUid()," đã chia sẻ bài viết của bạn!",0);
                    String key = mData.push().getKey();
                    mData.child("TB").child(postStatus.getId()).child(key).setValue(thongBao);
                    mData.child("TB").child(postStatus.getId()).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
                }
            }
        });
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layoutcomment.setVisibility(View.VISIBLE);
                if(postStatus.getListBinhLuan()==null)
                {
                    holder.rvListComment.setVisibility(View.GONE);

                }
                else
                {
                    commentList.clear();
                    for(Map.Entry<String, Comment> entry : postStatus.getListBinhLuan().entrySet())
                    {
                        Comment value = entry.getValue();
                        commentList.add(value);
                    }
                    Collections.reverse(commentList);
                    holder.rvListComment.setVisibility(View.VISIBLE);
                    binhLuanAdapter = new BinhLuanAdapter(new BinhLuanAdapter.Oncallback() {
                        @Override
                        public void onItemClick(int position) {

                        }
                    }, commentList);

                    holder.rvListComment.setHasFixedSize(true);
                    holder.rvListComment.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.rvListComment.setAdapter(binhLuanAdapter);
//                    holder.btnLike.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            updateEvent(holder, postStatus);
//                        }
//                    });
                    binhLuanAdapter.notifyDataSetChanged();
                }

                if(mAuth.getCurrentUser().getPhotoUrl()!=null)
                {
                    Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(holder.imAvatarComment);
                }

                    holder.NDComment.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                // Perform action on key press
                                final String ND = holder.NDComment.getText().toString();

                                if(!ND.equals(""))
                                {
                                   // commentList.clear();
                                    for(int i=0;i<commentList.size();i++)
                                    {
                                        if(!commentList.get(i).getID().equals(postStatus.getKey()))
                                        {
                                            commentList.remove(i);
                                        }
                                    }
                                    holder.rvListComment.setVisibility(View.VISIBLE);
                                    binhLuanAdapter = new BinhLuanAdapter(new BinhLuanAdapter.Oncallback() {
                                        @Override
                                        public void onItemClick(int position) {

                                        }
                                    }, commentList);
                                    holder.rvListComment.setHasFixedSize(true);
                                    holder.rvListComment.setLayoutManager(new LinearLayoutManager(mContext));
                                    holder.rvListComment.setAdapter(binhLuanAdapter);
                                    String key = mData.push().getKey();
                                    Comment comment = new Comment(key,postStatus.getKey(),mAuth.getCurrentUser().getUid(),ND,0);
                                    mData.child("PostActivity").child(postStatus.getKey()).child("listBinhLuan").child(key).setValue(comment);
                                    mData.child("PostActivity").child(postStatus.getKey()).child("listBinhLuan").child(key).child("time").setValue(ServerValue.TIMESTAMP);
                                    Toast.makeText(mContext,"Bình luận thành công",Toast.LENGTH_SHORT).show();
                                    commentList.add(comment);
                                    binhLuanAdapter.notifyDataSetChanged();
                                    holder.NDComment.setText("");
                                    EventTBComment(postStatus);

                                }
                                else
                                {
                                    Toast.makeText(mContext,"Bạn đang để trống nội dung bình luận",Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }
                            return false;
                        }
                    });

            }
        });
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateEvent(holder,postStatus);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    @Override
    public void onItemClick(int position) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvTime,tvQuyen,tvNoiDung,tvLike,tvShare,tvComment;
        ImageView imAvatar,imAvatarComment;
        Button btnLike,btnComment,btnShare;
        ConstraintLayout layoutcomment;
        RecyclerView grHinh,rvListComment;
        EditText NDComment;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNamePosts);
            tvTime = itemView.findViewById(R.id.tvTimepost);
            tvQuyen =itemView.findViewById(R.id.tvAccess);
            tvNoiDung=itemView.findViewById(R.id.tvNoidungpost);
            tvLike=itemView.findViewById(R.id.tvcountlike);
            tvComment=itemView.findViewById(R.id.tvCommentpost);
            tvShare=itemView.findViewById(R.id.tvSharepost);
            imAvatar=itemView.findViewById(R.id.imAvatarPost);
            btnLike=itemView.findViewById(R.id.btnLikePost);
            btnComment=itemView.findViewById(R.id.btnCommentPost);
            btnShare=itemView.findViewById(R.id.btnSharepost);
            grHinh = itemView.findViewById(R.id.gvhinhpost);
            layoutcomment =itemView.findViewById(R.id.layoutcomment);
            imAvatarComment = itemView.findViewById(R.id.imAvatarPost3);
            NDComment=itemView.findViewById(R.id.ndsubcomment);
            rvListComment=itemView.findViewById(R.id.rcsublistbinhluan);
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
    private void updateEvent(final ViewHolder holder, final PostStatus postStatus)
    {

        if(postStatus.isCheck())
        {
           mData.child("PostActivity").child(postStatus.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   PostStatus postStatus1 = dataSnapshot.getValue(PostStatus.class);
                   if(postStatus1.getListLike()!=null)
                   {
                       CountLike=postStatus1.getListLike().size();
                   }
                   CountLike--;
                   if(CountLike==0)
                   {
                       mData.child("PostActivity").child(postStatus.getKey()).child("listLike").removeValue();
                       holder.tvLike.setText("");
                   }
                   else
                   {
                       mData.child("PostActivity").child(postStatus.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               PostStatus postStatus1 = dataSnapshot.getValue(PostStatus.class);

                               for (Map.Entry<String, String> entry : postStatus1.getListLike().entrySet()) {
                                   String value = entry.getValue();
                                   if (value.equals(mAuth.getCurrentUser().getUid())) {
                                       keyRemove = entry.getKey();
                                       mData.child("PostActivity").child(postStatus.getKey()).child("listLike").child(keyRemove).removeValue();
                                       break;
                                   }

                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });

                       holder.tvLike.setText(CountLike +" lượt thích");

                   }

                   holder.btnLike.setTextColor(Color.BLACK);
                   setCheck(false);
                   postStatus.setCheck(false);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });


        }
        else
        {
            mData.child("PostActivity").child(postStatus.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    PostStatus postStatus1 = dataSnapshot.getValue(PostStatus.class);
                    if(postStatus1.getListLike()!=null)
                    {
                        CountLike=postStatus1.getListLike().size();
                    }
                    else
                    {
                        CountLike=0;
                    }
                    CountLike++;
                    mData.child("PostActivity").child(postStatus.getKey()).child("listLike").push().setValue(mAuth.getCurrentUser().getUid());
                    EventTBLike(postStatus);
                    holder.tvLike.setText(CountLike+" lượt thích");
                    holder.btnLike.setTextColor(Color.BLUE);
                    setCheck(true);
                    postStatus.setCheck(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
    private void EventTBLike(final PostStatus postStatus)
    {

        if(!postStatus.getId().equals(mAuth.getCurrentUser().getUid()))
        {
            ThongBao thongBao = new ThongBao(mAuth.getCurrentUser().getUid()," Đã thích bài viết của bạn!",0);
            mData.child("TB").child(postStatus.getId()).child(postStatus.getKey()).setValue(thongBao);
            mData.child("TB").child(postStatus.getId()).child(postStatus.getKey()).child("timestamp").setValue(ServerValue.TIMESTAMP);
        }

    }
    private void EventTBComment(final PostStatus postStatus)
    {
        if(!postStatus.getId().equals(mAuth.getCurrentUser().getUid()))
        {
            ThongBao thongBao = new ThongBao(mAuth.getCurrentUser().getUid()," Đã bình luận bài viết của bạn!",0);
            String key = mData.push().getKey();
            mData.child("TB").child(postStatus.getId()).child(key).setValue(thongBao);
            mData.child("TB").child(postStatus.getId()).child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
        }

    }

}
