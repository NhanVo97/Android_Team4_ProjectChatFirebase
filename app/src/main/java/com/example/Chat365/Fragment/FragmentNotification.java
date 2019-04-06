package com.example.Chat365.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Chat365.Adapter.ThongBaoAdapter;
import com.example.Chat365.Model.ThongBao;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FragmentNotification extends Fragment implements ThongBaoAdapter.Oncallback{
    View v;
    DatabaseReference mData;
    FirebaseAuth mAuth;
    ThongBaoAdapter thongBaoAdapter;
    RecyclerView rcV;
    List<ThongBao> thongBaoList = new ArrayList<>();
    public void getData()
    {
        mData.child("TB").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    ThongBao thongBao = dataSnapshot.getValue(ThongBao.class);
                    thongBaoList.add(thongBao);
                }
                thongBaoAdapter.notifyDataSetChanged();

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_notification,container,false);
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        rcV = v.findViewById(R.id.rcvListTb);
        thongBaoAdapter = new ThongBaoAdapter(this,thongBaoList);
        rcV.setHasFixedSize(true);
        rcV.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcV.setAdapter(thongBaoAdapter);
        getData();
        return v;
    }

    @Override
    public void onItemClick(int position) {

    }
}
