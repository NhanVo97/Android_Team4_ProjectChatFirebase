package com.example.Chat365.Activity.Library;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.Chat365.Adapter.UserAdapter.GroupChatAdapter.FriendSelectAdapter;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FriendPicker extends AppCompatActivity implements FriendSelectAdapter.OnCallBack {
    RecyclerView rcListFriend;
    List<Friends> listFriends,listFriendsSelected;
    DatabaseReference mData;
    FirebaseUser mCurrent;
    FriendSelectAdapter friendSelectAdapter;
    GroupFriends groupFriends;
    LinearLayout linearLayout;
    Button btnSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_picker);
        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chọn bạn bè ");
        actionBar.setDisplayHomeAsUpEnabled(true);
        rcListFriend = findViewById(R.id.rcListFriend);
        linearLayout = findViewById(R.id.linearLayout);
        btnSelect = findViewById(R.id.btnSelect);
        mData = FirebaseDatabase.getInstance().getReference();
        mCurrent = FirebaseAuth.getInstance().getCurrentUser();
        listFriendsSelected = new ArrayList<>();
        listFriends = new ArrayList<>();
        // init data friend
        groupFriends = (GroupFriends) getIntent().getBundleExtra("Bundle").getSerializable("FriendsGroup");
        friendSelectAdapter = new FriendSelectAdapter(listFriends,this,false,"");
        rcListFriend.setLayoutManager(new LinearLayoutManager(this));
        rcListFriend.setHasFixedSize(true);
        rcListFriend.setAdapter(friendSelectAdapter);
        initData();
        // Event
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("ListSelectFriends", (Serializable) listFriendsSelected);
                intent.putExtra("Bundle",bundle);
                setResult(Constant.REQUEST_CODE_SUCESS,intent);
                finish();
            }
        });
    }

    private void initData() {
        mData.child("Friends").child(mCurrent.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                if(friends!=null){
                    for(int i = 0;i<groupFriends.getListFriend().size();i++){
                        if(!groupFriends.getListFriend().get(i).equals(friends.getId())){
                            listFriends.add(friends);
                        }
                    }
                }
                if(listFriends.size()>0){
                    linearLayout.setVisibility(View.VISIBLE);
                }
                friendSelectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                if(friends!=null){
                    for(int i = 0 ;i<listFriends.size();i++){
                        if(listFriends.get(i).getId().equals(friends.getId())){
                            listFriends.set(i,friends);
                        }
                    }
                    friendSelectAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                if(friends!=null){
                    listFriends.remove(friends);
                    friendSelectAdapter.notifyDataSetChanged();
                }
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
    public void onItemSelect(int postion, boolean isPanelRight) {
        Friends friend = listFriends.get(postion);
        listFriends.get(postion).setCheck(!friend.isCheck());
        if(friend.isCheck()){
            listFriendsSelected.add(friend);
        } else {
            listFriendsSelected.remove(friend);
        }
        friendSelectAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ListSelectFriends", (Serializable) listFriendsSelected);
        intent.putExtra("Bundle",bundle);
        setResult(Constant.REQUEST_CODE_SUCESS,intent);
        finish();
        super.onBackPressed();
    }
}
