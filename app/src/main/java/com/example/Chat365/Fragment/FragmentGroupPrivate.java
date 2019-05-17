package com.example.Chat365.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Chat365.Activity.User.MainActivity;
import com.example.Chat365.Activity.User.RoomFriendsActivity;
import com.example.Chat365.Adapter.UserAdapter.GroupChatAdapter.ListGroupAdapter;
import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FragmentGroupPrivate extends Fragment implements ListGroupAdapter.OnCallBack {
    View v;
    TextView btnCreate,tvStatus;
    DatabaseReference mData;
    RecyclerView rcGroup;
    ListGroupAdapter listGroupAdapter;
    List<GroupFriends> listGroup;
    FirebaseUser firebaseUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.groupchat,container,false);
        btnCreate = v.findViewById(R.id.btnCreatGroup);
        Toolbar toolbar = v.findViewById(R.id.toolbargr);
        tvStatus = v.findViewById(R.id.tvStatus);
        rcGroup = v.findViewById(R.id.grvNhom);
        mData = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Nhóm Chat");
        listGroup = new ArrayList<>();
        listGroupAdapter = new ListGroupAdapter(listGroup,this);
        rcGroup.setHasFixedSize(true);
        rcGroup.setLayoutManager(new GridLayoutManager(getContext(),3));
        rcGroup.setAdapter(listGroupAdapter);
        // Event
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentCreateGroup fragmentCreateGroup = new FragmentCreateGroup();
                fragmentTransaction.replace(R.id.layoutgroups, fragmentCreateGroup).addToBackStack("tag").commit();
            }
        });
        // Init Data
        initData();
        // iCcheck

        return v;
    }

    private void initData() {
        mData.child("GroupChat").child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupFriends groupFriends = dataSnapshot.getValue(GroupFriends.class);
                if(groupFriends!=null){
                    listGroup.add(groupFriends);
                }
                listGroupAdapter.notifyDataSetChanged();
                if(listGroup.size()>0){
                    tvStatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GroupFriends groupFriends = dataSnapshot.getValue(GroupFriends.class);
                for(int i = 0;i<listGroup.size();i++){
                    if(groupFriends.getKey().equals(listGroup.get(i).getKey())){
                        listGroup.set(i,groupFriends);
                    }
                }
                listGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                GroupFriends groupFriends = dataSnapshot.getValue(GroupFriends.class);
                listGroup.remove(groupFriends);
                listGroupAdapter.notifyDataSetChanged();
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
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), RoomFriendsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("FriendsGroup",listGroup.get(position));
        intent.putExtra("BundleGr",bundle);
        startActivity(intent);
    }
}
