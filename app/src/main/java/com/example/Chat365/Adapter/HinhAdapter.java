package com.example.Chat365.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.Chat365.Fragment.FragmentViewPicture;
import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class HinhAdapter extends RecyclerView.Adapter<HinhAdapter.ViewHolder> {
    private List<String> listhinh;
    private Oncallback mListener;
    private Context mContext;
    int mselected = -1;
    User user,cr;
    FirebaseStorage storage;
    StorageReference storageRef;
    UploadTask uploadTask;
    FirebaseAuth mAuth;
    DatabaseReference mData;
    LinearLayout ln;
    public HinhAdapter(Oncallback mListener,List<String> listhinh,User user,User cr, LinearLayout linearLayout) {
        this.mListener = mListener;
        this.listhinh = listhinh;
        this.user=user;
        storage = FirebaseStorage.getInstance();
        storageRef =storage.getReference();
        mAuth =FirebaseAuth.getInstance();
        this.cr=cr;
        mData = FirebaseDatabase.getInstance().getReference("PrivateChat");
        this.ln=linearLayout;

    }

    @NonNull
    @Override
    public HinhAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemposthinh,parent,false);
        mContext=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HinhAdapter.ViewHolder holder, final int position) {
        if(listhinh!=null)
        {
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            Glide.with(mContext).load(listhinh.get(position)).apply(options).into(holder.imPosthinh);
            holder.imPosthinh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){

                    FragmentTransaction fragmentTransaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
                    FragmentViewPicture fragmentViewPicture = new FragmentViewPicture();
                    Bundle bundle = new Bundle();
                    bundle.putString("LinkAnh",listhinh.get(position));
                    fragmentViewPicture.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null).replace(R.id.layoutpost,fragmentViewPicture);
                    fragmentTransaction.commit();
                }
            });
        }
        if(user!=null)
        {
            if(mselected==position)
            {
                holder.imPosthinh.setAlpha(0.2f);
                holder.btnSend.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.imPosthinh.setAlpha(0.9f);
                holder.btnSend.setVisibility(View.INVISIBLE);
            }
            holder.imPosthinh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mselected == position) {
                        mselected=-1;

                    } else {
                        mselected = position;

                    }
                    notifyDataSetChanged();
                }
            });
            holder.btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri file = Uri.fromFile(new File(listhinh.get(position)));
                    final StorageReference riversRef = storageRef.child("Post/"+user.getId()+"/"+file.getLastPathSegment());
                    uploadTask = riversRef.putFile(file);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Xu ly K thanh cong
                            Toast.makeText(mContext,"Up hình lỗi, Xin thử lại!",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Thanh Cong
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return riversRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        String key = mData.push().getKey();
                                        Calendar cal = Calendar.getInstance();
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                        String Time = sdf.format(cal.getTime());
                                        Message messageSend = new Message(user,downloadUri.toString(),Time,key,"false");
                                        mData.child(mAuth.getCurrentUser().getUid()).child(user.getId()).child(key).setValue(messageSend);
                                        Message messagereceived = new Message(cr,downloadUri.toString(),"Đã Nhận",key,"false");
                                        mData.child(user.getId()).child(mAuth.getCurrentUser().getUid()).child(key).setValue(messagereceived);
                                        ln.setVisibility(View.GONE);
                                    } else {

                                    }
                                }
                            });

                        }
                    });
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
                    mListener.onItemClick(getPosition());
                }
            });
        }
    }
    public interface Oncallback{
        void onItemClick(int position);
    }
}