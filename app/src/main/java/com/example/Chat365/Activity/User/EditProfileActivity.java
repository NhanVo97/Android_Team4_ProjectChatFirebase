package com.example.Chat365.Activity.User;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Chat365.Activity.Library.GaleryPicker;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnHistory, btnAddInfo, btnAddAvt, btnAddBgAvt;
    EditText edHistory, edProvice, edWork, edStudy, edHometown, edRelationship;
    DatabaseReference mFirebase;
    FirebaseStorage storage;
    StorageReference storageRef;
    UploadTask uploadTask;
    FirebaseAuth mAuth;
    ImageView imAvt, imBg;
    User user,current;
    Session session;
    boolean isUpAvatar = false;
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
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
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
        current = session.getUser();
        edHometown.setText(user.getHomeTown());
        edRelationship.setText(user.getRelationship());
        edStudy.setText(user.getStudy());
        edWork.setText(user.getWork());
        edProvice.setText(user.getHomeTown());
        edHistory.setText(user.getHistory());
        btnHistory.setOnClickListener(this);
        btnAddInfo.setOnClickListener(this);
        btnAddAvt.setOnClickListener(this);
        btnAddBgAvt.setOnClickListener(this);
    }
    private void upGaleryProcess(Uri linkAnh){
        final StorageReference riversRef = storageRef.child("User/"+mAuth.getCurrentUser().getUid()+"/"+linkAnh.getLastPathSegment());
        uploadTask = riversRef.putFile(linkAnh);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xu ly K thanh cong
                Log.e("AAA",exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Thanh Cong
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            if(isUpAvatar){
                                mFirebase.child("linkAvatar").setValue(downloadUri.toString());
                                current.setLinkAvatar(downloadUri.toString());
                                Toast.makeText(getApplicationContext(), "Up hình đại diện thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                mFirebase.child("linkAnhBia").setValue(downloadUri.toString());
                                current.setLinkBackground(downloadUri.toString());
                                Toast.makeText(getApplicationContext(), "Up ảnh bìa thành công", Toast.LENGTH_SHORT).show();
                            }
                            session.updateSession(current);
                        } else {

                        }
                    }
                });

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String linkAnh = data.getStringExtra("LinkGalery");
        Uri file = Uri.fromFile(new File(linkAnh));
        if(requestCode == Constant.REQUEST_CODE_SUCESS){
            if(isUpAvatar){
                Picasso.get().load(file).into(imAvt);
                upGaleryProcess(file);

            } else {
                Picasso.get().load(file).into(imBg);
                upGaleryProcess(file);

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        Intent intent = new Intent(this, GaleryPicker.class);
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
            case R.id.btnAddAvt:
                isUpAvatar = true;
                startActivityForResult(intent, Constant.REQUEST_CODE_SUCESS);
                break;
            case R.id.btnAddbgAvt:
                isUpAvatar = false;
                startActivityForResult(intent, Constant.REQUEST_CODE_SUCESS);
                break;
        }
    }
}
