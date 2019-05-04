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
import android.widget.Toast;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Activity.Post.PostActivity;
import com.example.Chat365.Adapter.PostAdapter;
import com.example.Chat365.Adapter.ProfileAdapter;
import com.example.Chat365.Fragment.FragmentListFriends;
import com.example.Chat365.Model.PostStatus;
import com.example.Chat365.Model.ProfileButton;
import com.example.Chat365.Model.RequestType;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ProfileActivity extends AppCompatActivity implements ProfileAdapter.Oncallback {
    RecyclerView recyclerView;
    List<ProfileButton> list;
    ImageView ivBackground;
    ImageView imageAvatar, imPost;
    TextView tvName;
    User user = null;
    FirebaseUser current;
    ProfileAdapter profileAdapter;
    EditText edSTT;
    boolean isChecked, ktBB;
    private DatabaseReference mData, mFriends;
    List<PostStatus> listPost = new ArrayList<>();
    PostAdapter postAdapter;
    RecyclerView lv;
    ConstraintLayout cstranlayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Trang Cá Nhân");
        setContentView(R.layout.activity_profile);
        // Anh Xa
        ivBackground = findViewById(R.id.backgroundAvatar);
        imageAvatar = findViewById(R.id.ibAvatar);
        tvName = findViewById(R.id.tvNameUser);
        lv = findViewById(R.id.rclistpostcanhan);
        imPost = findViewById(R.id.imAvatarPrv);
        edSTT = findViewById(R.id.edTTPRV);
        cstranlayout = findViewById(R.id.cstranlayout);
        mData = FirebaseDatabase.getInstance().getReference();
        mFriends = FirebaseDatabase.getInstance().getReference("Friends");
        mFriends.keepSynced(true);
        mData.keepSynced(true);
        current = FirebaseAuth.getInstance().getCurrentUser();
        postAdapter = new PostAdapter(new PostAdapter.Oncallback() {
            @Override
            public void onItemClick(int position) {

            }
        }, listPost);
        lv.setHasFixedSize(true);
        lv.setLayoutManager(new LinearLayoutManager(this));
        lv.setAdapter(postAdapter);

        if (current.getPhotoUrl() != null) {
            Picasso.get().load(current.getPhotoUrl()).into(imPost);
        }

        // Bundle and event
        Intent intent = getIntent();
        Bundle b = intent.getBundleExtra("UserBundle");
        user = (User) b.getSerializable("User");
        if (user != null) {
            getData(user);
            tvName.setText(user.getName());
            if (!user.getLinkAvatar().equals("")) {
                Picasso.get().load(user.getLinkAvatar()).into(imageAvatar);
            }
            if (user.getId().equals(current.getUid())) {
                edSTT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User", user);
                        intent.putExtra("BundleUser", bundle);
                        startActivity(intent);
                    }
                });
            } else {
                cstranlayout.setVisibility(View.GONE);
            }
        } else {
            backToHome();
        }

        // List ReclycleView Button
        intRecycleView();
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

    public void checkTT(boolean c) {
        isChecked = c;
    }

    public void checkBB(boolean c) {
        ktBB = c;
    }

    private void intRecycleView() {

        list = new ArrayList<>();
        if (current.getUid().equals(user.getId())) {
            list.add(new ProfileButton(R.drawable.edituser, "Chỉnh sửa cá nhân"));
            list.add(new ProfileButton(R.drawable.diary, "Nhật ký hoạt động"));
            list.add(new ProfileButton(R.drawable.listfriends, "Danh sách bạn bè"));
        } else {
            mFriends.child(current.getUid()).child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        list.add(new ProfileButton(R.drawable.friends, "Đã là bạn bè"));
                        list.add(new ProfileButton(R.drawable.message2, "Gửi tin nhắn"));
                        list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                        profileAdapter.notifyDataSetChanged();
                        checkBB(true);
                    } else {
                        checkBB(false);
                        mData.child("Friends_Req").child(current.getUid()).child(user.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    RequestType requestType = dataSnapshot.getValue(RequestType.class);
                                    if (requestType.getRequest_type().equals("Sent")) {
                                        checkTT(true);
                                        list.clear();
                                        list.add(new ProfileButton(R.drawable.addfriendfinish, "Đã gửi lời kết bạn"));
                                        list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                                        list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                                    } else if (requestType.getRequest_type().equals("Received")) {
                                        checkTT(true);
                                        list.clear();
                                        list.add(new ProfileButton(R.drawable.addfriendfinish, "Chấp nhận lời mời"));
                                        list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                                        list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                                    } else if (requestType.getRequest_type().equals("Follow")) {
                                        checkTT(true);
                                        list.clear();
                                        list.add(new ProfileButton(R.drawable.addfriendfinish, "Đang theo dõi"));
                                        list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                                        list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                                    }

                                } else {
                                    checkTT(false);
                                    list.clear();
                                    list.add(new ProfileButton(R.drawable.addfriends, "Thêm vào bạn bè"));
                                    list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                                    list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                                }
                                profileAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        recyclerView = findViewById(R.id.recycleviewButton);
        recyclerView.setHasFixedSize(true);// toc do nhanh hon
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); //so cot 3
        profileAdapter = new ProfileAdapter(this, list);
        recyclerView.setAdapter(profileAdapter);
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0 && current.getUid().equals(user.getId())) {
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("User", user);
            intent.putExtra("UserBundle", bundle);
            startActivity(intent);
        } else if (position == 1 && current.getUid().equals(user.getId())) {
            Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_SHORT).show();
        } else if (position == 2 && current.getUid().equals(user.getId())) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            FragmentListFriends fragmentListFriends = new FragmentListFriends();
            fragmentTransaction.replace(R.id.layout_profile, fragmentListFriends).addToBackStack("tag").commit();
        } else {
            list.clear();
            profileAdapter.notifyDataSetChanged();
            if (ktBB) {

            } else {

                if (isChecked == true && position == 0) {
                    list.clear();
                    list.add(new ProfileButton(R.drawable.addfriends, "Thêm vào bạn bè"));
                    list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                    list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                    isChecked = false;
                    DatabaseReference mFriendSent = FirebaseDatabase.getInstance().getReference("Friends_Req").child(current.getUid()).child(user.getId());
                    mFriendSent.removeValue();
                    DatabaseReference mFriendReceived = FirebaseDatabase.getInstance().getReference("Friends_Req").child(user.getId()).child(current.getUid());
                    mFriendReceived.removeValue();
                    profileAdapter.notifyDataSetChanged();
                } else if (position == 1) {

                    if (isChecked == false) {
                        list.clear();
                        list.add(new ProfileButton(R.drawable.addfriends, "Thêm vào bạn bè"));
                        list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                        list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                    } else {
                        list.clear();
                        list.add(new ProfileButton(R.drawable.addfriendfinish, "Đã gửi lời kết bạn"));
                        list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                        list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                    }
                    Intent intent = new Intent(getApplicationContext(), MessagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", user);
                    intent.putExtra("PrivateChat", bundle);
                    startActivity(intent);

                } else {
                    list.clear();
                    list.add(new ProfileButton(R.drawable.addfriendfinish, "Đã gửi lời kết bạn"));
                    list.add(new ProfileButton(R.drawable.message2, "Nhắn tin làm quen"));
                    list.add(new ProfileButton(R.drawable.picture3, "Xem album ảnh"));
                    mData.child("Friends_Req").child(current.getUid()).child(user.getId()).child("request_type").setValue("Sent");
                    mData.child("Friends_Req").child(current.getUid()).child(user.getId()).child("id").setValue(user.getId());
                    mData.child("Friends_Req").child(user.getId()).child(current.getUid()).child("request_type").setValue("Received");
                    mData.child("Friends_Req").child(user.getId()).child(current.getUid()).child("id").setValue(current.getUid());
                    profileAdapter.notifyDataSetChanged();
                    isChecked = true;
                }


            }
        }


    }
}
