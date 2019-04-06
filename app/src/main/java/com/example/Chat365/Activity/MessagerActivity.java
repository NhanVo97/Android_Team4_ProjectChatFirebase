package com.example.Chat365.Activity;

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

import com.example.Chat365.Adapter.HinhAdapter;
import com.example.Chat365.Adapter.MessageAdapter;
import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.GetTime;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.Chat365.Utils.CheckPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.example.Chat365.Utils.CheckPermission.checkPermissionREAD_EXTERNAL_STORAGE;

public class MessagerActivity extends AppCompatActivity implements MessageAdapter.Oncallback {
    RecyclerView recyclerView,rclistPicture;
    List<Message> listchat;
    Button btnsend;
    EditText editText;
    DatabaseReference mData,mUser;
    MessageAdapter messageAdapter;
    CircleImageView profile_image;
    TextView username,tt;
    FirebaseAuth mAuth;
    User currentuser;
    ImageButton imCamera,imPicture;
    int Request_Code_Image = 123;
    FirebaseStorage storage;
    StorageReference storageRef;
    public LinearLayout linearLayout;
    User user=null;
    List<String> list = new ArrayList();
    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    HinhAdapter hinhAdapter;

    public void setUser(User user) {
        this.currentuser = user;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUser();
    }

    public void getUser()
    {
        mUser.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    setUser(user);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(uri,null,null,null,null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast())
                    {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mAuth=FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagerActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        // Anh Xa
        listchat = new ArrayList<>();
        recyclerView = findViewById(R.id.reyclerview_message_list);
        btnsend = findViewById(R.id.button_chatbox_send);
        editText = findViewById(R.id.edittext_chatbox);
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        tt = findViewById(R.id.sttuser);
        imCamera = findViewById(R.id.imCameraMS);
        imPicture=findViewById(R.id.imPictureShow);
        linearLayout=findViewById(R.id.linerHinh);
        rclistPicture = findViewById(R.id.rcHinh);
        listchat = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference("PrivateChat");
        mUser =FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef= storage.getReference();
        mData.keepSynced(true);
        mUser.keepSynced(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("PrivateChat");
        if(bundle!=null)
        {
            user = (User) bundle.getSerializable("User");
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(!user.getAvatar().equals(""))
            {
                Picasso.get().load(user.getAvatar()).into(profile_image);
            }
            else
            {
                profile_image.setImageResource(R.drawable.noavt);
            }
            if(user.getIsOnline().equals("true"))
            {
                tt.setText("Đang hoạt động");
            }
            else
            {
                GetTime getTime = new GetTime();
                long lastTime = user.getTimestamp();
                String lastSeenTime = GetTime.getTimeAgo(lastTime, getApplicationContext());
                tt.setText(lastSeenTime);
            }
            username.setText(user.getName());
            LoadData(currentUser,user);
            btnsend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ND = editText.getText().toString();
                    if(ND.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"Tin nhắn không được bỏ trống!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        AddData(ND,user,currentUser);
                        editText.setText("");
                    }

                }
            });
            imCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,Request_Code_Image);
                    imCamera.setImageResource(R.drawable.camera_active);
                }
            });
            imPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayout.setVisibility(View.VISIBLE);
                    imPicture.setImageResource(R.drawable.hinh_active);
                    if (checkPermissionREAD_EXTERNAL_STORAGE(MessagerActivity.this)) {
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
                    }, list,user,currentuser,linearLayout);
                    rclistPicture.setHasFixedSize(true);
                    LinearLayoutManager layoutManager
                            = new LinearLayoutManager(MessagerActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    rclistPicture.setLayoutManager(layoutManager);
                    rclistPicture.setAdapter(hinhAdapter);
                }
            });
            messageAdapter = new MessageAdapter(this,listchat,getSupportFragmentManager());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(messageAdapter);
        }
    }

    public void AddData(String ND, User user, FirebaseUser current) {
        String key = mData.push().getKey();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String Time = sdf.format(cal.getTime());
        Message messageSend = new Message(user,ND,Time,key,"false");
        mData.child(current.getUid()).child(user.getId()).child(key).setValue(messageSend);
        Message messagereceived = new Message(currentuser,ND,"Đã Nhận",key,"false");
        mData.child(user.getId()).child(current.getUid()).child(key).setValue(messagereceived);
    }

    private void LoadData(final FirebaseUser currentUser, final User user) {
       listchat.clear();
        mData.child(currentUser.getUid()).child(user.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    Message message = dataSnapshot.getValue(Message.class);
                    if(message.getTime().equals("Đã Nhận"))
                    {
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String Time = sdf.format(cal.getTime());
                        mData.child(currentUser.getUid()).child(user.getId()).child(message.getKey()).child("isseen").setValue("true");
                        mData.child(currentUser.getUid()).child(user.getId()).child(message.getKey()).child("time").setValue(Time);
                    }
                    listchat.add(message);
                }
                    messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for(Message m : listchat)
                {
                    if(m.getKey().equals(key))
                    {
                        listchat.remove(m);
                        messageAdapter.notifyDataSetChanged();
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
    public void onItemClick(int position) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Request_Code_Image && resultCode == RESULT_OK && data!=null)
        {
            Calendar calendar = Calendar.getInstance();
            final StorageReference mountainsRef = storageRef.child("Messager/"+mAuth.getCurrentUser().getUid()+"/"+calendar.getTimeInMillis()+".png");
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            try {
                final UploadTask uploadTask = mountainsRef.putBytes(byteArray);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return mountainsRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    AddData(downloadUri.toString(),user,mAuth.getCurrentUser());
                                } else {

                                }
                            }
                        });
                    }
                });
            stream.close();
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
