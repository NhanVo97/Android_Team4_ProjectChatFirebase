package com.example.Chat365.Activity.Library;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.Chat365.Adapter.LibraryAdapter.GaleryPickerAdapter;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

import static com.example.Chat365.Utils.PermissionUtils.checkPermissionREAD_EXTERNAL_STORAGE;

public class GaleryPicker extends AppCompatActivity implements GaleryPickerAdapter.OnCallBack {
    RecyclerView rcGalery;
    GaleryPickerAdapter galeryPickerAdapter;
    List<String> listHinh;
    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery_picker);
        rcGalery = findViewById(R.id.rcGalery);
        listHinh = new ArrayList<>();
        if (checkPermissionREAD_EXTERNAL_STORAGE(GaleryPicker.this)) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri,null,null,null,null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                listHinh.add(link);
                cursor.moveToNext();
            }
        } else {
            Toast.makeText(this, "Từ chối quyền truy cập đa phương tiện",
                    Toast.LENGTH_SHORT).show();
        }
        galeryPickerAdapter = new GaleryPickerAdapter(listHinh,this);
        rcGalery.setHasFixedSize(true);
        rcGalery.setLayoutManager(new GridLayoutManager(this,3));
        rcGalery.setAdapter(galeryPickerAdapter);
    }

    @Override
    public void OnItemSelect(int position) {
        Intent intent = new Intent();
        intent.putExtra("LinkGalery",listHinh.get(position));
        setResult(Constant.REQUEST_CODE_SUCESS,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("LinkGalery","");
        setResult(Constant.REQUEST_CODE_SUCESS,intent);
        finish();
        super.onBackPressed();
    }
}
