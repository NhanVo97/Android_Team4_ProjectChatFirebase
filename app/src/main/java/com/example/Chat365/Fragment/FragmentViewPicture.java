package com.example.Chat365.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.Chat365.R;

import uk.co.senab.photoview.PhotoView;

public class FragmentViewPicture extends Fragment {
    View v;
    PhotoView photoView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.viewpicture,container,false);
        photoView = v.findViewById(R.id.ptvhinh);
        Bundle extras = getArguments();
        String linkAnh = extras.getString("LinkAnh");
        Glide.with(v.getContext()).load(linkAnh).into(photoView);
        return v;
    }
}
