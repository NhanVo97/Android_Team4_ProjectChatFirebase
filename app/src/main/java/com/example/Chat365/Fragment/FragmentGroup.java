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
import com.example.Chat365.Activity.MessagerActivity;
import com.example.Chat365.Activity.OnlineActivity;
import com.example.Chat365.Activity.RoomChat;
import com.example.Chat365.Activity.StatusDialog;
import com.example.Chat365.Adapter.FriendsAdapter;
import com.example.Chat365.Adapter.ListRoomAdapter;
import com.example.Chat365.Adapter.RequestAdapter;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentGroup extends Fragment implements StatusDialog.statusListenner {
    View v;
    private TabHost tabHost;
    private Button btnListOnline;
    public static List<User> listOnline;
    private ListView lv, lv2, lv3;
    public static List<Room> listRoom;
    private TextView tvStt;
    private EditText edStt;
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static List<User> listrequestFriend, listFriends;
    RequestAdapter requestAdapter;
    FriendsAdapter friendsAdapter;
    User user;
    public static void addList(User u) {
        listFriends.add(u);
    }

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

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            listOnline.clear();
            getUser();
            getRequest();
            getListFriends();
            checkOnline();
        } else {
            sendToHome();
        }

    }

    private void checkOnline() {
        mData.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User uAll = dataSnapshot.getValue(User.class);
                if (uAll.getIsOnline().equals("true")) {
                    User u = new User(uAll.getName(), uAll.getPassword(), uAll.getEmail(), uAll.getSex(), uAll.getBirthday(), uAll.getLevel(), uAll.getHistory(), uAll.getProvince(), uAll.getAvatar(), uAll.getHomeTown(), uAll.getWork(), uAll.getStudy(), uAll.getRelationship(), uAll.getId(), uAll.getIsOnline(), uAll.getStatus(), uAll.getTimestamp());
                    listOnline.add(u);
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

    private void getRequest() {
        listrequestFriend.clear();
        mData.child("Friends_Req").child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                RequestType requestType = dataSnapshot.getValue(RequestType.class);
                if (requestType.getRequest_type().equals("Received")) {
                    try {
                        mData.child("Users").child(requestType.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                listrequestFriend.add(user);
                                requestAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } catch (NullPointerException e) {

                    }
                    requestAdapter.notifyDataSetChanged();
                } else {
                    tvStt.setText("Chưa có lời mời kết bạn nào!");
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                RequestType requestType = dataSnapshot.getValue(RequestType.class);
                try {
                    mData.child("Users").child(requestType.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            listrequestFriend.remove(user);
                            requestAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } catch (NullPointerException e) {
                }
                requestAdapter.notifyDataSetChanged();
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
                try {
                    mData.child("Users").child(friends.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            addList(user);
                            friendsAdapter.notifyDataSetChanged();
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
                            for (User f : listFriends) {
                                if (f.getId().equals(keys)) {
                                    listFriends.remove(f);
                                    friendsAdapter.notifyDataSetChanged();
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
        v = inflater.inflate(R.layout.fragment_group, container, false);
        // anh xa thuoc tinh
        tabHost = v.findViewById(R.id.myTabhost);
        btnListOnline = v.findViewById(R.id.btnOnline);
        lv = v.findViewById(R.id.listRoomOnline);
        lv2 = v.findViewById(R.id.listRqFriend);
        lv3 = v.findViewById(R.id.lvFriends);
        tvStt = v.findViewById(R.id.tvStt);
        edStt = v.findViewById(R.id.editStatus);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Database
        mData = FirebaseDatabase.getInstance().getReference();
        mData.keepSynced(true);
        // List
        listrequestFriend = new ArrayList<>();
        listFriends = new ArrayList<>();
        listOnline = new ArrayList<>();
        // tab and event
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Bạn bè");
        tab1.setIndicator("Bạn bè");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Phòng chat");
        tab2.setIndicator("Phòng Chat");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);

        btnListOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OnlineActivity.class);
                startActivity(intent);
            }
        });
        requestAdapter = new RequestAdapter(getContext(), listrequestFriend);
        lv2.setAdapter(requestAdapter);
        friendsAdapter = new FriendsAdapter(getContext(), listFriends);
        lv3.setAdapter(friendsAdapter);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("Phòng chat")) {
                    btnListOnline.setText("TỔNG ONLINE " + (listOnline.size()));
                    ListRoomAdapter listRoomAdapter = new ListRoomAdapter(getContext(), listRoom);
                    lv.setAdapter(listRoomAdapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), RoomChat.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("IDRoom", position);
                            bundle.putSerializable("User", user);
                            intent.putExtra("RoomChat", bundle);
                            startActivity(intent);
                        }
                    });
                } else {
                }
            }
        });
        edStt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        // List Rom
        lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MessagerActivity.class);
                Bundle bundle = new Bundle();
                User u = listFriends.get(position);
                bundle.putSerializable("User", u);
                intent.putExtra("PrivateChat", bundle);
                startActivity(intent);
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
    public void applytexts(String stt) {
        Map<String, Object> keyUp = new HashMap<String, Object>();
        keyUp.put("status", stt);
        mData.child("Users").child(currentUser.getUid()).updateChildren(keyUp);
        edStt.setText(stt);
    }
}
