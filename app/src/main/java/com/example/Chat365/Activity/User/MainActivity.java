package com.example.Chat365.Activity.User;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.Chat365.Activity.HomeActivity;
import com.example.Chat365.Adapter.MainAdapter.MainViewPagerAdapter;
import com.example.Chat365.Fragment.FragmentActivity;
import com.example.Chat365.Fragment.FragmentGroup;
import com.example.Chat365.Fragment.FragmentHome;
import com.example.Chat365.Fragment.FragmentNotification;
import com.example.Chat365.Fragment.FragmentOther;
import com.example.Chat365.Model.LocationUser;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.example.Chat365.Scheduler.Service.LocationService;
import com.example.Chat365.Utils.Constant;
import com.example.Chat365.Utils.Management.Session;
import com.example.Chat365.Utils.PermissionUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private  LocationUser locationUser;
    private DatabaseReference mData;
    Session session;
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Tài khoản bị đăng xuất", Toast.LENGTH_SHORT).show();
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Anh Xa
        tabLayout = findViewById(R.id.tablayour);
        viewPager = findViewById(R.id.viewpager);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        session = new Session(mData,mAuth.getCurrentUser(),getApplicationContext(),false);
        // Fragment navigation
        init();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tab.setIcon(R.drawable.activity_active);
                } else {
                    tabLayout.getTabAt(0).setIcon(R.drawable.activity);
                }
                if (tab.getPosition() == 1) {
                    tab.setIcon(R.drawable.message_active);
                } else {
                    tabLayout.getTabAt(1).setIcon(R.drawable.chating);
                }
                if (tab.getPosition() == 2) {
                    tab.setIcon(R.drawable.group_active);
                } else {
                    tabLayout.getTabAt(2).setIcon(R.drawable.group);
                }

                if (tab.getPosition() == 3) {
                    tab.setIcon(R.drawable.notification_active);
                } else {
                    tabLayout.getTabAt(3).setIcon(R.drawable.notification);
                }
                if (tab.getPosition() == 4) {
                    tab.setIcon(R.drawable.menuline_active);
                } else {
                    tabLayout.getTabAt(4).setIcon(R.drawable.menuline);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(LocationService.str_receiver));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constant
                    .REQUEST_PERMISSIONS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getApplicationContext(), "Xin hãy cho phép quyền thiết bị", Toast.LENGTH_LONG).show();

                }
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    private void updateTokenNotification(){
        SharedPreferences sharedPreferences= this.getSharedPreferences("TokenNotification", Context.MODE_PRIVATE);
        User user = session.getUser();
        String token = sharedPreferences.getString("Token","");
        user.setTokenNotification(token);
        session.updateSession(user);
        mData.child("Users").child(currentUser.getUid()).child("tokenNotification").setValue(token);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Double latitude = Double.valueOf(intent.getStringExtra("latutide"));
            Double longitude = Double.valueOf(intent.getStringExtra("longitude"));
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAdminArea();
                String countryName = addresses.get(0).getCountryName();
                locationUser = new LocationUser(address,stateName,countryName,latitude,longitude,false);
                User user = session.getUser();
                user.setLocationUser(locationUser);
                session.updateSession(user);
                mData.child("Users").child(currentUser.getUid()).child("locationUser").setValue(locationUser);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    };

    private void init() {
        updateTokenNotification();
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPagerAdapter.addFragment(new FragmentActivity(), "Hoạt động");
        mainViewPagerAdapter.addFragment(new FragmentHome(), "Tin Nhắn");
        mainViewPagerAdapter.addFragment(new FragmentGroup(), "Nhóm");
        mainViewPagerAdapter.addFragment(new FragmentNotification(), "Thông báo");
        mainViewPagerAdapter.addFragment(new FragmentOther(), "Khác");
        viewPager.setAdapter(mainViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.activity_active);
        tabLayout.getTabAt(1).setIcon(R.drawable.message);
        tabLayout.getTabAt(2).setIcon(R.drawable.group);
        tabLayout.getTabAt(3).setIcon(R.drawable.notification);
        tabLayout.getTabAt(4).setIcon(R.drawable.menuline);
        if(PermissionUtils.Check_FINE_LOCATION(this)){
            Intent locationService = new Intent(getApplicationContext(), LocationService.class);
            startService(locationService);
        } else {
            Toast.makeText(this, "Xin hãy bật GPS", Toast.LENGTH_SHORT).show();
        }
    }
}
