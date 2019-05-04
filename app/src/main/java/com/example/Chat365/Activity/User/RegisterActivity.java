package com.example.Chat365.Activity.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.Chat365.R;
import com.example.Chat365.Utils.Management.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edName, edMail, edPassword, edDate;
    private RadioGroup rGSex;
    private Button btnRegister;
    private String Name, Mail, Password, Birthday;
    private String Sex = "";
    private FirebaseAuth mAuth;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // init data
        init();
        // event click
        edDate.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        // event checkbox
        rGSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdNam:
                        Sex = "Nam";
                        break;
                    case R.id.rdNu:
                        Sex = "Nữ";
                        break;
                }
            }
        });
    }

    private boolean validateInput() {
        // get Value Input
        Name = edName.getText().toString();
        Mail = edMail.getText().toString();
        Password = edPassword.getText().toString();
        Birthday = edDate.getText().toString();
        if (Mail.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email không được bỏ trống!", Toast.LENGTH_SHORT).show();
            edMail.findFocus();
            return false;
        } else if (Name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Chúng tôi có thể gọi bạn bằng gì?", Toast.LENGTH_SHORT).show();
            edName.findFocus();
            return false;
        } else if (Password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu không được bỏ trống rồi!", Toast.LENGTH_SHORT).show();
            edPassword.findFocus();
            return false;
        } else if (Birthday.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Hãy cho chúng tôi biết ngày sinh của bạn!", Toast.LENGTH_SHORT).show();
            edDate.findFocus();
            return false;
        } else if (Sex.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Bạn chưa chọn giới tính của mình!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void init() {
        // Action bar config
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng Ký");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2f80ed")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Anh Xa Phan Tu
        edName = findViewById(R.id.edName);
        edMail = findViewById(R.id.Mail);
        edPassword = findViewById(R.id.edPassword);
        edDate = findViewById(R.id.edDate);
        rGSex = findViewById(R.id.rGSex);
        btnRegister = findViewById(R.id.btnRegister);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void CreateAccount() {
        mAuth.createUserWithEmailAndPassword(Mail, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Session session = new Session(mData,mAuth.getCurrentUser(),getApplicationContext(),true);
                            session.initUser();
                            Toast.makeText(RegisterActivity.this, "Đăng Ký Thành Công", Toast.LENGTH_SHORT).show();
                            goHome();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đăng Ký Thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SetValueBirthday() {
        final Calendar calendar = Calendar.getInstance();
        int Day = calendar.get(Calendar.DATE);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edDate.setText(simpleDateFormat.format(calendar.getTime()));
                Birthday = edDate.getText().toString();
            }
        }, Year, Month, Day);
        datePickerDialog.show();
    }

    private void goHome(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
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
        switch (id) {
            case R.id.btnRegister:
                if (validateInput()) {
                    CreateAccount();
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edDate:
                SetValueBirthday();
                break;
        }
    }
}
