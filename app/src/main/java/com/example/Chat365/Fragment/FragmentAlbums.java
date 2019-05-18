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

import com.example.Chat365.Adapter.UserAdapter.AlbumAdapter.ListAlbumAdapter;
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

public class FragmentAlbums extends Fragment{
    View v;
    ListView lvAlbum;
    TextView tvTotal;
    ListAlbumAdapter listAlbumAdapter;
    List<Album> listAlbum = new ArrayList<>();
    FirebaseAuth mAuth;
    DatabaseReference mData;
    private void GetData() {
        // count total ablum
        mData.child("Albums").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tvTotal.setText("("+dataSnapshot.getChildrenCount()+"/20)");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Get list album
        mData.child("Albums").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Album album = dataSnapshot.getValue(Album.class);
                listAlbum.add(album);
                listAlbumAdapter.notifyDataSetChanged();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.albumfragment, container, false);
        lvAlbum = v.findViewById(R.id.listviewab);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        tvTotal = v.findViewById(R.id.tvDem);
        listAlbumAdapter = new ListAlbumAdapter(listAlbum,getActivity());
        lvAlbum.setAdapter(listAlbumAdapter);
        GetData();
        lvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Album album = listAlbum.get(i);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentGalery fragmentGalery = new FragmentGalery();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("ListHinh", (ArrayList<String>) album.getListHinh());
                fragmentGalery.setArguments(bundle);
                fragmentTransaction.replace(R.id.layoutalbums,fragmentGalery).addToBackStack("tag").commit();
            }
        });
        return v;
    }
}
