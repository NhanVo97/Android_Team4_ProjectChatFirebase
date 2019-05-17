package com.example.Chat365.Activity.User;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.Chat365.Adapter.UserAdapter.AlbumAdapter.PermissionAdapter;
import com.example.Chat365.Model.Quyen;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class QuyenActivity extends AppCompatActivity {
    ListView lvQuyen;
    PermissionAdapter permissionAdapter;
    List<Quyen> listQuyen;
    String quyen = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quyen);
        // Anh Xa
        lvQuyen = findViewById(R.id.listQuyen);
        Toolbar toolbar = findViewById(R.id.tbQuyen);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chọn quyền riêng tư");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        // Get Data
        Intent intent = getIntent();
        quyen = intent.getStringExtra("Quyen");
        // Create List
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                int vitri = permissionAdapter.getVitri();
                intent.putExtra("positionPermission",String.valueOf(vitri));
                setResult(Constant.REQUEST_CODE_PERMISSION,intent);
                finish();
            }
        });
    }

    private void initData() {
        listQuyen = new ArrayList<>();
        listQuyen.add(new Quyen("Công Khai","Mọi người trên hoặc ngoài chat365"));
        listQuyen.add(new Quyen("Bạn Bè","Bạn bè của bạn trên chat365"));
        listQuyen.add(new Quyen("Chỉ Mình Tôi","Tin chỉ có bạn nhìn thấy, riêng tư"));
        permissionAdapter = new PermissionAdapter(listQuyen,this,quyen);
        lvQuyen.setAdapter(permissionAdapter);
    }

}
