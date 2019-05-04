package com.example.Chat365.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Activity.User.ProfileActivity;
import com.example.Chat365.Adapter.UserNearAdapter;
import com.example.Chat365.Model.LocationUser;
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

import java.util.ArrayList;
import java.util.List;

public class FragmentFindFriendNear extends Fragment implements UserNearAdapter.OnCallBack {
    View v;
    RecyclerView rc;
    Switch swHideLocation;
    UserNearAdapter userNearAdapter;
    DatabaseReference mData;
    FirebaseUser mCurrent;
    List<User> listUser;
    User user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_find_near, container, false);
        // Anh Xa
        rc = v.findViewById(R.id.rcListNearUser);
        swHideLocation = v.findViewById(R.id.swHideLocation);
        mData = FirebaseDatabase.getInstance().getReference();
        mData.keepSynced(true);
        mCurrent = FirebaseAuth.getInstance().getCurrentUser();
        listUser = new ArrayList<>();
        final Session session = new Session(mData,mCurrent,getContext(),false);
        user = session.getUser();
        if(user == null){
            backToHome();
        }
        userNearAdapter = new UserNearAdapter(listUser,this,user);
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(userNearAdapter);
        // init Data
        initData();

        // set Data
        swHideLocation.setChecked(user.getLocationUser().isHide());
        // Event
        swHideLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mData.child("Users").child(mCurrent.getUid()).child("locationUser")
                                    .child("hide").setValue(isChecked);
                LocationUser locationUser = user.getLocationUser();
                locationUser.setHide(isChecked);
                user.setLocationUser(locationUser);
                session.updateSession(user);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        swHideLocation.setChecked(user.getLocationUser().isHide());
    }

    private void backToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void initData() {

        mData.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                    if(user != null && !user.getLocationUser().isHide() && !user.getId().equals(mCurrent.getUid())){
                        listUser.add(user);
                    }
                  Log.e("AAA",listUser.size()+"");
                  userNearAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.getId().equals(mCurrent.getUid())){
                    if(user.getLocationUser().isHide()){
                        for(User item : listUser){
                            if(item.getId().equals(user.getId()) && user.getLocationUser().isHide() ){
                                listUser.remove(item);
                                break;
                            }
                        }
                    } else {
                        listUser.add(user);
                    }
                    userNearAdapter.notifyDataSetChanged();
                }
                }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                listUser.remove(user);
                userNearAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("User",listUser.get(position));
        intent.putExtra("UserBundle",bundle);
        startActivity(intent);
    }
}
