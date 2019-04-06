package com.example.Chat365.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Chat365.Adapter.ChatRoomAdapter;
import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.Chat365.Fragment.FragmentGroup.listRoom;

public class RoomChat extends AppCompatActivity implements ChatRoomAdapter.Oncallback{
    RecyclerView recyclerView;
    List<Message> listChat;
    DatabaseReference mData;
    ChatRoomAdapter chatRoomAdapter;
    Button btnSend;
    EditText edNd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);
        // Anh xa
        listChat = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference("ChatRoom");
        mData.keepSynced(true);
        recyclerView = findViewById(R.id.rcListchat);
        btnSend = findViewById(R.id.btnGui);
        edNd=findViewById(R.id.edContentChat);

        // GetData
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("RoomChat");
        if(bundle!=null)
        {
            final int idRoom = bundle.getInt("IDRoom");
            final User user = (User) bundle.getSerializable("User");
            LoadData(idRoom,user);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Room "+listRoom.get(idRoom).getStr());
            actionBar.setDisplayHomeAsUpEnabled(true);

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ND = edNd.getText().toString();
                    if(ND.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"Nội dung chat không được bỏ trống!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AddData(ND,user,idRoom);
                    }
                }
            });

             recyclerView.setHasFixedSize(true);
            //recyclerView.setLayoutManager(new GridLayoutManager(this,3)); //so cot 3
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            chatRoomAdapter = new ChatRoomAdapter(this,listChat);
            recyclerView.setAdapter(chatRoomAdapter);
        }

    }

    private void AddData(String ND, User user, int idRoom) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String Time = sdf.format(cal.getTime());
        String keys = mData.child(String.valueOf(idRoom)).push().getKey();
        Message message = new Message(user,ND,Time,keys,"false");
        mData.child(String.valueOf(idRoom)).child(keys).setValue(message);
        edNd.setText("");
    }
    private void LoadData(int room, User user) {
        if(listChat.size()>0)
        {
            listChat.clear();
        }
        mData.child(String.valueOf(room)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    listChat.add(message);
                    chatRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                   String k = dataSnapshot.getKey();
                    for(Message m : listChat)
                    {
                        if(m.getKey().equals(k))
                        {
                            listChat.remove(m);
                            chatRoomAdapter.notifyDataSetChanged();
                            break;
                        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {

    }
}
