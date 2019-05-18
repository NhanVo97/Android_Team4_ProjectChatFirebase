package com.example.Chat365.Activity.VideoCall;

import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.UserPresenceUnavailableException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.Activity.User.MainActivity;
import com.example.Chat365.Activity.VideoCall.CallVideoOneActivity;
import com.example.Chat365.Model.CallVideo;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class WaitingCallActivity extends AppCompatActivity {
    View v;
    TextView tvName;
    TextView tvStatus;
    ImageView imAvt,btnStartCall,btnEndCall;
    DatabaseReference mData;
    FirebaseAuth mAuth;
    private void backToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
       finish();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_iscalling);
        tvName = findViewById(R.id.tvName);
        tvStatus = findViewById(R.id.tvStatus);
        imAvt = findViewById(R.id.imAvatar);
        btnStartCall = findViewById(R.id.btnStartCall);
        btnEndCall = findViewById(R.id.btnEndCall);
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        // init data
        initData();
        ListenerEvent();

    }

    private void ListenerEvent(){
        mData.child("VideoCall").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final CallVideo callVideo = dataSnapshot.getValue(CallVideo.class);
                Log.e("AAA",callVideo.getReceivedId()+"");
                Log.e("AAA",callVideo.getSenderId()+"");
                if(callVideo!=null){
                    if(callVideo.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                        btnStartCall.setVisibility(View.GONE);
                        mData.child("Users").child(callVideo.getReceivedId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                tvName.setText(user.getName());
                                if(!user.getLinkAvatar().isEmpty()){
                                    Picasso.get().load(user.getLinkAvatar()).into(imAvt);
                                }
                                tvStatus.setText("Đang đổ chuông");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                        btnEndCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                mData.child("VideoCall").child(mAuth.getCurrentUser().getUid()).removeValue();
                                mData.child("VideoCall").child(callVideo.getReceivedId()).removeValue();
                                backToMain();
                            }
                        });
                    } else {
                        mData.child("Users").child(callVideo.getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                tvName.setText(user.getName());
                                if(!user.getLinkAvatar().isEmpty()){
                                    Picasso.get().load(user.getLinkAvatar()).into(imAvt);
                                }
                                tvStatus.setText("Cuộc trò chuyện của "+user.getName());

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        btnStartCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mData.child("VideoCall").child(callVideo.getSenderId()).child(callVideo.getId())
                                        .child("statusReceivedId").setValue("Accept");
                                Intent intent = new Intent(getApplicationContext(), CallVideoOneActivity.class);
                                intent.putExtra("IDRemove",callVideo.getSenderId());
                                startActivity(intent);
                            }
                        });
                        btnEndCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mData.child("VideoCall").child(mAuth.getCurrentUser().getUid()).removeValue();
                                mData.child("VideoCall").child(callVideo.getSenderId()).removeValue();
                                backToMain();
                            }
                        });
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CallVideo callVideo = dataSnapshot.getValue(CallVideo.class);
                if(callVideo.getStatusReceivedId().equals("Accept")){
                    Intent intent = new Intent(getApplicationContext(), CallVideoOneActivity.class);
                    intent.putExtra("IDRemove",callVideo.getReceivedId());
                    startActivity(intent);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                backToMain();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initData() {
//        Bundle bundle = getArguments();
//        userChat = (User) bundle.getSerializable("UserCall");
//        if(userChat!=null){
//            taskCall.setVisibility(View.GONE);
//        }
    }

}
