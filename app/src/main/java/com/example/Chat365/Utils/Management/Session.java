package com.example.Chat365.Utils.Management;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.Chat365.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Session implements ValueEventListener {
    private DatabaseReference mData;
    private FirebaseUser mCurrent;
    private Context mContext;
    private boolean isNew;

    public Session(DatabaseReference mData, FirebaseUser mCurrent, Context mContext, boolean isNew) {
        this.mData = mData;
        this.mCurrent = mCurrent;
        this.mContext = mContext;
        this.isNew = isNew;
    }


    public void initUser() {
        if (isNew) {
            // create new sharedPreferences
            saveUser(null);
        } else {
            mData.child("Users").child(mCurrent.getUid()).addListenerForSingleValueEvent(this);
        }

    }

    private void saveUser(DataSnapshot dataSnapshot) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("User", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        User user;
        if (isNew) {
            String key = mCurrent.getUid();
            user = new User(mCurrent.getDisplayName(), "", mCurrent.getEmail(), "",
                    "", "User", "", "", mCurrent.getPhotoUrl().toString(),
                    "", "", "", "", key, "true", "Xin chào! Tôi đang sử dụng chat365",
                    0);
            // add new record firebase
            mData.child("Users").child(mCurrent.getUid()).setValue(user);
        } else {
            // Update current user
            user = dataSnapshot.getValue(User.class);
            user.setIsOnline("true");
            // change status -> online, set new time
            mData.child("User").child(user.getId()).child("isOnline").setValue("true");
            mData.child("User").child(user.getId()).child("timestamp").setValue(ServerValue.TIMESTAMP);
        }
        String json = gson.toJson(user);
        editor.putString("User", json);
        editor.commit();
    }
    private void detroyUser(User user){
        mData.child("User").child(user.getId()).child("isOnline").setValue("true");
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
