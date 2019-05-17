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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.Adapter.UserAdapter.GroupChatAdapter.FriendChatAdapter;
import com.example.Chat365.Adapter.UserAdapter.PostAdapter.PictureAdapter;
import com.example.Chat365.Fragment.FragmentGroupInfo;
import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.Chat365.Utils.PermissionUtils.checkPermissionREAD_EXTERNAL_STORAGE;

public class RoomFriendsActivity extends AppCompatActivity implements View.OnClickListener,PictureAdapter.OnCallback {
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
    UploadTask uploadTask;
    ImageButton imCamera,imPicture;
    List<String> listHinh;
    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    PictureAdapter pictureAdapter;
    LinearLayout linearLayout;
    User user;
    EditText editText;
    FriendChatAdapter friendChatAdapter;
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
        listHinh = new ArrayList<>();
        listChat = new ArrayList<>();
        Session session = new Session(mData,mUser,getApplicationContext(),false);
        user = session.getUser();
        friendChatAdapter = new FriendChatAdapter(listChat);
        rcListChat.setHasFixedSize(true);
        rcListChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rcListChat.setAdapter(friendChatAdapter);
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
                        listHinh.add(link);
                        cursor.moveToNext();
                    }
                    pictureAdapter.notifyDataSetChanged();
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
            Picasso.get().load(friends.getLinkAvatar()).into(imAvt);
        }
        tvNameGroup.setText(friends.getNameGroup());

        // get last
        mData.child("RoomFriend").child(friends.getKey())
                .orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Message message = dataSnapshot.getValue(Message.class);
                    String dayTime = message.getTime().split("-")[0];
                    String lastTime = message.getTime().split("-")[1];
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
                    String currentTime = sdf.format(new Date());
                    if(lastTime.compareTo(currentTime) > 0 ){
                        tvActivity.setText("hoạt động ngày "+dayTime);
                    } else {
                        tvActivity.setText("hoạt động lúc "+lastTime);
                    }
                } else {
                    tvActivity.setText("Chưa có ai hoạt động");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    Message message = dataSnapshot.getValue(Message.class);
                    String dayTime = message.getTime().split("-")[0];
                    String lastTime = message.getTime().split("-")[1];
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
                    String currentTime = sdf.format(new Date());
                    if(lastTime.compareTo(currentTime) > 0 ){
                        tvActivity.setText("hoạt động ngày "+dayTime);
                    } else {
                        tvActivity.setText("hoạt động lúc "+lastTime);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                tvActivity.setText("Chưa có ai hoạt động");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Check data listHinh
        mData.child("RoomFriend").child(friends.getKey()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Message message = dataSnapshot.getValue(Message.class);
               listChat.add(message);
                friendChatAdapter.notifyDataSetChanged();
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imCameraMS:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,Constant.REQUEST_CODE_IMAGE);
                imCamera.setImageResource(R.drawable.camera_active);
                break;
            case R.id.imPictureShow:
                listHinh.clear();
                linearLayout.setVisibility(View.VISIBLE);
                imPicture.setImageResource(R.drawable.hinh_active);
                if (checkPermissionREAD_EXTERNAL_STORAGE(RoomFriendsActivity.this)) {
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(uri,null,null,null,null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        listHinh.add(link);
                        cursor.moveToNext();
                    }
                }
                pictureAdapter = new PictureAdapter(listHinh,this);
                rclistPicture.setHasFixedSize(true);
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(RoomFriendsActivity.this, LinearLayoutManager.HORIZONTAL, false);
                rclistPicture.setLayoutManager(layoutManager);
                rclistPicture.setAdapter(pictureAdapter);
                break;
            case R.id.button_chatbox_send:
                String ND = editText.getText().toString();
                if(ND.equals("")) {
                    Toast.makeText(getApplicationContext(),"Tin nhắn không được bỏ trống!",Toast.LENGTH_SHORT).show();
                }
                else {
                    AddData(ND,friends.getKey());
                    editText.setText("");
                }
                break;
            case R.id.btnInfo:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentGroupInfo fragmentGroupInfo = new FragmentGroupInfo();
                Bundle bundle = new Bundle();
                bundle.putSerializable("FriendsGroup",friends);
                fragmentGroupInfo.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.csLayOut,fragmentGroupInfo)
                        .addToBackStack("STACK").commit();
                break;
        }
    }

    @Override
    public void onPictureClick(int position) {
        sendHinh(position);
    }

    private void sendHinh(int position) {
        Uri file = Uri.fromFile(new File(listHinh.get(position)));
        final StorageReference riversRef = storageRef.child("GroupChatPrivate/" + friends.getKey() + "/" + file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xu ly K thanh cong
                Toast.makeText(getApplicationContext(), "Up hình lỗi, Xin thử lại!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Thanh Cong
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            AddData(downloadUri.toString(),friends.getKey());
                        }
                    }
                });

            }
        });
    }
    private void AddData(String ND,String keyRoomFriend) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
        String Time = sdf.format(cal.getTime());
        String key = mData.child("RoomFriend").push().getKey();
        Message message = new Message(user,ND,Time,key,"false");
        mData.child("RoomFriend").child(keyRoomFriend).child(key).setValue(message);
        editText.setText("");
        linearLayout.setVisibility(View.GONE);
        imPicture.setImageResource(R.drawable.hinh_notactive);
    }
}
