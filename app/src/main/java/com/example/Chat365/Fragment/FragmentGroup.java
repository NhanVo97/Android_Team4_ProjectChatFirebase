package com.example.Chat365.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Activity.User.MessagerActivity;
import com.example.Chat365.Activity.User.OnlineActivity;
import com.example.Chat365.Activity.Chat.RoomChatActivity;
import com.example.Chat365.Fragment.DialogCustom.StatusDialog;
import com.example.Chat365.Adapter.UserAdapter.FriendAdapter.FriendsOnlineAdapter;
import com.example.Chat365.Adapter.RoomAdapter.RoomPublicAdapter;
import com.example.Chat365.Adapter.UserAdapter.FriendAdapter.FriendRequestAdapter;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.RequestType;
import com.example.Chat365.Model.Room;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentGroup extends Fragment implements StatusDialog.statusListenner, View.OnClickListener {
    View v;
    private TabHost tabHost;
    private Button btnListOnline;
    private ListView lvChatRoom, lvFriendRequest, lvFriendOnline;
    public  List<Room> listRoom;
    private TextView tvStt;
    private EditText edStatus;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<RequestType> listRequestFriend;
    private List<Friends> listFriends;
    private FriendRequestAdapter friendRequestAdapter;
    private FriendsOnlineAdapter friendsOnlineAdapter;
    private TabHost.TabSpec tab1,tab2;
    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {

        } else {
            sendToHome();
        }

    }
    User user;
    public FragmentGroup() {
        listRoom = new ArrayList<>();
        listRoom.add(new Room(R.drawable.chat365, "Làm quen kết bạn"));
        listRoom.add(new Room(R.drawable.chat365, "Hoa học trò"));
        listRoom.add(new Room(R.drawable.chat365, "Chat tiếng anh"));
        listRoom.add(new Room(R.drawable.chat365, "Chat tiếng nhật"));
        listRoom.add(new Room(R.drawable.chat365, "Phòng giải trí"));
        listRoom.add(new Room(R.drawable.chat365, "CLB âm nhạc"));
        listRoom.add(new Room(R.drawable.chat365, "Yêu bóng đá"));
        listRoom.add(new Room(R.drawable.chat365, "Điểm hẹn game thủ"));
        listRoom.add(new Room(R.drawable.chat365, "Hỏi đáp"));
    }
    public void getUser() {
        Session session = new Session(mData,mAuth.getCurrentUser(),getActivity(),false);
        user = session.getUser();
        if (user == null) {
            sendToHome();
        }
    }
    private void sendToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void getRequest() {
        listRequestFriend.clear();
        mData.child("Friends_Req").child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RequestType requestType = dataSnapshot.getValue(RequestType.class);
                if (requestType.getRequest_type().equals("Received")) {
                    listRequestFriend.add(requestType);
                    friendRequestAdapter.notifyDataSetChanged();
                } else {
                    tvStt.setText("Chưa có lời mời kết bạn nào!");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RequestType requestType = dataSnapshot.getValue(RequestType.class);
                if(requestType!=null){
                    if (requestType.getRequest_type().equals("Received")) {
                        for(int i = 0;i<listRequestFriend.size();i++){
                            if(listRequestFriend.get(i).getId().equals(requestType.getId())){
                                listRequestFriend.set(i,requestType);
                                break;
                            }
                        }
                        friendRequestAdapter.notifyDataSetChanged();
                    } else {
                        tvStt.setText("Chưa có lời mời kết bạn nào!");
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                RequestType requestType = dataSnapshot.getValue(RequestType.class);
                if (requestType.getRequest_type().equals("Received")) {
                    listRequestFriend.remove(requestType);
                }
                friendRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getListFriends() {
        listFriends.clear();
        mData.child("Friends").child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                    listFriends.add(friends);
                    friendsOnlineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Friends friends = dataSnapshot.getValue(Friends.class);
                listFriends.remove(friends);
                friendsOnlineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void AnhXa(){
        // other view
        tabHost = v.findViewById(R.id.myTabhost);
        btnListOnline = v.findViewById(R.id.btnOnline);
        lvChatRoom = v.findViewById(R.id.listRoomOnline);
        lvFriendRequest = v.findViewById(R.id.listRqFriend);
        lvFriendOnline = v.findViewById(R.id.lvFriends);
        tvStt = v.findViewById(R.id.tvStt);
        edStatus = v.findViewById(R.id.editStatus);
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mData = FirebaseDatabase.getInstance().getReference();
        // List
        listRequestFriend = new ArrayList<>();
        listFriends = new ArrayList<>();
    }
    private void initData(){
        // tab layout
        tabHost.setup();
        tab1 = tabHost.newTabSpec("Bạn bè");
        tab1.setIndicator("Bạn bè");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);
        tab2 = tabHost.newTabSpec("Phòng chat");
        tab2.setIndicator("Phòng Chat");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);
        // Adapter
        friendRequestAdapter = new FriendRequestAdapter(getContext(), listRequestFriend);
        lvFriendRequest.setAdapter(friendRequestAdapter);
        friendsOnlineAdapter = new FriendsOnlineAdapter(getContext(), listFriends);
        lvFriendOnline.setAdapter(friendsOnlineAdapter);
        // get Data
        getUser();
        getRequest();
        getListFriends();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_group, container, false);
        AnhXa();
        initData();
        // Event
        edStatus.setOnClickListener(this);
        btnListOnline.setOnClickListener(this);
        // Tab layout
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("Phòng chat")) {
                    btnListOnline.setText("DANH SÁCH ONLINE");
                    RoomPublicAdapter roomPublicAdapter = new RoomPublicAdapter(getContext(), listRoom);
                    lvChatRoom.setAdapter(roomPublicAdapter);
                    lvChatRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), RoomChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("Room",  listRoom.get(position));
                            bundle.putInt("IDRoom",position);
                            intent.putExtra("RoomBundle", bundle);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
        // List Rom
        lvFriendOnline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friends friends = listFriends.get(position);
                mData.child("Users").child(friends.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Intent intent = new Intent(getActivity(), MessagerActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("User", user);
                        intent.putExtra("PrivateChat", bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        return v;
    }

    private void openDialog() {
        StatusDialog statusDialog = new StatusDialog();
        statusDialog.setTargetFragment(FragmentGroup.this, 1);
        statusDialog.show(getFragmentManager(), "MyCustom");
    }

    @Override
    public void applyTextStatus(String stt) {
        Map<String, Object> keyUp = new HashMap<String, Object>();
        keyUp.put("status", stt);
        mData.child("Users").child(currentUser.getUid()).updateChildren(keyUp);
        edStatus.setText(stt);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.editStatus:
                openDialog();
                break;
            case R.id.btnOnline:
                Intent intent = new Intent(getActivity(), OnlineActivity.class);
                startActivity(intent);
                break;
        }
    }
}
