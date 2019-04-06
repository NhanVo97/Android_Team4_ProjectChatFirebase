package com.example.Chat365.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {
    Button btnHistory,btnAddInfo,btnAddAvt,btnAddBgAvt;
    EditText edHistory,edProvice,edWork,edStudy,edHometown,edRelationship;
    DatabaseReference mFirebase;
    ImageView imAvt,imBg;
    User user,newU;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chỉnh sửa thông tin");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_profile);
        // Anh Xa
        btnHistory = findViewById(R.id.btnHistory);
        btnAddInfo = findViewById(R.id.btnEdit);
        edHistory = findViewById(R.id.edHistory);
        edProvice = findViewById(R.id.edProvince);
        edWork = findViewById(R.id.edWork);
        edStudy = findViewById(R.id.edStudy);
        edHometown = findViewById(R.id.edHometown);
        edRelationship = findViewById(R.id.edRelationship);
        btnAddAvt = findViewById(R.id.btnAddAvt);
        btnAddBgAvt = findViewById(R.id.btnAddbgAvt);
        imAvt = findViewById(R.id.bgAvatarUser);
        imBg = findViewById(R.id.bgAvatar);

        // Bundle and event
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("UserBundle");
       user = (User) bundle.getSerializable("User");
       if(!user.getAvatar().equals(""))
       {
           Picasso.get().load(user.getAvatar()).into(imAvt);
       }
        // Load Data Firebase
        mFirebase= FirebaseDatabase.getInstance().getReference("User").child(user.getId());
        edHistory.setText(user.getHistory());
        edHometown.setText(user.getHomeTown());
        edRelationship.setText(user.getRelationship());
        edStudy.setText(user.getStudy());
        edWork.setText(user.getWork());
        edProvice.setText(user.getProvince());
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String HistoryUser = edHistory.getText().toString();
                mFirebase.child("history").setValue(HistoryUser);
                Toast.makeText(getApplicationContext(),"Update tiểu sử thành công!",Toast.LENGTH_SHORT).show();

            }
        });
        btnAddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebase.child("province").setValue(edProvice.getText().toString());
                mFirebase.child("homeTown").setValue(edHometown.getText().toString());
                mFirebase.child("work").setValue(edWork.getText().toString());
                mFirebase.child("study").setValue(edStudy.getText().toString());
                mFirebase.child("relationship").setValue(edRelationship.getText().toString());
                newU = new User(user.getName(), user.getPassword(), user.getEmail(), user.getSex(), user.getBirthday(), user.getLevel(), edHistory.getText().toString(), edProvice.getText().toString(), user.getAvatar(), edHometown.getText().toString(), edWork.getText().toString(), edStudy.getText().toString(), edRelationship.getText().toString(), user.getId(),user.getIsOnline(),user.getStatus(),user.getTimestamp());
                Toast.makeText(getApplicationContext(),"Update thành công thông tin!",Toast.LENGTH_SHORT).show();
                //MainActivity.setUser(newU);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
