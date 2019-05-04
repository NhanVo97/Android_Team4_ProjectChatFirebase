package com.example.Chat365.Activity.Post;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.example.Chat365.Adapter.GaleryAdapter;
import com.example.Chat365.Adapter.SpinnerAdapter;
import com.example.Chat365.Model.Galery;
import com.example.Chat365.Model.Other;
import com.example.Chat365.Model.PostStatus;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;;
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

public class PostActivity extends AppCompatActivity {
    Spinner spAccess,spAlbum;
    SpinnerAdapter spinnerAdapter,spinnerAdapter2;
    List<Other> spAccessList,spListAlbum;
    ImageView imAvatar,imGalery;
    GridView imPosthinh;
    TextView tvName,btnShare;
    FirebaseAuth mAuth;
    EditText edND;
    String Access="";
    String Album="";
    DatabaseReference mData;
    User user=null;
    FirebaseStorage storage;
    List<Galery> linkAnh;
    StorageReference storageRef;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        // ANh Xa
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.tbQuyen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tạo bài viết");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        spAccess = findViewById(R.id.spAccess);
        spAlbum = findViewById(R.id.spAlbum);
        spAccessList = new ArrayList<>();
        spAccessList.add(new Other(R.drawable.congkhai,"Công Khai"));
        spAccessList.add(new Other(R.drawable.withmyfriends,"Bạn Bè"));
        spAccessList.add(new Other(R.drawable.riengtu,"Chỉ Mình Tôi"));
        spListAlbum = new ArrayList<>();
        imAvatar = findViewById(R.id.imavtpost);
        tvName = findViewById(R.id.tvNamePost);
        btnShare = findViewById(R.id.btnShare);
        edND = findViewById(R.id.edPostSTT);
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        imGalery =findViewById(R.id.btnGalery);
        imPosthinh =findViewById(R.id.imPosthinh);
        storageRef= storage.getReference();
        spinnerAdapter = new SpinnerAdapter(this,R.layout.customspinner,spAccessList);
        spAccess.setAdapter(spinnerAdapter);
        spListAlbum.add(new Other(R.drawable.add,"MyAlbum"));
        spinnerAdapter2 = new SpinnerAdapter(this,R.layout.customspinner,spListAlbum);
        spAlbum.setAdapter(spinnerAdapter2);
        // DATA
        final Intent intent= getIntent();
        Bundle bundle = intent.getBundleExtra("BundleUser");
        Bundle bdPicture = intent.getBundleExtra("BundleLinkAnh");

        if(bdPicture!=null)
        {
            String ndtext = bdPicture.getString("ND");
            // Image camera
            if(!ndtext.equals(""))
            {
                edND.setText(ndtext);
            }
            else {
                edND.setHint("Hãy nói gì đó về bức ảnh này...");
            }
            String quyen = bdPicture.getString("Quyen");
            if(!quyen.equals(""))
            {
                spAccess.setSelection(Integer.parseInt(quyen));
            }
            String Album = bdPicture.getString("Album");
            if(Album!=null)
            {
                if(!Album.equals("Album"))
                {
                    spListAlbum.add(new Other(R.drawable.macdinh,Album));
                    spAlbum.setSelection((spListAlbum.size()-1));
                }
            }

            User mBackuser = (User) bdPicture.getSerializable("User");
            tvName.setText(mBackuser.getName());
            user=mBackuser;
            Picasso.get().load(mBackuser.getLinkAvatar()).into(imAvatar);
            linkAnh = (List<Galery>) bdPicture.getSerializable("LinkAnh");
            if(linkAnh!=null)
            {
                GaleryAdapter galeryAdapter = new GaleryAdapter(this,R.layout.itemgalery,linkAnh);
                imPosthinh.setAdapter(galeryAdapter);
            }

        }
        if(bundle!=null)
        {
            user = (User) bundle.getSerializable("User");
            tvName.setText(user.getName());
            String NameAlbums = bundle.getString("NameAlbums");
            String Quyen = bundle.getString("Quyen");

            if(Quyen!=null)
            {
                spAccess.setSelection(Integer.parseInt(Quyen));
            }
            String ND = bundle.getString("ND");

            if(NameAlbums!=null)
            {
                spListAlbum.add(new Other(R.drawable.macdinh,NameAlbums));
                spAlbum.setSelection((spListAlbum.size()-1));
            }
            if(ND!=null)
            {
                edND.setText(ND);
            }
            if(!user.getLinkAvatar().equals(""))
            {
                Picasso.get().load(user.getLinkAvatar()).into(imAvatar);
            }
        }


