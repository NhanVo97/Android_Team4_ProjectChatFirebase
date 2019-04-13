package com.example.Chat365.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Chat365.R;
import com.example.Chat365.Utils.Management.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edMail, edPassword;
    TextView btnforget, btnregister;
    Button btnLogin;
    FirebaseAuth mAuth;
    String Email, Password;
    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // init data
        init();
        // event
        btnregister.setOnClickListener(this);
        btnforget.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng Nhập");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2f80ed")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        // ánh xạ
        edMail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        btnforget = findViewById(R.id.btnForget);
        btnregister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLoginEmail);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
    }

    private void Login(final String Email, final String Password) {
        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Session session = new Session(mData,mAuth.getCurrentUser(),getApplicationContext(),false);
                    session.initUser();
                    goHome();
                } else {
                    Toast.makeText(LoginActivity.this, "Mật khẩu hoặc Email không đúng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Event back in action bar
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
        switch (id) {
            case R.id.btnRegister:
                goRegister();
                break;
            case R.id.btnForget:
                goForgotPassword();
                break;
            case R.id.btnLoginEmail:
                Email = edMail.getText().toString();
                Password = edPassword.getText().toString();
                if (Email.equals("") || Password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Email và Mật khẩu không được bỏ trống!", Toast.LENGTH_SHORT).show();
                } else {
                    Login(Email, Password);
                }
                break;
        }
    }

    private void goHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goForgotPassword() {
        Intent intent = new Intent(getApplicationContext(), ForgetActivity.class);
        startActivity(intent);
    }

    private void goRegister() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}
