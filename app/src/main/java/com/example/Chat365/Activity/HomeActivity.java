package com.example.Chat365.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    Button btnLogin,btnRegister,btnFacebook,btnGoogle;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mData;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private LoginButton btnLoginFacebook;
    private static final String EMAIL = "email";
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Anh Xa
        btnLogin = findViewById(R.id.btnLoginEmail);
        btnRegister =findViewById(R.id.btnRegisterEmail);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnGoogle = findViewById(R.id.btnGoogle);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        final SignInButton signInButton = findViewById(R.id.sign_in_button);
        mCallbackManager = CallbackManager.Factory.create();
        btnLoginFacebook = findViewById(R.id.btnFacebookLogin);
        btnLoginFacebook.setReadPermissions(Arrays.asList(EMAIL));
        //Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);

            }
        });
        // google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signInButton.setSize(SignInButton.SIZE_STANDARD);
                signIn();
            }
        });
        // Facebook
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLoginFacebook.performClick();
            }
        });
        btnLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("AAA",error.toString());

            }
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            boolean kt = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(kt)
                            {
                                String keys=user.getUid();
                                User u = new User(user.getDisplayName(),"",user.getEmail(),"","","User","","",user.getPhotoUrl().toString(),"","","","",keys,"true","Xin chào! Tôi đang sử dụng chat365",0);
                                mData.child("Users").child(keys).setValue(u);
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else {

                            Toast.makeText(getApplicationContext(), "Lỗi không đăng nhập facebook được!.",
                                    Toast.LENGTH_SHORT).show();

                        }


                        // ...
                    }
                });
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            Toast.makeText(getApplicationContext(),"Đăng nhập không thành công!",Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            boolean kt = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(kt)
                            {
                                String keys=user.getUid();
                                User u = new User(user.getDisplayName(),"",user.getEmail(),"","","User","","",user.getPhotoUrl().toString(),"","","","",keys,"true","Xin chào! Tôi đang sử dụng chat365",0);
                                mData.child("Users").child(keys).setValue(u);
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }
                        else
                            {
                                Log.w("AAA", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}

