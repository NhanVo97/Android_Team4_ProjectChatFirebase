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
import android.widget.EditText;
import android.widget.ImageView;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Activity.Post.PostActivity;
import com.example.Chat365.Adapter.PostAdapter;
import com.example.Chat365.Model.PostStatus;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentActivity extends Fragment implements PostAdapter.Oncallback {
    View v;
    EditText editStt;
    ImageView imAvt;
    DatabaseReference mData;
    FirebaseAuth mCurrent;
    List<PostStatus> listPost;
    PostAdapter postAdapter;
    RecyclerView lv;
    User user;

    @Override
    public void onStart() {
        super.onStart();
        startUser();
        getData();
    }

    private void startUser() {
        Session session = new Session(mData,mCurrent.getCurrentUser(),getActivity(),false);
        user = session.getUser();
        if(user == null){
            backToHome();
        } else {
            if(user.getLinkAvatar()!=null){
                Picasso.get().load(user.getLinkAvatar()).into(imAvt);
            }
        }

    }

    public void getData() {
        listPost.clear();
        mData.keepSynced(true);
        mData.child("PostActivity").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostStatus postStatus = dataSnapshot.getValue(PostStatus.class);
                if (postStatus.getAccess().equals("0") || postStatus.getId().equals(mCurrent.getCurrentUser().getUid())) {
                    listPost.add(postStatus);
                }
                Collections.reverse(listPost);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostStatus postStatus = dataSnapshot.getValue(PostStatus.class);
                for(int i = 0;i<listPost.size();i++){
                    if(listPost.get(i).getKey().equals(postStatus.getKey())){
                        listPost.set(i,postStatus);
                        break;
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                PostStatus postStatus = dataSnapshot.getValue(PostStatus.class);
                listPost.remove(postStatus);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void backToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_active, container, false);
        // Anh Xa
        editStt = v.findViewById(R.id.edActt);
        imAvt = v.findViewById(R.id.imAvatarPrv);
        mCurrent = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        listPost = new ArrayList<>();
        lv = v.findViewById(R.id.postlist);
        postAdapter = new PostAdapter(this, listPost);
        lv.setHasFixedSize(true);
        lv.setLayoutManager(new LinearLayoutManager(getContext()));
        lv.setAdapter(postAdapter);
        // Event
        editStt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User",user);
                intent.putExtra("BundleUser",bundle);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onItemClick(int position) {

    }
}
