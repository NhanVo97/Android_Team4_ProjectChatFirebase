package com.example.Chat365.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.Chat365.Activity.MainActivity;
import com.example.Chat365.R;

public class FragmentGroupPrivate extends Fragment {
    View v;
    TextView btnCreate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.groupchat,container,false);
        btnCreate = v.findViewById(R.id.btnCreatGroup);
        Toolbar toolbar = v.findViewById(R.id.toolbargr);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Nhóm Chat");
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentCreate fragmentCreate = new FragmentCreate();
                fragmentTransaction.replace(R.id.layoutgroups,fragmentCreate).addToBackStack("tag").commit();
            }
        });
        return v;
    }
}
