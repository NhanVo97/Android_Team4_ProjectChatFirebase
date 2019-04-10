package com.example.Chat365.Utils.Management;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.example.Chat365.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
public class Session implements ValueEventListener {
    private DatabaseReference mData;
    private FirebaseAuth mCurrent;
    private Context mContext;

    public Session(DatabaseReference mData, FirebaseAuth mCurrent,Context mContext) {
        this.mData = mData;
        this.mCurrent = mCurrent;
        this.mContext = mContext;
    }


    public void initUser(){
        mData.child("Users").child(mCurrent.getCurrentUser().getUid()).addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
            if(sharedPreferences==null){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                User user = dataSnapshot.getValue(User.class);
                String json = gson.toJson(user);
                editor.putString("User",json);
                editor.commit();
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
