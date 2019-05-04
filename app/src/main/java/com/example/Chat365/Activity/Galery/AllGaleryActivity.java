package com.example.Chat365.Activity.Galery;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.Chat365.Activity.Post.PostActivity;
import com.example.Chat365.Adapter.GaleryAdapter;
import com.example.Chat365.Model.Galery;
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
    GridView gridView;
    TextView imCamera;
    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    List<Galery> listAnh;
    TextView tvCheck;
    List<Galery> linkAnh;
    int Request_Code_Image = 123;
    GaleryAdapter galeryAdapter;
    int num =0;
    User user;
    String ND="",Quyen="",Album="";
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant
                    .REQUEST_PERMISSIONS_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(uri,null,null,null,null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        Galery galery = new Galery(link,name,false,"");
                        listAnh.add(galery);
                        cursor.moveToNext();
                    }
                    galeryAdapter.notifyDataSetChanged();
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
        if(requestCode==Request_Code_Image && resultCode == RESULT_OK && data!=null)
        {
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
            linkAnh.add(new Galery(byteArray));
            bundle.putSerializable("LinkAnh", (ArrayList<Galery>) linkAnh);
            bundle.putSerializable("User",user);
            bundle.putString("ND",ND);
            bundle.putString("Quyen",Quyen);
            bundle.putString("Album",Album);
            bundle.putByteArray("Image",byteArray);
            intent.putExtra("BundleLinkAnh",bundle);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictureall);

        // Anh Xa
        gridView = findViewById(R.id.gvhinhanh);
        //gridView.setChoiceMode(gridView.CHOICE_MODE_MULTIPLE_MODAL);
        imCamera = findViewById(R.id.imCamera);
        linkAnh = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.tbpicture);
        tvCheck = findViewById(R.id.tvCheckGalery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thư Viện");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BundleUser");
        user = (User) bundle.getSerializable("User");
        ND = bundle.getString("ND");
        Quyen=bundle.getString("Quyen");
        Album=bundle.getString("Album");
        listAnh = new ArrayList<>();
        final List<Galery> list = (List<Galery>) bundle.getSerializable("LinkAnh");

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri,null,null,null,null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                String link = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                Galery galery = new Galery(link,name,false,"");
                listAnh.add(galery);
                cursor.moveToNext();
            }
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                galeryAdapter.notifyDataSetChanged();
                if(gridView.isItemChecked(position)) {
                    view = gridView.getChildAt(position);
                    view.setBackgroundResource(R.drawable.pressed);
                    listAnh.get(position).setCheck(true);
                    num++;
                    listAnh.get(position).setNum(num+"");
                    linkAnh.add(listAnh.get(position));
                    imCamera.setText("Xong");
                    imCamera.setBackgroundResource(android.R.color.transparent);
                    updateEvent(true);

                }else {
                    view = gridView.getChildAt(position);
                    view.setBackgroundResource(R.drawable.normal);
                    int y = Integer.parseInt(listAnh.get(position).getNum());//2
                    if((num-1)!=position) {
                        for(int i=0;i<listAnh.size();i++)
                        {
                            if(!listAnh.get(i).getNum().equals("")) {
                                int x = Integer.parseInt(listAnh.get(i).getNum());//1 2 3
                                if(x>y)
                                {
                                    listAnh.get(i).setNum((Integer.parseInt(listAnh.get(i).getNum())-1)+"");
                                    galeryAdapter.notifyDataSetChanged();
                                }
//
                            }
                        }
                    }
                    listAnh.get(position).setNum("");
                    num--;
                    linkAnh.remove(listAnh.get(position));
                    listAnh.get(position).setCheck(false);

                }
                if(linkAnh.size()==0)
                {
                    imCamera.setText("");
                    imCamera.setBackgroundResource(R.drawable.camera_photo);
                    imCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    updateEvent(false);
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AllGaleryActivity.this, PostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("LinkAnh", (ArrayList<Galery>) linkAnh);
                bundle.putSerializable("User",user);
                bundle.putString("ND",ND);
                bundle.putString("Quyen",Quyen);
                bundle.putString("Album",Album);
                intent.putExtra("BundleLinkAnh",bundle);
                startActivity(intent);

            }
        });
        updateEvent(false);
        galeryAdapter = new GaleryAdapter(this,R.layout.itemgalery,listAnh);
        gridView.setAdapter(galeryAdapter);

    }
    private void updateEvent(boolean check)
    {
        if(check)
        {
            imCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllGaleryActivity.this, PostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("LinkAnh", (ArrayList<Galery>) linkAnh);
                    bundle.putSerializable("User",user);
                    bundle.putString("ND",ND);
                    bundle.putString("Quyen",Quyen);
                    bundle.putString("Album",Album);
                    intent.putExtra("BundleLinkAnh",bundle);
                    startActivity(intent);
                }
            });
        }
        else
        {
            imCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,Request_Code_Image);
                }
            });
        }
        if(listAnh==null)
        {
            tvCheck.setText("Thư viện trống!");
        }

    }
}
