package com.example.Chat365.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Chat365.Adapter.HinhAdapter2;
import com.example.Chat365.R;

import java.util.List;

public class FragmentGalery extends Fragment implements HinhAdapter2.Oncallback {
    View v;
    RecyclerView rc;
    HinhAdapter2 hinhAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragmentgalery, container, false);
        rc = v.findViewById(R.id.rycGaleryAB);
        Bundle bundle = getArguments();
        List<String> list = bundle.getStringArrayList("ListHinh");
        hinhAdapter = new HinhAdapter2(this,list,getFragmentManager());
        rc.setHasFixedSize(true);
        rc.setLayoutManager(new GridLayoutManager(getActivity(),3));
        rc.setAdapter(hinhAdapter);
        return v;
    }

    @Override
    public void onItemClick(int position) {

    }
}
