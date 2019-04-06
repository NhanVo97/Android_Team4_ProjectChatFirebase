package com.example.Chat365.Activity;

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

import com.example.Chat365.Adapter.ListAlbumAdapter;
import com.example.Chat365.Model.Album;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
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

public class CreateAlbum extends AppCompatActivity {
    User user;
    TextView btnCreate,tvDemAlbum;
    EditText nameAlbum,caption;
    Button btnQuyen;
    DatabaseReference mData;
    Switch swfriends,swshowlistalbums;
    List<Album> listAlbum;
    ListAlbumAdapter listAlbumAdapter;
    ListView lv;
    //List<Quyen>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        // ANh Xa
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.tbcreatealbum);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tạo album");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCreate = findViewById(R.id.btnCreateAlbums);
        nameAlbum = findViewById(R.id.edNameAlbum);
        caption = findViewById(R.id.edCapAlbum);
        btnQuyen =findViewById(R.id.btnQuyen);
        swfriends = findViewById(R.id.swfriends);
        swshowlistalbums=findViewById(R.id.swalbum);
        listAlbum = new ArrayList<>();
        lv = findViewById(R.id.listviewalbums);
        tvDemAlbum = findViewById(R.id.tvDemAlbums);
        // Get Data
        Intent intent = getIntent();
        final Bundle b1 = intent.getBundleExtra("BundleUser");
        final Bundle b2 = intent.getBundleExtra("BundleLinkAnh");
        final Bundle bundle;
        if(b1==null)
        {
            bundle=b2;
        }
        else
        {
            bundle=b1;
        }
        user = (User) bundle.getSerializable("User");
        final String ND = bundle.getString("ND");
        final String Quyen = bundle.getString("Quyen");
        if(Quyen.equals("0"))
        {
            btnQuyen.setText("Công Khai");
            btnQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.congkhai,0,0,0);
        }
        else if(Quyen.equals("1"))
        {
            btnQuyen.setText("Bạn Bè");
            btnQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.withmyfriends,0,0,0);
        }
        else if(Quyen.equals("2"))
        {
            btnQuyen.setText("Chỉ Mình Tôi");
            btnQuyen.setCompoundDrawablesWithIntrinsicBounds(R.drawable.riengtu,0,0,0);
        }

        mData = FirebaseDatabase.getInstance().getReference();
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameAlbum.getText().toString();
                if(name.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Tên Album không được để trống",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String cap = caption.getText().toString();
                    String quyenalbum = btnQuyen.getText().toString();
                    Map<String,String> listFriends= new HashMap<>();
                    Album album = new Album(name,cap,quyenalbum,listFriends,null);
                    mData.child("Albums").child(user.getId()).child(name).setValue(album);
                    Intent intent = new Intent(getApplicationContext(),Post.class);
                    Bundle b = new Bundle();
                    b.putString("NameAlbums",name);
                    int idQuyen=0;
                    if(quyenalbum.equals("Bạn Bè")){
                        idQuyen=1;
                    }
                    else if(quyenalbum.equals("Chỉ Mình Tôi"))
                    {
                        idQuyen=2;
                    }

                    b.putString("Quyen",idQuyen+"");
                    b.putSerializable("User",user);
                    intent.putExtra("BundleUser",b);
                    startActivity(intent);
                }


            }
        });
        btnQuyen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callCreateAccess = new Intent(getApplicationContext(),QuyenActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("User",user);
                bundle1.putString("ND",ND);
                bundle1.putString("Quyen",Quyen);
                bundle1.putString("Check","True");
                callCreateAccess.putExtra("BundleUser",bundle1);
                startActivity(callCreateAccess);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameAlbum.getText().toString();
                Intent intent = new Intent(getApplicationContext(),Post.class);
                Bundle b = new Bundle();
                if(name.equals(""))
                {
                    name="MyAlbum";
                }
                b.putString("NameAlbums",name);
                b.putString("Quyen",Quyen);
                b.putSerializable("User",user);
                intent.putExtra("BundleUser",b);
                startActivity(intent);
            }
        });
        GetData(user);
        swshowlistalbums.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    listAlbum.clear();
                    lv.setVisibility(View.VISIBLE);
                    GetData(user);
                }
                else
                {
                    lv.setVisibility(View.INVISIBLE);
                }
            }
        });
        listAlbumAdapter = new ListAlbumAdapter(listAlbum,this);
        lv.setAdapter(listAlbumAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = listAlbum.get(position);
                String name =album.getName();
                Intent intent = new Intent(getApplicationContext(),Post.class);
                Bundle b = new Bundle();
                b.putString("NameAlbums",name);
                b.putString("Quyen",Quyen);
                b.putSerializable("User",user);
                intent.putExtra("BundleUser",b);
                startActivity(intent);
            }
        });

    }

    private void GetData(User user) {
        mData.child("Albums").child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               tvDemAlbum.setText("("+dataSnapshot.getChildrenCount()+"/20)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("Albums").child(user.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    LoadData(dataSnapshot.getKey());
                }
                else
                {
                    tvDemAlbum.setText("(0/20)");
                }

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

    private void LoadData(final String key) {

        mData.child("Albums").child(user.getId()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name="";
                String caption="";
                String quyen="";
                if(key.equals("MyAlbum"))
                {
                    name="MyAlbum";
                    caption="Default";
                    quyen="Công Khai";
                }
                else
                {
                    name = dataSnapshot.child("name").getValue().toString();
                    caption = dataSnapshot.child("caption").getValue().toString();
                    quyen = dataSnapshot.child("quyen").getValue().toString();
                }

                    Map<String,String> listFriends = (Map<String, String>) dataSnapshot.child("listFriends").getValue();
                    Map<String,String> listHinh = (Map<String, String>) dataSnapshot.child("listHinh").getValue();
                    Album album = new Album(name,caption,quyen,listFriends,listHinh);
                    listAlbum.add(album);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
