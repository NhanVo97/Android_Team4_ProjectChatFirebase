package com.example.Chat365.Activity.Galery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.Activity.Post.PostActivity;
import com.example.Chat365.Activity.User.QuyenActivity;
import com.example.Chat365.Adapter.UserAdapter.AlbumAdapter.ListAlbumAdapter;
import com.example.Chat365.Model.Album;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView btnCreate,tvDemAlbum;
    private EditText nameAlbum, edCaption;
    private Button btnQuyen;
    private DatabaseReference mData;
    private Switch swShowListFriend, swShowListAlbum;
    private ListAlbumAdapter listAlbumAdapter;
    private ListView lvAlbum;
    private Session session;
    private User user;
    private android.support.v7.widget.Toolbar tbAlbum;
    private List<Album> listAlbum;
    private List<String> listFriends;
    String name = "album";
    private void initData(){
        // Action bar
        tbAlbum = findViewById(R.id.tbcreatealbum);
        setSupportActionBar(tbAlbum);
        getSupportActionBar().setTitle("Tạo album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Anh Xa view
        btnCreate = findViewById(R.id.btnCreateAlbums);
        nameAlbum = findViewById(R.id.edNameAlbum);
        edCaption = findViewById(R.id.edCapAlbum);
        btnQuyen =findViewById(R.id.btnQuyen);
        swShowListFriend = findViewById(R.id.swfriends);
        swShowListAlbum =findViewById(R.id.swalbum);
        lvAlbum = findViewById(R.id.listviewalbums);
        tvDemAlbum = findViewById(R.id.tvDemAlbums);
        // init list
        listAlbum = new ArrayList<>();
        listFriends = new ArrayList<>();
        // init Album adapter
        listAlbumAdapter = new ListAlbumAdapter(listAlbum,this);
        lvAlbum.setAdapter(listAlbumAdapter);
        //Session
        session = new Session(this);
        user = session.getUser();
        // Firebase
        mData = FirebaseDatabase.getInstance().getReference();
        // Get Data
        GetData(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == Constant.REQUEST_CODE_PERMISSION){
            int position = data.getIntExtra("positionPermission",0);
            switch (position){
                case 0:
                    btnQuyen.setText("Công Khai");
                    btnQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.congkhai,0,0,0);
                    break;
                case 1:
                    btnQuyen.setText("Bạn Bè");
                    btnQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.withmyfriends,0,0,0);
                    break;
                case 2:
                    btnQuyen.setText("Chỉ Mình Tôi");
                    btnQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.riengtu,0,0,0);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        initData();
        // Event
        btnQuyen.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        swShowListAlbum.setOnCheckedChangeListener(this);
        lvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = listAlbum.get(position);
                name = album.getName();
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("nameAlbum",name);
                setResult(Constant.REQUEST_CODE_ALBUM,intent);
                finish();
            }
        });
        tbAlbum.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("nameAlbum",name);
                setResult(Constant.REQUEST_CODE_ALBUM,intent);
                finish();
            }
        });

    }

    private void GetData(User user) {
        // count total ablum
        mData.child("Albums").child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               tvDemAlbum.setText("("+dataSnapshot.getChildrenCount()+"/20)");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Get list album
        mData.child("Albums").child(user.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Album album = dataSnapshot.getValue(Album.class);
                if(album!=null) {
                   listAlbum.add(album);
                }
                else {
                    tvDemAlbum.setText("(0/20)");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Album album = dataSnapshot.getValue(Album.class);
                for(int i =0 ;i<listAlbum.size();i++){
                    if(listAlbum.get(i).getKey().equals(album.getKey())){
                        listAlbum.set(i,album);
                    }
                }
                listAlbumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Album album = dataSnapshot.getValue(Album.class);
                listAlbum.remove(album);
                listAlbumAdapter.notifyDataSetChanged();
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
            case R.id.btnQuyen:
                Intent pagePermission = new Intent(getApplicationContext(), QuyenActivity.class);
                pagePermission.putExtra("Quyen","Công Khai");
                startActivityForResult(pagePermission,Constant.REQUEST_CODE_PERMISSION);
                break;
            case R.id.btnCreateAlbums:
                String name = nameAlbum.getText().toString();
                if(name.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Tên Album không được để trống",Toast.LENGTH_SHORT).show();
                }
                else {
                    String key = mData.child("Albums").push().getKey();
                    String caption = edCaption.getText().toString();
                    String quyenAlbum = btnQuyen.getText().toString();
                    Album album = new Album(key,name,caption,quyenAlbum,null,null);
                    mData.child("Albums").child(user.getId()).child(key).setValue(album);
                    Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                    intent.putExtra("nameAlbum",album.getName());
                    setResult(Constant.REQUEST_CODE_ALBUM,intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id){
            case R.id.swalbum:
                if(isChecked) {
                    listAlbum.clear();
                    lvAlbum.setVisibility(View.VISIBLE);
                    GetData(user);
                }
                else {
                    lvAlbum.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.swfriends:
                break;
        }
    }
}
