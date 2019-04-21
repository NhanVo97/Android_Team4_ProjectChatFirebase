package com.example.Chat365.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.Chat365.Adapter.MainViewPagerAdapter;
import com.example.Chat365.Fragment.FragmentActivity;
import com.example.Chat365.Fragment.FragmentGroup;
import com.example.Chat365.Fragment.FragmentHome;
import com.example.Chat365.Fragment.FragmentNotification;
import com.example.Chat365.Fragment.FragmentOther;
import com.example.Chat365.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

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

    private void init() {
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
    }
}
