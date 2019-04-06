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

import com.example.Chat365.Activity.ProfileActivity;
import com.example.Chat365.Adapter.FriendAutoAdapter;
import com.example.Chat365.Adapter.FriendsListAdapter;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.User;
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

public class FragmentListFriends extends Fragment {
    View v;
    FriendsListAdapter friendsListAdapter;
    List<User> listFriends= new ArrayList<>();
    ListView listView;
    DatabaseReference mData;
    FirebaseAuth mAuth;
    AutoCompleteTextView autoND;
    FriendAutoAdapter friendAutoAdapter;
    private void getListFriends()
    {
        listFriends.clear();
        mData.child("Friends").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                try {
                    mData.child("Users").child(friends.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            listFriends.add(user);
                            friendsListAdapter.notifyDataSetChanged();
                            friendAutoAdapter = new FriendAutoAdapter(getActivity(),listFriends);
                            autoND.setAdapter(friendAutoAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (NullPointerException e) {
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                try {
                    mData.child("Users").child(friends.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String keys = dataSnapshot.getKey();
                            for(User f: listFriends)
                            {
                                if(f.getId().equals(keys))
                                {
                                    listFriends.remove(f);
                                    friendsListAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } catch (NullPointerException e) {
                }
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
        v=inflater.inflate(R.layout.listfriends,container,false);
        listView = v.findViewById(R.id.lvFriendsPrv);
        friendsListAdapter = new FriendsListAdapter(getActivity(),listFriends);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        listView.setAdapter(friendsListAdapter);
        autoND = v.findViewById(R.id.autoSearchFriends);
        getListFriends();
        autoND.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),ProfileActivity.class);
                Bundle bundle = new Bundle();
                User user = listFriends.get(i);
                bundle.putSerializable("User",user);
                intent.putExtra("UserBundle",bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),ProfileActivity.class);
                Bundle bundle = new Bundle();
                User user = listFriends.get(i);
                bundle.putSerializable("User",user);
                intent.putExtra("UserBundle",bundle);
                startActivity(intent);
            }
        });
        return v;
    }
}
