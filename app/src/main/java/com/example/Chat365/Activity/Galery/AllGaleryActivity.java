package com.example.Chat365.Activity.Galery;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.Chat365.Activity.Post.PostActivity;
import com.example.Chat365.Adapter.LibraryAdapter.GaleryAdapter;
import com.example.Chat365.Model.Gallery;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.Chat365.Utils.PermissionUtils.checkPermissionREAD_EXTERNAL_STORAGE;
public class AllGaleryActivity extends AppCompatActivity {
    private GridView gridView;
    private TextView imCamera;
    private Toolbar toolbar;
    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private TextView tvCheck;
    private List<Gallery> linkAnh;
    private List<Gallery> listAnh;
    private int Request_Code_Image = 123;
    private GaleryAdapter galeryAdapter;
    private int num = 0;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant
                    .REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadGalery();

                } else {
                    Toast.makeText(this, "Từ chối quyền truy cập đa phương tiện",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==Request_Code_Image && resultCode == RESULT_OK && data!=null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            try {
                stream.close();
                bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent= new Intent(AllGaleryActivity.this, PostActivity.class);
            Bundle bundle = new Bundle();
            linkAnh.add(new Gallery(byteArray));
            bundle.putSerializable("LinkAnh", (ArrayList<Gallery>) linkAnh);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void loadGalery(){
        listAnh.clear();
        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri,null,null,null,null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                Gallery gallery = new Gallery(link,name,0,false,null);
                listAnh.add(gallery);
                cursor.moveToNext();
            }
            cursor.close();
            galeryAdapter.notifyDataSetChanged();
            if(listAnh==null) {
                tvCheck.setText("Thư viện trống!");
            }
        }
    }
    private void anhXa(){
        // other view
        gridView = findViewById(R.id.gvhinhanh);
        imCamera = findViewById(R.id.imCamera);
        toolbar = findViewById(R.id.tbpicture);
        tvCheck = findViewById(R.id.tvCheckGalery);
        // Action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thư Viện Ảnh");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // list
        listAnh = new ArrayList<>();
        Log.e("AAA",listAnh.size()+"");
        linkAnh = new ArrayList<>();
        // init adapter
        galeryAdapter = new GaleryAdapter(this,R.layout.itemgalery,listAnh,true);
        gridView.setAdapter(galeryAdapter);
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictureall);
        // Anh Xa View
        anhXa();
        // load picture from internal
        loadGalery();

        // Event
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                galeryAdapter.notifyDataSetChanged();
                if(gridView.isItemChecked(position)) {
                    view = gridView.getChildAt(position);
                    view.setBackgroundResource(R.drawable.pressed);
                    listAnh.get(position).setCheck(true);
                    num++;
                    listAnh.get(position).setNumber(num);
                    linkAnh.add(listAnh.get(position));
                    imCamera.setText("Xong");
                    imCamera.setBackgroundResource(android.R.color.transparent);
                    updateEvent(true);
                }else {
                    view = gridView.getChildAt(position);
                    view.setBackgroundResource(R.drawable.normal);
                    int y = listAnh.get(position).getNumber();//2
                    if((num-1)!=position) {
                        for(int i=0;i<listAnh.size();i++)
                        {
                            if(listAnh.get(i).getNumber()!=0) {
                                int x =listAnh.get(i).getNumber();//1 2 3
                                if(x>y)
                                {
                                    listAnh.get(i).setNumber(listAnh.get(i).getNumber()-1);
                                    galeryAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    listAnh.get(position).setNumber(0);
                    num--;
                    linkAnh.remove(listAnh.get(position));
                    listAnh.get(position).setCheck(false);
                }
                if(linkAnh.size() == 0) {
                    imCamera.setText("");
                    imCamera.setBackgroundResource(R.drawable.camera_photo);
                    updateEvent(false);
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AllGaleryActivity.this, PostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("LinkAnh", (ArrayList<Gallery>) linkAnh);
                intent.putExtra("BundleLinkAnh",bundle);
                setResult(Constant.REQUEST_CODE_ALBUM,intent);
                finish();
            }
        });
        updateEvent(false);
    }
    private void updateEvent(boolean check) {
        if(check) {
            imCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllGaleryActivity.this, PostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("LinkAnh", (ArrayList<Gallery>) linkAnh);
                    intent.putExtra("BundleLinkAnh",bundle);
                    setResult(Constant.REQUEST_CODE_ALBUM,intent);
                    finish();
                }
            });
        }
        else {
            imCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,Request_Code_Image);
                }
            });
        }

    }
}
