package com.example.Chat365.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Chat365.Adapter.ListAlbumAdapter;
import com.example.Chat365.Model.Album;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentAlbums extends Fragment{
    View v;
    ListView lv;
    TextView tv;
    ListAlbumAdapter listAlbumAdapter;
    List<Album> listAlbum = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference mData;
    private void GetData() {
        mData.child("Albums").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv.setText("Danh Sách Album của bạn ("+dataSnapshot.getChildrenCount()+"/20)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("Albums").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    LoadData(dataSnapshot.getKey());
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

        mData.child("Albums").child(mAuth.getCurrentUser().getUid()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
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
                listAlbumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.albumfragment, container, false);
        lv = v.findViewById(R.id.listviewab);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        tv = v.findViewById(R.id.tvDem);
        listAlbumAdapter = new ListAlbumAdapter(listAlbum,getActivity());

        lv.setAdapter(listAlbumAdapter);
        GetData();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<String> list = new ArrayList<>();
                for (Map.Entry<String, String> entry : listAlbum.get(i).getListHinh().entrySet()) {
                    String value = entry.getValue();
                    list.add(value);
                }
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentGalery fragmentGalery = new FragmentGalery();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("ListHinh", (ArrayList<String>) list);
                fragmentGalery.setArguments(bundle);
                fragmentTransaction.replace(R.id.layoutalbums,fragmentGalery).addToBackStack("tag").commit();
            }
        });
        return v;
    }
}
