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
import android.widget.TextView;

import com.example.Chat365.Adapter.AnotherAdapter.ViewPictureAdapter;
import com.example.Chat365.R;

import java.util.List;

public class FragmentGalery extends Fragment implements ViewPictureAdapter.Oncallback {
    View v;
    RecyclerView rc;
    ViewPictureAdapter hinhAdapter;
    TextView tvStatus;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragmentgalery, container, false);
        rc = v.findViewById(R.id.rycGaleryAB);
        tvStatus = v.findViewById(R.id.tvStatus);
        Bundle bundle = getArguments();
        List<String> listHinh = bundle.getStringArrayList("ListHinh");
        if(listHinh!=null){
            hinhAdapter = new ViewPictureAdapter(this,listHinh,getFragmentManager());
            rc.setHasFixedSize(true);
            rc.setLayoutManager(new GridLayoutManager(getActivity(),3));
            rc.setAdapter(hinhAdapter);
            tvStatus.setVisibility(View.GONE);
        } else {
            tvStatus.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onItemClick(int position) {

    }
}
