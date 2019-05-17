package com.example.Chat365.Activity.User;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.Adapter.UserAdapter.MessagePrivateAdapter;
import com.example.Chat365.Adapter.UserAdapter.PostAdapter.PictureAdapter;
import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
import com.example.Chat365.Utils.TimeUtils;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.Chat365.Utils.PermissionUtils.checkPermissionREAD_EXTERNAL_STORAGE;

public class MessagerActivity extends AppCompatActivity implements PictureAdapter.OnCallback, View.OnClickListener {

    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private RecyclerView rcMessagePrivate, rclistPicture;
    private Toolbar toolbarMessage;
    private Button btnSend;
    private EditText edChatBox;
    private LinearLayout layoutGaleryHide;
    private CircleImageView profile_image;
    private TextView username, tvStatusActivity;
    private ImageButton imCamera, imPicture;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference mData;
    private User userChat,currentUser;
    private List<String> listGalery;
    private List<Message> listMessage;
    int Request_Code_Image = 123;
    private MessagePrivateAdapter messagePrivateAdapter;
    private PictureAdapter pictureAdapter;
    private Session session;
    private UploadTask uploadTask;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant
                    .REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadGalery();
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
    private void AnhXa(){
        // session
        session = new Session(this);
        currentUser = session.getUser();
        toolbarMessage = findViewById(R.id.toolbar);
        rcMessagePrivate = findViewById(R.id.reyclerview_message_list);
        btnSend = findViewById(R.id.button_chatbox_send);
        edChatBox = findViewById(R.id.edittext_chatbox);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.tvNameGroup);
        tvStatusActivity = findViewById(R.id.sttuser);
        imCamera = findViewById(R.id.imCameraMS);
        imPicture = findViewById(R.id.imPictureShow);
        layoutGaleryHide = findViewById(R.id.linerHinh);
        rclistPicture = findViewById(R.id.rcHinh);
        // Firebase Init
        mData = FirebaseDatabase.getInstance().getReference("PrivateChat");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // init list
        listMessage = new ArrayList<>();
        listGalery = new ArrayList<>();
        // init adapter
        pictureAdapter = new PictureAdapter(listGalery,this);
        rclistPicture.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessagerActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        rclistPicture.setLayoutManager(layoutManager);
        rclistPicture.setAdapter(pictureAdapter);

        messagePrivateAdapter = new MessagePrivateAdapter( listMessage, getSupportFragmentManager());
        rcMessagePrivate.setHasFixedSize(true);
        rcMessagePrivate.setLayoutManager(new LinearLayoutManager(this));
        rcMessagePrivate.setAdapter(messagePrivateAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messager);
        // Anh Xa
        AnhXa();
       // Init Data
        initData();
        // Event
        imCamera.setOnClickListener(this);
        imPicture.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        toolbarMessage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagerActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    private void initData() {
        // set action bar custom
        setSupportActionBar(toolbarMessage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get userChat from another activity
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("PrivateChat");
        userChat = (User) bundle.getSerializable("User");
        // set Avatar
        if (!userChat.getLinkAvatar().equals("")) {
            Picasso.get().load(userChat.getLinkAvatar()).into(profile_image);
        } else {
            profile_image.setImageResource(R.drawable.noavt);
        }
        // Check time activity
        if (userChat.getIsOnline().equals("true")) {
            tvStatusActivity.setText("Đang hoạt động");
        } else {
            String lastSeenTime = TimeUtils.getTimeAgo(userChat.getTimestamp(), getApplicationContext());
            tvStatusActivity.setText(lastSeenTime);
        }
        // set name
        username.setText(userChat.getName());
        // load list chat
        LoadData(currentUser,userChat);
    }

    public void sendMessage(String ND, User user, User currentUser) {
        String key = mData.push().getKey();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(cal.getTime());
        // sent
        Message messageSend = new Message(user, ND, time, key, "false");
        mData.child(currentUser.getId()).child(user.getId()).child(key).setValue(messageSend);
        // received
        Message messageReceived = new Message(currentUser, ND, "Đã Nhận", key, "false");
        mData.child(user.getId()).child(currentUser.getId()).child(key).setValue(messageReceived);
    }

    private void LoadData(final User currentUser, final User user) {
        listMessage.clear();
        mData.child(currentUser.getId()).child(user.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message.getTime().equals("Đã Nhận")) {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String Time = sdf.format(cal.getTime());
                        mData.child(currentUser.getId()).child(user.getId()).child(message.getKey()).child("isSeen").setValue("true");
                        mData.child(currentUser.getId()).child(user.getId()).child(message.getKey()).child("time").setValue(Time);
                    }
                    listMessage.add(message);
                }
                messagePrivateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for (Message m : listMessage) {
                    if (m.getKey().equals(key)) {
                        listMessage.remove(m);
                        messagePrivateAdapter.notifyDataSetChanged();
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

    // take of photo and send message
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Request_Code_Image && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            uploadProcessGalery(null,"Message/",byteArray);
            imCamera.setImageResource(R.drawable.camera_notactive);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // get picture from internal storage
    private void loadGalery(){
        listGalery.clear();
        if (checkPermissionREAD_EXTERNAL_STORAGE(MessagerActivity.this)) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                listGalery.add(link);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
    private void uploadProcessGalery(Uri linkGalery,String path,byte[] byteArray){
         StorageReference riversRef ;
        Calendar calendar = Calendar.getInstance();
        if(byteArray == null){
            riversRef = storageRef.child(path + currentUser.getId()
                    + "/" + linkGalery.getLastPathSegment());
            uploadTask = riversRef.putFile(linkGalery);
        } else {
            riversRef = storageRef.child(path + currentUser.getId() + "/" + calendar.getTimeInMillis() + ".png");
            uploadTask = riversRef.putBytes(byteArray);
        }

        final StorageReference finalRiversRef = riversRef;
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
                        return finalRiversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            sendMessage(downloadUri.toString(),userChat,currentUser);
                        }
                    }
                });

            }
        });
    }
    // Event click view
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imCameraMS:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Request_Code_Image);
                imCamera.setImageResource(R.drawable.camera_active);
            break;
            case R.id.imPictureShow:
                // enable layout hide & set image active
                layoutGaleryHide.setVisibility(View.VISIBLE);
                imPicture.setImageResource(R.drawable.hinh_active);
                // load picture from internal storage
                loadGalery();
                pictureAdapter.notifyDataSetChanged();

            break;
            case R.id.button_chatbox_send:
                String messageChat = edChatBox.getText().toString();
                if (messageChat.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Tin nhắn không được bỏ trống!", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(messageChat, userChat, currentUser);
                    edChatBox.setText("");
                }
                break;
        }
    }

    @Override
    public void onPictureClick(int position) {
        Uri file = Uri.fromFile(new File(listGalery.get(position)));
        uploadProcessGalery(file,"Message/",null);
        layoutGaleryHide.setVisibility(View.GONE);
        imPicture.setImageResource(R.drawable.hinh_notactive);
    }
}
