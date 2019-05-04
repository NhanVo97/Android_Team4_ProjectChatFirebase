package com.example.Chat365.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Activity.User.MessagerActivity;
import com.example.Chat365.Adapter.ListMessagerAdapter;
import com.example.Chat365.Model.Message;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment implements ListMessagerAdapter.Oncallback {

    View v;
    DatabaseReference mData;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    ListMessagerAdapter messagerAdapter;
    User user;
    List<Message> list;

    public void getUser() {
        Session session = new Session(mData,mAuth.getCurrentUser(),getActivity(), false);
        user = session.getUser();
        if (user == null) {
            backToHome();
        }
    }

    private void backToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        getUser();
        LoadData();
    }

    private void LoadDataKey(String x) {
        list.clear();
        Query lastQuery = mData.child("PrivateChat").child(mAuth.getCurrentUser().getUid()).child(x).orderByKey().limitToLast(1);
        lastQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    list.add(message);
                }
                messagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                for(int i =0 ;i<list.size();i++){
                    if(list.get(i).getKey().equals(message.getKey())){
                        list.set(i,message);
                        break;
                    }
                }
                messagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                list.remove(message);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadData() {

        mData.child("PrivateChat").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    LoadDataKey(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        // Anh Xa
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = v.findViewById(R.id.lvSMS);
        list = new ArrayList<>();
        messagerAdapter = new ListMessagerAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(messagerAdapter);
        return v;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), MessagerActivity.class);
        Bundle bundle = new Bundle();
        User u = list.get(position).getUser();
        bundle.putSerializable("User", u);
        intent.putExtra("PrivateChat", bundle);
        startActivity(intent);
    }
}
