package com.example.Chat365.Activity.User;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Chat365.Adapter.FriendsChatAdapter;
import com.example.Chat365.Adapter.HinhAdapter;
import com.example.Chat365.Fragment.FragmentGroupInfo;
import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.Chat365.Utils.PermissionUtils.checkPermissionREAD_EXTERNAL_STORAGE;

public class RoomFriendsActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnsend;
    TextView tvNameGroup,tvActivity,tvInfo;
    RecyclerView rcListChat,rclistPicture;
    ImageView imAvt;
    GroupFriends friends;
    DatabaseReference mData;
    FirebaseUser mUser;
    List<Message> listChat;
    FirebaseStorage storage;
    StorageReference storageRef;
    ImageButton imCamera,imPicture;
    List<String> list;
    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    HinhAdapter hinhAdapter;
    LinearLayout linearLayout;
    User user;
    EditText editText;
    FriendsChatAdapter friendsChatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_friends);
        // Anh Xa
        tvNameGroup = findViewById(R.id.tvNameGroup);
        tvActivity = findViewById(R.id.sttActiveGroup);
        rcListChat = findViewById(R.id.reyclerview_message_list);
        imAvt = findViewById(R.id.profile_image);
        linearLayout=findViewById(R.id.linerHinh);
        imCamera = findViewById(R.id.imCameraMS);
        imPicture=findViewById(R.id.imPictureShow);
        rclistPicture = findViewById(R.id.rcHinh);
        editText = findViewById(R.id.edittext_chatbox);
        btnsend = findViewById(R.id.button_chatbox_send);
        tvInfo = findViewById(R.id.btnInfo);
        mData = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        list = new ArrayList<>();
        listChat = new ArrayList<>();
        Session session = new Session(mData,mUser,getApplicationContext(),false);
        user = session.getUser();
        friendsChatAdapter = new FriendsChatAdapter(listChat);
        rcListChat.setHasFixedSize(true);
        rcListChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rcListChat.setAdapter(friendsChatAdapter);
        // Get Data
        Bundle bundle = getIntent().getBundleExtra("BundleGr");
        friends = (GroupFriends) bundle.getSerializable("FriendsGroup");
        // Init Data
        initData();
        // event
        imCamera.setOnClickListener(this);
        imPicture.setOnClickListener(this);
        btnsend.setOnClickListener(this);
        tvInfo.setOnClickListener(this);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant
                    .REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(uri,null,null,null,null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        list.add(link);
                        cursor.moveToNext();
                    }
                    hinhAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Từ chối quyền truy cập đa phương tiện",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
    private void initData() {
        if(!friends.getLinkAvatar().isEmpty()){
            Glide.with(getApplicationContext())
                    .load(friends.getLinkAvatar().isEmpty())
                    .centerCrop()
                    .placeholder(R.drawable.spinne_loading)
                    .into(imAvt);
        }
        tvNameGroup.setText(friends.getNameGroup());
        // Check active friends list
        mData.child("ChatFriends").child(friends.getKey()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Message message = dataSnapshot.getValue(Message.class);
               listChat.add(message);
                friendsChatAdapter.notifyDataSetChanged();
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
    public void AddData(String ND, User user) {
        String key = mData.push().getKey();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String Time = sdf.format(cal.getTime());
        Message messageSend = new Message(user,ND,Time,key,"false");
        mData.child("ChatFriends").child(friends.getKey()).child(key).setValue(messageSend);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imCameraMS:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,Constant.REQUEST_CODE_IMAGE);
                imCamera.setImageResource(R.drawable.camera_active);
                break;
            case R.id.imPictureShow:
                linearLayout.setVisibility(View.VISIBLE);
                imPicture.setImageResource(R.drawable.hinh_active);
                if (checkPermissionREAD_EXTERNAL_STORAGE(RoomFriendsActivity.this)) {
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(uri,null,null,null,null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast())
                    {
                        String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        list.add(link);
                        cursor.moveToNext();
                    }
                }
                hinhAdapter = new HinhAdapter(new HinhAdapter.Oncallback() {
                    @Override
                    public void onItemClick(int position) {

                    }
                }, list,user,user,linearLayout);
                rclistPicture.setHasFixedSize(true);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(RoomFriendsActivity.this, LinearLayoutManager.HORIZONTAL, false);
                rclistPicture.setLayoutManager(layoutManager);
                rclistPicture.setAdapter(hinhAdapter);
                break;
            case R.id.button_chatbox_send:
                String ND = editText.getText().toString();
                if(ND.equals("")) {
                    Toast.makeText(getApplicationContext(),"Tin nhắn không được bỏ trống!",Toast.LENGTH_SHORT).show();
                }
                else {
                    AddData(ND,user);
                    editText.setText("");
                }
                break;
            case R.id.btnInfo:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentGroupInfo fragmentGroupInfo = new FragmentGroupInfo();
                fragmentManager.beginTransaction().replace(R.id.csLayOut,fragmentGroupInfo)
                        .addToBackStack("STACK").commit();
                break;
        }
    }
}
