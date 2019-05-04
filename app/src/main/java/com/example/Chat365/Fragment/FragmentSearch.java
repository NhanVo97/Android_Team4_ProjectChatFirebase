package com.example.Chat365.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Chat365.Activity.User.ProfileActivity;
import com.example.Chat365.Adapter.FriendAutoAdapter;
import com.example.Chat365.Adapter.HistoryAdapter;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentSearch extends Fragment {
    View v;
    List<User> listUser = new ArrayList<>();
    List<User> listHistory = new ArrayList<>();
    TextView tvLichSu;
    AutoCompleteTextView autoND;
    DatabaseReference mData;
    FriendAutoAdapter friendAutoAdapter;
    ListView listView;
    FirebaseAuth mAuth;
    HistoryAdapter historyAdapter;

    public void getUser() {
        listUser.clear();
        mData.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getId().equals(mAuth.getUid())) {
                        listUser.add(user);
                        friendAutoAdapter = new FriendAutoAdapter(getActivity(), listUser);
                        autoND.setAdapter(friendAutoAdapter);
                    }
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

    public void getHistory() {
        mData.child("History").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    listHistory.add(user);
                    Collections.reverse(listHistory);
                    historyAdapter.notifyDataSetChanged();
                    tvLichSu.setVisibility(View.GONE);
                } else {
                    tvLichSu.setVisibility(View.VISIBLE);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.findfriends, container, false);
        autoND = v.findViewById(R.id.autocomplete);
        mData = FirebaseDatabase.getInstance().getReference();
        tvLichSu = v.findViewById(R.id.tvLichSu);
        listView = v.findViewById(R.id.listhistory);
        mAuth = FirebaseAuth.getInstance();
        getUser();
        getHistory();
        autoND.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                Bundle bundle = new Bundle();
                User user = listUser.get(i);
                bundle.putSerializable("User", user);
                intent.putExtra("UserBundle", bundle);
                startActivity(intent);
                mData.child("History").child(mAuth.getCurrentUser().getUid()).push().setValue(user);
            }
        });

        historyAdapter = new HistoryAdapter(getActivity(), listHistory);
        listView.setAdapter(historyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                Bundle bundle = new Bundle();
                User user = listHistory.get(i);
                bundle.putSerializable("User", user);
                intent.putExtra("UserBundle", bundle);
                startActivity(intent);
            }
        });
        return v;
    }
}
