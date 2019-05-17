package com.example.Chat365.Activity.Post;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.Activity.Galery.AlbumActivity;
import com.example.Chat365.Activity.Galery.AllGaleryActivity;
import com.example.Chat365.Activity.User.MainActivity;
import com.example.Chat365.Activity.User.QuyenActivity;
import com.example.Chat365.Adapter.AnotherAdapter.SpinnerAdapter;
import com.example.Chat365.Adapter.LibraryAdapter.GaleryAdapter;
import com.example.Chat365.Model.Gallery;
import com.example.Chat365.Model.Other;
import com.example.Chat365.Model.PostStatus;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;;
import com.example.Chat365.Utils.Management.Session;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.Chat365.Utils.Constant.REQUEST_CODE_ALBUM;
import static com.example.Chat365.Utils.Constant.REQUEST_CODE_GALERY;
import static com.example.Chat365.Utils.Constant.REQUEST_CODE_PERMISSION;

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        View.OnClickListener, View.OnTouchListener {
    private Spinner spPermission, spAlbum;
    private SpinnerAdapter spAdapterPermission, spAdapterAlbum;
    private List<Other> permissionList, listAlbum;
    private ImageView imAvatar, imGalery;
    private GridView imPosthinh;
    private TextView tvName, btnShare;
    private FirebaseAuth mAuth;
    private EditText edStatusPost;
    private String permission = "";
    private String album = "";
    private DatabaseReference mData;
    private User user = null;
    private FirebaseStorage storage;
    private List<Gallery> linkAnh;
    private StorageReference storageRef;
    private UploadTask uploadTask;
    private android.support.v7.widget.Toolbar tbPost;
    private Session session;
    private GaleryAdapter galeryAdapter;

    private void initData() {
        // Action bar & set data
        tbPost = findViewById(R.id.tbQuyen);
        setSupportActionBar(tbPost);
        getSupportActionBar().setTitle("Tạo bài viết");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Other view
        imAvatar = findViewById(R.id.imavtpost);
        tvName = findViewById(R.id.tvNamePost);
        btnShare = findViewById(R.id.btnShare);
        edStatusPost = findViewById(R.id.edPostSTT);
        imGalery = findViewById(R.id.btnGalery);
        imPosthinh = findViewById(R.id.imPosthinh);
        // List init
        permissionList = new ArrayList<>();
        listAlbum = new ArrayList<>();
        // Spinner permission & Album
        spPermission = findViewById(R.id.spAccess);
        spAlbum = findViewById(R.id.spAlbum);

        // Init Firebase
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Init data spinner permission
        permissionList.add(new Other(R.drawable.congkhai, "Công Khai"));
        permissionList.add(new Other(R.drawable.withmyfriends, "Bạn Bè"));
        permissionList.add(new Other(R.drawable.riengtu, "Chỉ Mình Tôi"));
        spAdapterPermission = new SpinnerAdapter(this, R.layout.customspinner, permissionList);
        spPermission.setAdapter(spAdapterPermission);
        // init data ablum
        listAlbum.add(new Other(R.drawable.add, "MyAlbum"));
        spAdapterAlbum = new SpinnerAdapter(this, R.layout.customspinner, listAlbum);
        spAlbum.setAdapter(spAdapterAlbum);
        // session
        session = new Session(this);
        user = session.getUser();
        tvName.setText(user.getName());
        if (!user.getLinkAvatar().isEmpty()) {
            Picasso.get().load(user.getLinkAvatar()).into(imAvatar);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initData();
        // Event
        spPermission.setOnItemSelectedListener(this);
        spPermission.setOnTouchListener(this);
        spAlbum.setOnItemSelectedListener(this);
        spAlbum.setOnTouchListener(this);
        imGalery.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        edStatusPost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                edStatusPost.getWindowVisibleDisplayFrame(r);
                int screenHeight = edStatusPost.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    imPosthinh.setVisibility(View.INVISIBLE);
                } else {
                    imPosthinh.setVisibility(View.VISIBLE);
                }
            }
        });
        tbPost.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    private void uploadProcessGalery(Uri linkGalery, String path, byte[] byteArray, final List<String> listLinkAnh
            , final String key, final String ND){
        StorageReference riversRef ;
        Calendar calendar = Calendar.getInstance();
        album = !album.isEmpty() ? album : "MyAlbum";
        if(byteArray == null){
            riversRef = storageRef.child(path + user.getId()
                    + "/" + linkGalery.getLastPathSegment());
            uploadTask = riversRef.putFile(linkGalery);
        } else {
            riversRef = storageRef.child(path + user.getId() + "/" + calendar.getTimeInMillis() + ".png");
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
                            listLinkAnh.add(downloadUri + "");
                            updateEvent(listLinkAnh, key, ND);
                            mData.child("Albums").child(user.getId()).child(album).child("listHinh").push().setValue(downloadUri + "");
                        }
                    }
                });

            }
        });
    }
    private void postStatus( final String ND) {
        Intent intent = getIntent();
        final String key = mData.push().getKey();
        PostStatus postStatus = null;
        final List<String> listLinkAnh = new ArrayList<>();
        if (linkAnh != null) {
            if (linkAnh.get(0).getByteArray() == null) {
                for (int i = 0; i < linkAnh.size(); i++) {
                    Uri file = Uri.fromFile(new File(linkAnh.get(i).getLink()));
                    uploadProcessGalery(file,"PostActivity/",null,listLinkAnh,key,ND);
                }
            } else {
                Bundle bundle = intent.getBundleExtra("BundleLinkAnh");
                byte[] array = bundle.getByteArray("Image");
                uploadProcessGalery(null,"PostActivity/",array,listLinkAnh,key,ND);
            }
        } else {
            permission = !permission.isEmpty() ? permission : "0";
            postStatus = new PostStatus(key, ND, 0, permission, mAuth.getCurrentUser().getUid(), null, false);
            mData.child("PostActivity").child(key).setValue(postStatus);
            mData.child("PostActivity").child(key).child("time").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void updateEvent(List<String> listLinkAnh, String key, String ND) {
        PostStatus postStatus = new PostStatus(key, ND, 0, permission, mAuth.getCurrentUser().getUid(), listLinkAnh, false);
        mData.child("PostActivity").child(key).setValue(postStatus);
        mData.child("PostActivity").child(key).child("time").setValue(ServerValue.TIMESTAMP);
    }

    // get value spinner select
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int idSelect = view.getId();
        switch (idSelect) {
            case R.id.spAccess:
                permission = String.valueOf(position);
                break;
            case R.id.spAlbum:
                Other other = listAlbum.get(position);
                album = other.getStr();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnShare:
                String postText = edStatusPost.getText().toString();
                if (postText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Nội dung bài viết không được bỏ trống!", Toast.LENGTH_SHORT).show();
                } else {
                    postStatus(postText);
                    Toast.makeText(getApplicationContext(), "Thêm bài viết thành công!", Toast.LENGTH_SHORT).show();
                    Intent callback = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(callback);
                    finish();
                }
                break;
            case R.id.btnGalery:
                Intent galeryPage = new Intent(getApplicationContext(), AllGaleryActivity.class);
                startActivityForResult(galeryPage, REQUEST_CODE_GALERY);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        switch (id) {
            case R.id.spAccess:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent pagePermission = new Intent(getApplicationContext(), QuyenActivity.class);
                    permission = !permission.isEmpty() ? permission : "0";
                    pagePermission.putExtra("Quyen", permission);
                    startActivityForResult(pagePermission, REQUEST_CODE_PERMISSION);
                }
                return true;
            case R.id.spAlbum:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent pageCreateAlbum = new Intent(getApplicationContext(), AlbumActivity.class);
                    startActivityForResult(pageCreateAlbum, REQUEST_CODE_ALBUM);
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ALBUM:
                String nameAlbum = data.getStringExtra("nameAlbum");
                Log.e("Album", nameAlbum);
                if (!nameAlbum.equals("album")) {
                    listAlbum.add(new Other(R.drawable.macdinh, nameAlbum));
                    spAlbum.setSelection((listAlbum.size() - 1));
                }
                break;
            case REQUEST_CODE_GALERY:
                linkAnh = (List<Gallery>) data.getBundleExtra("BundleLinkAnh")
                        .getSerializable("LinkAnh");
                if (linkAnh == null) {
                    linkAnh = new ArrayList<>();
                }
                galeryAdapter = new GaleryAdapter(getApplicationContext(), R.layout.itemgalery, linkAnh, false);
                imPosthinh.setAdapter(galeryAdapter);
                Log.e("LINKANHSIZE", linkAnh.size() + "");
                break;
            case REQUEST_CODE_PERMISSION:
                int positionPermission = data.getIntExtra("positionPermission", 0);
                spPermission.setSelection(positionPermission);
                Log.e("positionPermission", positionPermission + "");
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
