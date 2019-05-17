package com.example.Chat365.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Chat365.Adapter.UserAdapter.PostAdapter.NotificationAdapter;
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

public class FragmentNotification extends Fragment implements NotificationAdapter.Oncallback {
    View v;
    DatabaseReference mData;
    FirebaseAuth mAuth;
    NotificationAdapter notificationAdapter;
    RecyclerView rcV;
    List<ThongBao> thongBaoList = new ArrayList<>();
    TextView tvStatus;
    private void updateStatus(){
        if(thongBaoList.size() > 0){
            rcV.setVisibility(View.VISIBLE);
            tvStatus.setVisibility(View.GONE);
        } else {
            rcV.setVisibility(View.GONE);
            tvStatus.setVisibility(View.VISIBLE);
        }
    }
    public void getData() {
        mData.child("TB").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    ThongBao thongBao = dataSnapshot.getValue(ThongBao.class);
                    thongBaoList.add(thongBao);
                }
                notificationAdapter.notifyDataSetChanged();
                updateStatus();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    ThongBao thongBao = dataSnapshot.getValue(ThongBao.class);
                    for (int i = 0; i < thongBaoList.size(); i++) {
                        if (thongBaoList.get(i).getId().equals(thongBao.getId())) {
                            thongBaoList.set(i, thongBao);
                            break;
                        }
                    }
                    notificationAdapter.notifyDataSetChanged();
                    updateStatus();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ThongBao thongBao = dataSnapshot.getValue(ThongBao.class);
                thongBaoList.remove(thongBao);
                notificationAdapter.notifyDataSetChanged();
                updateStatus();
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
        v = inflater.inflate(R.layout.fragment_notification, container, false);
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        rcV = v.findViewById(R.id.rcvListTb);
        tvStatus = v.findViewById(R.id.tvStatus);
        notificationAdapter = new NotificationAdapter(this, thongBaoList);
        rcV.setHasFixedSize(true);
        rcV.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcV.setAdapter(notificationAdapter);
        updateStatus();
        getData();
        return v;
    }

    @Override
    public void onItemClick(int position) {

    }
}
