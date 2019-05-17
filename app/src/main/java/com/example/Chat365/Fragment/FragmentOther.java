package com.example.Chat365.Fragment;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Activity.User.ProfileActivity;
import com.example.Chat365.Adapter.AnotherAdapter.OtherAdapter;
import com.example.Chat365.Model.Other;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FragmentOther extends Fragment {
    View v;
    private List<Other> otherList;
    private OtherAdapter otherAdapter;
    private ListView lv;
    private Button btnUser;
    private FirebaseAuth mCurrent;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mData;
    private User user;
    private Session session;

    private void sendToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void getUser() {
        user = session.getUser();
        if (user == null) {
            sendToHome();
        } else {
            btnUser.setText(user.getName());
            btnUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", user);
                    intent.putExtra("UserBundle", bundle);
                    startActivity(intent);
                }
            });
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCurrent.getCurrentUser() != null) {
            getUser();
        } else {
            sendToHome();
        }

    }

    public FragmentOther() {
        otherList = new ArrayList<>();
        otherList.add(new Other(R.drawable.groupcolor, "Nhóm"));
        otherList.add(new Other(R.drawable.camera, "Album ảnh"));
        otherList.add(new Other(R.drawable.users, "Tìm bạn bè"));
        otherList.add(new Other(R.drawable.placeholder, "Tìm quanh đây"));
        otherList.add(new Other(R.drawable.settings, "Cài đặt"));
        otherList.add(new Other(R.drawable.logout, "Đăng Xuất"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_other, container, false);
        // anh xa
        lv = v.findViewById(R.id.listmenuother);
        btnUser = v.findViewById(R.id.btnUser);
        mCurrent = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        session = new Session(mData,mCurrent.getCurrentUser(),getActivity(), false);
        mData.keepSynced(true);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        otherAdapter = new OtherAdapter(container.getContext(), otherList);
        lv.setAdapter(otherAdapter);
        // event lv
        handleEvent();
        return v;
    }
    private void handleEvent(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (position){
                    case Constant.GROUP:
                        FragmentGroupPrivate fragmentGroupPrivate = new FragmentGroupPrivate();
                        fragmentTransaction.replace(R.id.frag_other, fragmentGroupPrivate)
                                            .addToBackStack("tag").commit();
                        break;
                    case Constant.ALBUM_GALERY:
                        FragmentAlbums fragmentAlbums = new FragmentAlbums();
                        fragmentTransaction.replace(R.id.frag_other, fragmentAlbums)
                                            .addToBackStack("tag").commit();
                        break;
                    case Constant.FIND_FRIEND:
                        FragmentSearch fragmentSearch = new FragmentSearch();
                        fragmentTransaction.replace(R.id.frag_other, fragmentSearch)
                                            .addToBackStack("tag").commit();
                        break;
                    case Constant.FIND_FRIEND_NEAR:
                        FragmentFindFriendNear fragmentFindFriendNear = new FragmentFindFriendNear();
                        fragmentTransaction.replace(R.id.frag_other, fragmentFindFriendNear)
                                .addToBackStack("tag").commit();
                        break;
                    case Constant.SETTING:
                        break;
                    case Constant.LOG_OUT:
                            session.detroyUser(user);
                            LoginManager.getInstance().logOut();
                            mCurrent.signOut();
                            mGoogleSignInClient.signOut();
                            sendtoHome();
                        break;

                }
            }
        });
    }

    private void sendtoHome() {
        Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
