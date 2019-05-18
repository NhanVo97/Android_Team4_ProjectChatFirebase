package com.example.Chat365.Utils.Management;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.Chat365.Model.LocationUser;
import com.example.Chat365.Model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Session implements ValueEventListener {
    private DatabaseReference mData;
    private FirebaseUser mCurrent;
    private Context mContext;
    private boolean isNew;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public Session(Context mContext) {
        this.mContext = mContext;
        sharedPreferences = mContext.getSharedPreferences("User", Context.MODE_PRIVATE);
        gson = new Gson();
        editor = sharedPreferences.edit();
    }

    public Session(DatabaseReference mData, FirebaseUser mCurrent, Context mContext, boolean isNew) {
        this.mData = mData;
        this.mCurrent = mCurrent;
        this.mContext = mContext;
        this.isNew = isNew;
        sharedPreferences = mContext.getSharedPreferences("User", Context.MODE_PRIVATE);
        gson = new Gson();
        editor = sharedPreferences.edit();
    }


    public void initUser() {
        if (isNew) {
            // create new sharedPreferences
            saveUser(null);
        } else {
            mData.child("Users").child(mCurrent.getUid()).addListenerForSingleValueEvent(this);
        }

    }
    public User getUser(){
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("User", Context.MODE_PRIVATE);
            if (sharedPreferences != null) {
                String json = sharedPreferences.getString("User","");
                Gson gson = new Gson();
                User user = gson.fromJson(json,User.class);
                return user;
            } else {
                return null;
            }
    }


    private void saveUser(DataSnapshot dataSnapshot) {
        User user;
        if (isNew) {
            String key = mCurrent.getUid();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            user = new User(mCurrent.getDisplayName(), "", mCurrent.getEmail(), "",
                    simpleDateFormat.format(date), "User", "", mCurrent.getPhotoUrl().toString(),"",
                    "", "", "", key,"Vn", "true", "Xin chào! Tôi đang sử dụng chat365",
                    0L,"",new LocationUser("","","",0,0,false),"","");
            // add new record firebase
            mData.child("Users").child(key).setValue(user);
            mData.child("Users").child(key).child("timestamp").setValue(ServerValue.TIMESTAMP);
        } else {
            // Update current user
            user = dataSnapshot.getValue(User.class);
            if(user !=  null){
                user.setIsOnline("true");
                // change status -> online, set new time
                mData.child("Users").child(user.getId()).child("isOnline").setValue("true");
                mData.child("Users").child(user.getId()).child("timestamp").setValue(ServerValue.TIMESTAMP);
            }
        }
        String json = gson.toJson(user);
        editor.putString("User", json);
        editor.commit();
    }
    public void updateSession(User user){
        String json = gson.toJson(user);
        editor.putString("User", json);
        editor.commit();
    }
    public void detroyUser(User user){
        mData.child("Users").child(user.getId()).child("isOnline").setValue("false");
        mData.child("Users").child(user.getId()).child("timestamp").setValue(ServerValue.TIMESTAMP);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("User").commit();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            saveUser(dataSnapshot);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
