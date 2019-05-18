package com.example.Chat365.Activity.User;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Activity.Post.PostActivity;
import com.example.Chat365.Adapter.UserAdapter.PostAdapter.PostAdapter;
import com.example.Chat365.Adapter.UserAdapter.ProfileButtonAdapter;
import com.example.Chat365.Fragment.FragmentListFriends;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.PostStatus;
import com.example.Chat365.Model.ProfileButton;
import com.example.Chat365.Model.RequestType;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ProfileButtonAdapter.Oncallback,PostAdapter.Oncallback {
    private RecyclerView recyclerView;
    private List<ProfileButton> listButtonProfile;
    private ImageView imageAvatar, imPost;
    private TextView tvName;
    private User anotherUser,current;
    private ProfileButtonAdapter profileButtonAdapter;
    private EditText edPost;
    private boolean isRequest, isFriend;
    private DatabaseReference mData, mFriends;
    private List<PostStatus> listPost = new ArrayList<>();
    private PostAdapter postAdapter;
    private RecyclerView rcPost;
    private ConstraintLayout layoutPost;
    private Session session;
    private String requestTypeStr;
    public void getData(final User user) {
        listPost.clear();
        mData.keepSynced(true);
        mData.child("PostActivity").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostStatus postStatus = dataSnapshot.getValue(PostStatus.class);
                if (postStatus.getId().equals(user.getId())) {
                    listPost.add(postStatus);
                }
                Collections.reverse(listPost);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void anhXa(){
        // Anh Xa Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Trang Cá Nhân");
        // View other
        imageAvatar = findViewById(R.id.ibAvatar);
        tvName = findViewById(R.id.tvNameUser);
        rcPost = findViewById(R.id.rclistpostcanhan);
        imPost = findViewById(R.id.imAvatarPrv);
        edPost = findViewById(R.id.edTTPRV);
        layoutPost = findViewById(R.id.layoutPost);
        // listButtonProfile
        listPost = new ArrayList<>();
        listButtonProfile = new ArrayList<>();
        // Firebase
        mData = FirebaseDatabase.getInstance().getReference();
        mFriends = FirebaseDatabase.getInstance().getReference("Friends");
        // Init Adapter
        postAdapter = new PostAdapter(this, listPost);
        rcPost.setHasFixedSize(true);
        rcPost.setLayoutManager(new LinearLayoutManager(this));
        rcPost.setAdapter(postAdapter);
        // Session
        session = new Session(this);
        current = session.getUser();
        if(current == null){
            backToHome();
        }
    }
    private void initData(){
        // set Avatar
        if(!current.getLinkAvatar().isEmpty()){
            Picasso.get().load(current.getLinkAvatar()).into(imPost);
        }
        // Get from bundle
        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("UserBundle");
        anotherUser = (User) b.getSerializable("User");
        // set info when another anotherUser into my page
        if(anotherUser !=null){
            // set name
            tvName.setText(anotherUser.getName());
            // set avatar
            if (!anotherUser.getLinkAvatar().isEmpty()) {
                Picasso.get().load(anotherUser.getLinkAvatar()).into(imageAvatar);
            }
            // get post anotherUser
            getData(anotherUser);
        }
        // List ReclycleView Button
        recyclerView = findViewById(R.id.recycleviewButton);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); //so cot 3
        profileButtonAdapter = new ProfileButtonAdapter(this, listButtonProfile);
        recyclerView.setAdapter(profileButtonAdapter);
        intRecycleView();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // init data
        anhXa();
        initData();
        // Event
        if (current.getId().equals(anotherUser.getId())) {
            edPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            layoutPost.setVisibility(View.GONE);
        }
    }

    private void backToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void intRecycleView() {
        //case self
        if (current.getId().equals(anotherUser.getId())) {
            listButtonProfile.add(new ProfileButton(R.drawable.edituser, "Chỉnh sửa cá nhân"));
            listButtonProfile.add(new ProfileButton(R.drawable.diary, "Nhật ký hoạt động"));
            listButtonProfile.add(new ProfileButton(R.drawable.listfriends, "Danh sách bạn bè"));
            profileButtonAdapter.notifyDataSetChanged();
        } else {
            // 1 : friend , 2 stranger, 3. already has request
            listButtonProfile.add(new ProfileButton(R.drawable.addfriends, "Thêm là bạn bè"));
            listButtonProfile.add(new ProfileButton(R.drawable.message2, "Gửi tin nhắn"));
            listButtonProfile.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
            mFriends.child(current.getId()).child(anotherUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // case 1 is friend
                        isFriend = true;
                    } else {// not friend
                        isFriend = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mData.child("Friends_Req").child(current.getId()).child(anotherUser.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        RequestType requestType = dataSnapshot.getValue(RequestType.class);
                        if (requestType.getRequest_type().equals("Sent")) { // sender
                            //checkTT(true);
                            isRequest = true;
                            requestTypeStr = "Sent";
                            listButtonProfile.set(0,new ProfileButton(R.drawable.addfriendfinish, "Đã gửi lời kết bạn"));
                        } else if (requestType.getRequest_type().equals("Received")) { // receiver
//                                        checkTT(true);
                            isRequest = true;
                            requestTypeStr = "Received";
                            listButtonProfile.set(0,new ProfileButton(R.drawable.addfriendfinish, "Chấp nhận lời mời"));
                        } else if (requestType.getRequest_type().equals("Follow")) { // Follow
                            isRequest = true;
                            requestTypeStr = "Follow";
                            listButtonProfile.set(0,new ProfileButton(R.drawable.addfriendfinish, "Đang theo dõi"));
                        }
                    } else {
                        // case 2 stranger
                        isRequest = false;
                        listButtonProfile.set(0,new ProfileButton(R.drawable.addfriends, "Thêm vào bạn bè"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        profileButtonAdapter.notifyDataSetChanged();


    }

    @Override
    public void onItemClick(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // if itself watch page
        if(current.getId().equals(anotherUser.getId())){
            switch (position){
                case 0 : // can edit profile of user
                    Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", anotherUser);
                    intent.putExtra("UserBundle", bundle);
                    startActivity(intent);
                    break;
                case 1: // handle fragment activity diary
                    break;
                case 2: // handle fragment friend
                    FragmentListFriends fragmentListFriends = new FragmentListFriends();
                    fragmentTransaction.replace(R.id.layout_profile, fragmentListFriends).addToBackStack("tag").commit();
                    break;
            }
        } else {
            if(isFriend){ // friend
                switch (position){
                    case 0 : // unfriend
                        listButtonProfile.set(0,new ProfileButton(R.drawable.addfriends, "Thêm vào bạn bè"));
                        mFriends.child(current.getId()).child(anotherUser.getId()).removeValue();
                        mFriends.child(anotherUser.getId()).child(current.getId()).removeValue();
                        isFriend = false;
                        profileButtonAdapter.notifyDataSetChanged();
                        break;
                    case 1: // sent message
                        Intent intent = new Intent(getApplicationContext(), MessagerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User", anotherUser);
                        intent.putExtra("PrivateChat", bundle);
                        startActivity(intent);
                        break;
                    case 2: // see album

                }
            } else { // not friend
                    switch (position){
                        case 0 :
                            if(isRequest){// if have request check type request
                                if(requestTypeStr.equals("Sent")){ // remove request
                                    listButtonProfile.set(0,new ProfileButton(R.drawable.addfriends, "Thêm vào bạn bè"));
                                    DatabaseReference mFriendSent = FirebaseDatabase.getInstance().getReference("Friends_Req").child(current.getId()).child(anotherUser.getId());
                                    mFriendSent.removeValue();
                                    DatabaseReference mFriendReceived = FirebaseDatabase.getInstance().getReference("Friends_Req").child(anotherUser.getId()).child(current.getId());
                                    mFriendReceived.removeValue();

                                } else if(requestTypeStr.equals("Received")){ // accept friend
                                    Friends friendSent = new Friends(anotherUser.getId(),"Friends",false);
                                    Friends friendsReceived = new Friends(current.getId(),"Friends",false);
                                    listButtonProfile.set(0,new ProfileButton(R.drawable.addfriends, "Đã là bạn bè"));
                                    mData.child("Friends").child(current.getId()).child(anotherUser.getId()).setValue(friendSent);
                                    mData.child("Friends").child(anotherUser.getId()).child(current.getId()).setValue(friendsReceived);
                                    mData.child("Friends_Req").child(current.getId()).child(anotherUser.getId()).removeValue();
                                    mData.child("Friends_Req").child(anotherUser.getId()).child(current.getId()).removeValue();
                                    isFriend = true;
                                } else { // unfollow
                                    mData.child("Friends_Req").child(current.getId()).child(anotherUser.getId()).removeValue();
                                    listButtonProfile.set(0,new ProfileButton(R.drawable.addfriends, "Thêm vào bạn bè"));
                                }
                                profileButtonAdapter.notifyDataSetChanged();
                                isRequest = false;
                            } else { // sent request
                                isRequest = true;
                                listButtonProfile.set(0,new ProfileButton(R.drawable.addfriendfinish, "Đã gửi lời kết bạn"));
                                RequestType requestTypeSent = new RequestType(anotherUser.getId(),"Sent");
                                RequestType requestTypeReceived = new RequestType(current.getId(),"Received");
                                mData.child("Friends_Req").child(current.getId()).child(anotherUser.getId()).setValue(requestTypeSent);
                                mData.child("Friends_Req").child(anotherUser.getId()).child(current.getId()).setValue(requestTypeReceived);
                                profileButtonAdapter.notifyDataSetChanged();
                            }
                            break;
                        case 1: // sent messge
                            Intent intent = new Intent(getApplicationContext(), MessagerActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("User", anotherUser);
                            intent.putExtra("PrivateChat", bundle);
                            startActivity(intent);
                            break;
                        case 2:

                    }
                }
            }
        }

    }
