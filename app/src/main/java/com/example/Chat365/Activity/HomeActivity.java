package com.example.Chat365.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.Chat365.Activity.User.LoginActivity;
import com.example.Chat365.Activity.User.MainActivity;
import com.example.Chat365.Activity.User.RegisterActivity;
import com.example.Chat365.R;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
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

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RC_SIGN_IN = 1;
    Button btnLogin, btnRegister, btnFacebook, btnGoogle;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mData;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private LoginButton btnLoginFacebook;
    private static final String EMAIL = "email";
    private SignInButton signInButton;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Session session = new Session(mData,mAuth.getCurrentUser(),getApplicationContext(), false);
            session.initUser();
            goToMain();
        }
    }

    private void goToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToRegister() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        btnLogin = findViewById(R.id.btnLoginEmail);
        btnRegister = findViewById(R.id.btnRegisterEmail);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnGoogle = findViewById(R.id.btnGoogle);
        signInButton = findViewById(R.id.sign_in_button);
        btnLoginFacebook = findViewById(R.id.btnFacebookLogin);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        mCallbackManager = CallbackManager.Factory.create();
    }
    private void askPermissionLocation() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        Constant.REQUEST_PERMISSIONS_LOCATION);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Anh Xa
        init();
        // set read permisision email
        btnLoginFacebook.setReadPermissions(Arrays.asList(EMAIL));
        // ask Location
        askPermissionLocation();
        //Event button login & register with email
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        // Google Login & config
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.clientID))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogle.setOnClickListener(this);
        // Facebook Login
        btnFacebook.setOnClickListener(this);
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
                Log.e("ERROR_LOGIN_FB", error.toString());
                Toast.makeText(HomeActivity.this, "Đăng nhập Facebook thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Facebook Handle
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.e("AAA",isNewUser+"");
                            Session session = new Session(mData,user, getApplicationContext(), isNewUser);
                            session.initUser();
                            goToMain();
                        } else {
                            Toast.makeText(getApplicationContext(), "Lỗi không đăng nhập facebook được!.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // Google Handle
    private void signInGoogle() {
        // open modal & handle in method onActivityResult
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.e("LOGIN_GOOGLE", "Google sign in failed", e);
            Toast.makeText(HomeActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
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
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.e("AAA",isNewUser+"");
                            Session session = new Session(mData,user, getApplicationContext(), isNewUser);
                            session.initUser();
                            goToMain();
                        } else {
                            Log.e("LOGIN_FIREBASE", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    // Event click
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnLoginEmail:
                // redirect page to login
                goToLogin();
                break;
            case R.id.btnRegisterEmail:
                // redirect page to register
                goToRegister();
                break;
            case R.id.btnGoogle:
                signInButton.setSize(SignInButton.SIZE_STANDARD);
                signInGoogle();
                break;
            case R.id.btnFacebook:
                // double click , because button facebook can't custom view
                btnLoginFacebook.performClick();
                break;
        }
    }
}

