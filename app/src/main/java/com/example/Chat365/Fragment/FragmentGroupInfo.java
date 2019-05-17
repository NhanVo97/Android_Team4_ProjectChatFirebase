package com.example.Chat365.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Chat365.Activity.Library.FriendPicker;
import com.example.Chat365.Activity.User.FriendNearActivity;
import com.example.Chat365.Activity.User.ProfileActivity;
import com.example.Chat365.Adapter.UserAdapter.GroupChatAdapter.UserStatusAdapter;
import com.example.Chat365.Model.Friends;
import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentGroupInfo extends Fragment  implements View.OnClickListener, UserStatusAdapter.onCallBack {
    CircleImageView cvAvt;
    TextView tvNameGroup,tvStatusActive,tvTotalMember,btnBietDanh,btnViTriThanhVien;
    LinearLayout lnCallAudio,lnFaceTime,lnNotification,lnPersonal,lnAddMember;
    GroupFriends friends;
    DatabaseReference mData;
    int totalActive = 0;
    RecyclerView rcListFriends;
    UserStatusAdapter userStatusAdapter;
    RelativeLayout rlColorSelect,rlEmotion;
    View v;
    List<User> listUser;
    FragmentManager fragmentManager;
    FirebaseAuth firebaseAuth;
    List<String> listFriend;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_infogroup,container,false);
        // Anh Xa
        tvNameGroup = v.findViewById(R.id.tvName);
        tvStatusActive = v.findViewById(R.id.tvStatus);
        cvAvt = v.findViewById(R.id.cAvt);
        lnCallAudio = v.findViewById(R.id.layoutAudio);
        lnFaceTime = v.findViewById(R.id.layoutVideo);
        lnNotification = v.findViewById(R.id.layoutNotification);
        lnPersonal = v.findViewById(R.id.layoutPersonal);
        lnAddMember = v.findViewById(R.id.layoutAdd);
        tvTotalMember = v.findViewById(R.id.tvTotalMember);
        rcListFriends = v.findViewById(R.id.rcListFriend);
//        rlColorSelect = v.findViewById(R.id.rlColorSelect);
//        rlEmotion = v.findViewById(R.id.rlEmotion);
        //btnBietDanh = v.findViewById(R.id.btnBietDanh);
        btnViTriThanhVien = v.findViewById(R.id.btnViTriThanhVien);
        mData = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getChildFragmentManager();
        Bundle bundle = getArguments();
        friends = (GroupFriends) bundle.getSerializable("FriendsGroup");
        listUser = new ArrayList<>();
        listFriend = friends.getListFriend();
        userStatusAdapter = new UserStatusAdapter(listFriend,mData,this);
        rcListFriends.setLayoutManager(new GridLayoutManager(getContext(),3));
        rcListFriends.setHasFixedSize(true);
        rcListFriends.setAdapter(userStatusAdapter);
        // InitData
        InitData();
        // Event
        btnViTriThanhVien.setOnClickListener(this);
        lnAddMember.setOnClickListener(this);
        return v;
    }

    private void InitData() {
        if(!friends.getLinkAvatar().isEmpty()){
            Picasso.get().load(friends.getLinkAvatar()).into(cvAvt);
        }
        tvNameGroup.setText(friends.getNameGroup());
        // status active
        for(String id : friends.getListFriend()){
            checkOnlineMember(id);
        }
         if(totalActive > 0){
             tvStatusActive.setText("Có "+totalActive + " người hoạt động");
         } else {
             tvStatusActive.setText("Không có ai hoạt động");
         }
        // total member
        tvTotalMember.setText(String.valueOf(friends.getListFriend().size()));

    }

    private void checkOnlineMember(String id) {
        mData.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getIsOnline().equals("True")){
                    totalActive ++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.REQUEST_CODE_SUCESS){
            Bundle bundle = data.getBundleExtra("Bundle");
            List<Friends> listFriends = (List<Friends>) bundle.getSerializable("ListSelectFriends");
            if(listFriends !=null){
                for(Friends friends : listFriends){
                    listFriend.add(friends.getId());
                }
                mData.child("GroupChat").child(firebaseAuth.getUid()).child(friends.getKey()).
                        child("listFriend").setValue(listFriend);
                userStatusAdapter.notifyDataSetChanged();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnViTriThanhVien:
                Intent intent = new Intent(getContext(), FriendNearActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("FriendsGroup",friends);
                intent.putExtra("Bundle",bundle);
                startActivity(intent);
                break;
            case R.id.layoutAdd:
                Intent startPickFriend = new Intent(getContext(), FriendPicker.class);
                Bundle bundleFriend = new Bundle();
                bundleFriend.putSerializable("FriendsGroup",friends);
                startPickFriend.putExtra("Bundle",bundleFriend);
                startActivityForResult(startPickFriend, Constant.REQUEST_CODE_SUCESS);
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.e("AAA",listFriend.get(position));
        mData.child("Users").child(listFriend.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                Bundle bundle = new Bundle();
                User user = dataSnapshot.getValue(User.class);
                if(user!=null){
                    bundle.putSerializable("User",user);
                    intent.putExtra("UserBundle",bundle);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