        spAccess.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Access=String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spAccess.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    Intent callCreateAccess = new Intent(getApplicationContext(), QuyenActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("User",user);
                    bundle1.putString("ND",edND.getText().toString());
                    bundle1.putString("Quyen",Access);
                    bundle1.putString("Album",Album);
                    callCreateAccess.putExtra("BundleUser",bundle1);
                    startActivity(callCreateAccess);
                }
                return true;
            }
        });
        spAlbum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Other other = spListAlbum.get(position);
                Album = other.getStr();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spAlbum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    Intent callCreateAlbum = new Intent(getApplicationContext(), AlbumActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("User",user);
                    bundle1.putString("ND",edND.getText().toString());
                    bundle1.putString("Quyen",Access);
                    callCreateAlbum.putExtra("BundleUser",bundle1);
                    startActivity(callCreateAlbum);
                }
                return true;
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ND = edND.getText().toString();
                if(ND.equals("") && linkAnh==null)
                {
                    Toast.makeText(getApplicationContext(),"Nội dung bài viết không được bỏ trống!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    addData(user,ND);
                    Toast.makeText(getApplicationContext(),"Thêm bài viết thành công!",Toast.LENGTH_SHORT).show();
                    Intent callback = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(callback);
                    finish();
                }

            }
        });
        // Ask permission

        imGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galery = new Intent(getApplicationContext(), AllGaleryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User",user);
                if(linkAnh!=null)
                {
                    bundle.putSerializable("LinkAnh", (ArrayList<Galery>) linkAnh);
                }
                String ND = edND.getText().toString();
                bundle.putString("ND",ND);
                bundle.putString("Quyen",Access);
                bundle.putString("Album",Album);
                galery.putExtra("BundleUser",bundle);
                startActivity(galery);

            }
        });

        edND.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                edND.getWindowVisibleDisplayFrame(r);
                int screenHeight = edND.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                   imPosthinh.setVisibility(View.INVISIBLE);
                }
                else {
                    imPosthinh.setVisibility(View.VISIBLE);
                }
            }
        });

    }



    private void addData(final User user, final String ND) {
        Intent intent = getIntent();
        //List<Galery> linkAnh = intent.ge("LinkAnh");
        final String key = mData.push().getKey();
        PostStatus postStatus=null;
        Calendar calendar = Calendar.getInstance();
        final List<String> listLinkAnh = new ArrayList<>();
        final StorageReference mountainsRef = storageRef.child("PostActivity/"+user.getId()+"/"+Album+"/"+calendar.getTimeInMillis()+".png");
        if(linkAnh!=null)
        {
            if(linkAnh.get(0).getByteArray()==null)
            {
                for(int i=0;i<linkAnh.size();i++)
                {
                    Uri file = Uri.fromFile(new File(linkAnh.get(i).getLink()));
                    final StorageReference riversRef = storageRef.child("PostActivity/"+user.getId()+"/"+Album+file.getLastPathSegment());
                    uploadTask = riversRef.putFile(file);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Xu ly K thanh cong
                            Toast.makeText(getApplicationContext(),"Up hình lỗi, Xin thử lại!",Toast.LENGTH_SHORT).show();
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

                                    // Continue with the task to get the download URL
                                    return riversRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        listLinkAnh.add(downloadUri+"");
                                        updateEvent(listLinkAnh,key,ND);
                                        mData.child("Albums").child(user.getId()).child(Album).child("listHinh").push().setValue(downloadUri+"");
                                    } else {

                                    }
                                }
                            });

                        }
                    });


                }
            }
            else
            {
                Bundle bundle = intent.getBundleExtra("BundleLinkAnh");
                byte[] array = bundle.getByteArray("Image");
                final UploadTask uploadTask = mountainsRef.putBytes(array);
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
                                    listLinkAnh.add(downloadUri+"");
                                    updateEvent(listLinkAnh,key,ND);
                                    mData.child("Albums").child(user.getId()).child(Album).child("listHinh").push().setValue(downloadUri+"");
                                } else {

                                }
                            }
                        });
                    }
                });
            }


        }
        else {
            postStatus = new PostStatus(key, ND, 0, Access, mAuth.getCurrentUser().getUid(), null,false);
            mData.child("PostActivity").child(key).setValue(postStatus);
            mData.child("PostActivity").child(key).child("time").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void updateEvent(List<String> listLinkAnh, String key, String ND) {
        PostStatus postStatus = new PostStatus(key,ND,0,Access,mAuth.getCurrentUser().getUid(),listLinkAnh,false);
        mData.child("PostActivity").child(key).setValue(postStatus);
        mData.child("PostActivity").child(key).child("time").setValue(ServerValue.TIMESTAMP);
    }
}
