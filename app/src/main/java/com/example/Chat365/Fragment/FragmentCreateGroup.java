package com.example.Chat365.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.Adapter.FriendListAdapter;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class FragmentCreateGroup extends Fragment implements FriendListAdapter.OnCallBack, View.OnClickListener {
    View v;
    RecyclerView rcListFriend,lvSelected;
    EditText edSearch,edNameGroup;
    ImageView imUpHinh;
    List<Friends> listFriends,listFriendsSelected;
    DatabaseReference mData;
    FriendListAdapter friendListAdapter,friendListSelectAdapter;
    FirebaseUser mCurrent;
    TextView tvTotal;
    Button btnCreate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.groupprivate,container,false);
        // Anh Xa
        rcListFriend = v.findViewById(R.id.rcListFriend);
        lvSelected = v.findViewById(R.id.lvSelected);
        edSearch = v.findViewById(R.id.edSearch);
        edNameGroup = v.findViewById(R.id.edNameGroup);
        imUpHinh = v.findViewById(R.id.btnUpHinh);
        tvTotal = v.findViewById(R.id.totalSelected);
        btnCreate = v.findViewById(R.id.btnCreate);
        listFriends = new ArrayList<>();
        listFriendsSelected = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference();
        mCurrent = FirebaseAuth.getInstance().getCurrentUser();

        // init friend list select panel right
        friendListSelectAdapter = new FriendListAdapter(listFriendsSelected,this,true,"");
        lvSelected.setLayoutManager(new LinearLayoutManager(getContext()));
        lvSelected.setHasFixedSize(true);
        lvSelected.setAdapter(friendListSelectAdapter);
        // init Data
        initData("");
        // Event
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filterLongEnough()) {
                    initData(edSearch.getText().toString());
                } else {
                    initData("");
                }
            }

            private boolean filterLongEnough() {
                return edSearch.getText().toString().trim().length() > 1;
            }
        };
        edSearch.addTextChangedListener(fieldValidatorTextWatcher);
        btnCreate.setOnClickListener(this);
        return v;
    }

    private void initData(String searchKey) {
        listFriends.clear();
        // Init friend list adapter panel left
        String keyWord = edSearch.getText().toString();
        searchKey = (!keyWord.isEmpty() && keyWord != null) ? keyWord : "";
        friendListAdapter = new FriendListAdapter(listFriends,this,false,searchKey);
        rcListFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        rcListFriend.setHasFixedSize(true);
        rcListFriend.setAdapter(friendListAdapter);

        mData.child("Friends").child(mCurrent.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                if(friends!=null){
                    listFriends.add(friends);
                }
                Log.e("AAA",listFriends.size()+"AAA");
                friendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                if(friends!=null){
                   for(int i = 0 ;i<listFriends.size();i++){
                       if(listFriends.get(i).getId().equals(friends.getId())){
                           listFriends.set(i,friends);
                       }
                   }
                    friendListAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                if(friends!=null){
                   listFriends.remove(friends);
                   friendListAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemSelect(int postion,boolean isPanelRight) {
        Friends friend = listFriends.get(postion);
        listFriends.get(postion).setCheck(!friend.isCheck());
        // handle panel left
        if(!isPanelRight){
            if(friend.isCheck()){
                listFriendsSelected.add(friend);
            } else {
                listFriendsSelected.remove(friend);
            }
        } else {
            // handle button close panel right
            listFriendsSelected.remove(friend);
        }
        // Change Everything
        tvTotal.setText(String.valueOf(listFriendsSelected.size()));
        friendListAdapter.notifyDataSetChanged();
        friendListSelectAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnCreate:
                String nameGroup = edNameGroup.getText().toString();
                if(!nameGroup.isEmpty()){
                    if(listFriendsSelected.size()>0){
                        List<String> listStr = new ArrayList<>();
                        for(Friends f : listFriendsSelected){
                            listStr.add(f.getId());
                        }
                        String key = mData.child("GroupChat").push().getKey();
                        GroupFriends groupFriends = new GroupFriends(key,nameGroup,listStr,"");
                        mData.child("GroupChat").child(mCurrent.getUid()).child(key).setValue(groupFriends);
                        Toast.makeText(getContext(), "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Bạn chưa chọn bạn vào nhóm", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Bạn chưa nhập tên nhóm", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

