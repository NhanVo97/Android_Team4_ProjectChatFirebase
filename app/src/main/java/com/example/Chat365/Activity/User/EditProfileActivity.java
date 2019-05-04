package com.example.Chat365.Activity.User;

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
import com.example.Chat365.Utils.Management.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnHistory, btnAddInfo, btnAddAvt, btnAddBgAvt;
    EditText edHistory, edProvice, edWork, edStudy, edHometown, edRelationship;
    DatabaseReference mFirebase;
    FirebaseAuth mAuth;
    ImageView imAvt, imBg;
    User user;
    Session session;
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
        mAuth = FirebaseAuth.getInstance();
        // Bundle and event
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("UserBundle");
        user = (User) bundle.getSerializable("User");
        if (!user.getLinkAvatar().equals("")) {
            Picasso.get().load(user.getLinkAvatar()).into(imAvt);
        }
        // Load Data Firebase
        mFirebase = FirebaseDatabase.getInstance().getReference("Users").child(user.getId());
        session = new Session(mFirebase,mAuth.getCurrentUser(),getApplicationContext(),false);
        edHometown.setText(user.getHomeTown());
        edRelationship.setText(user.getRelationship());
        edStudy.setText(user.getStudy());
        edWork.setText(user.getWork());
        edProvice.setText(user.getHomeTown());
        edHistory.setText(user.getHistory());
        btnHistory.setOnClickListener(this);
        btnAddInfo.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnHistory:
                String HistoryUser = edHistory.getText().toString();
                mFirebase.child("history").setValue(HistoryUser);
                session.updateSession(user);
                Toast.makeText(getApplicationContext(), "Update tiểu sử thành công!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnEdit:
                // update Session
                user.setLive(edProvice.getText().toString());
                user.setHomeTown(edHometown.getText().toString());
                user.setWork(edWork.getText().toString());
                user.setStudy(edStudy.getText().toString());
                user.setRelationship(edRelationship.getText().toString());
                session.updateSession(user);
                // Update record firebase
                mFirebase.setValue(user);
                Toast.makeText(getApplicationContext(), "Update thành công thông tin!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bgAvatarUser:
                break;
            case R.id.bgAvatar:
                break;
        }
    }
}
