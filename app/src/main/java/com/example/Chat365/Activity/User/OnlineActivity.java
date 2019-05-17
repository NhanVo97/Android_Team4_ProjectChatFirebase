package com.example.Chat365.Activity.User;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.Chat365.Adapter.RoomAdapter.ListUserOnlineAdapter;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OnlineActivity extends AppCompatActivity {
    private ListView lvOnline;
    private List<User> listOnline;
    private DatabaseReference mData;
    private ListUserOnlineAdapter listUserOnlineAdapter;
    private void initData(){
        // actionbar
        getSupportActionBar().setTitle("Ai đang Online");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Anh Xa
        lvOnline = findViewById(R.id.listOnline);
        // Firebase
        mData = FirebaseDatabase.getInstance().getReference();
        // init list online user
        listOnline = new ArrayList<>();
        listUserOnlineAdapter = new ListUserOnlineAdapter(getApplicationContext(), listOnline);
        lvOnline.setAdapter(listUserOnlineAdapter);
        getDataFirebase();
    }

    private void getDataFirebase() {
        mData.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getIsOnline().equals("true")) {
                    listOnline.add(user);
                }
                listUserOnlineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                for(int i = 0 ;i<listOnline.size();i++){
                    if(listOnline.get(i).getId().equals(user.getId())){
                        listOnline.set(i,user);
                    }
                }
                listUserOnlineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                listOnline.remove(user);
                listUserOnlineAdapter.notifyDataSetChanged();
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

        setContentView(R.layout.activity_online);
        // init
        initData();
        // Event
        lvOnline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", listOnline.get(position));
                intent.putExtra("UserBundle", bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
