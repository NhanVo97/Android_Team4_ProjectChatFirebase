package com.example.Chat365.Activity.User;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.Chat365.Activity.Galery.AlbumActivity;
import com.example.Chat365.Activity.Post.PostActivity;
import com.example.Chat365.Adapter.QuyenAdapter;
import com.example.Chat365.Model.Quyen;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;

import java.util.ArrayList;
import java.util.List;

public class QuyenActivity extends AppCompatActivity {
    ListView lv;
    QuyenAdapter quyenAdapter;
    User user;
    String ND,Quyen,Album,Check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quyen);
        // Anh Xa
        lv = findViewById(R.id.listQuyen);
        Toolbar toolbar = findViewById(R.id.tbQuyen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chọn quyền riêng tư");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Data
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BundleUser");
        user = (User) bundle.getSerializable("User");
        ND = bundle.getString("ND");
        Quyen=bundle.getString("Quyen");
        Album=bundle.getString("Album");
        Check = bundle.getString("Check");
        // Create List
        final List<Quyen> quyenList = new ArrayList<>();
        quyenList.add(new Quyen("Công Khai","Mọi người trên hoặc ngoài chat365"));
        quyenList.add(new Quyen("Bạn Bè","Bạn bè của bạn trên chat365"));
        quyenList.add(new Quyen("Chỉ Mình Tôi","Tin chỉ có bạn nhìn thấy, riêng tư"));
        quyenAdapter = new QuyenAdapter(quyenList,this,Quyen);
        lv.setAdapter(quyenAdapter);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=null;
                if(Check!=null)
                {
                    intent = new Intent(QuyenActivity.this, AlbumActivity.class);
                }
               else
                {
                    intent = new Intent(QuyenActivity.this, PostActivity.class);
                }
                Bundle bundle = new Bundle();
                Quyen = quyenAdapter.getVitri()+"";
                bundle.putSerializable("User",user);
                bundle.putString("ND",ND);
                bundle.putString("Quyen",Quyen);
                bundle.putString("Album",Album);
                intent.putExtra("BundleLinkAnh",bundle);
                startActivity(intent);

            }
        });
    }

}
