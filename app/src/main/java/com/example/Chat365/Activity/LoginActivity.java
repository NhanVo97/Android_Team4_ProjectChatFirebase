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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class LoginActivity extends AppCompatActivity {

    EditText edMail,edPassword;
    TextView btnforget,btnregister;
    Button btnLogin;
    FirebaseAuth mAuth;
    String Email,Password;
    DatabaseReference mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng Nhập");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2f80ed")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        // ánh xạ
        edMail = findViewById(R.id.edEmail);
        edPassword=findViewById(R.id.edPassword);
        btnforget = findViewById(R.id.btnForget);
        btnregister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLoginEmail);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        // event
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgetActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = edMail.getText().toString();
                Password = edPassword.getText().toString();
                if(Email.equals("") || Password.equals(""))
                {
                    Toast.makeText(LoginActivity.this,"Email và Mật khẩu không được bỏ trống!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Login(Email,Password);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void Login(final String Email, final String Password)
    {
        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                               // mData.child("User").child(uAll.getId()).child("Online").setValue("true");
                               // User u = new User(uAll.getName(),uAll.getPassword(),uAll.getEmail(),uAll.getSex(),uAll.getBirthday(),uAll.getLevel(),uAll.getHistory(),uAll.getProvince(),uAll.getAvatar(),uAll.getHomeTown(),uAll.getWork(),uAll.getStudy(),uAll.getRelationship(),uAll.getId(),"true");
                                startActivity(intent);

                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Mật khẩu hoặc Email không đúng!",Toast.LENGTH_SHORT).show();
                }
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
