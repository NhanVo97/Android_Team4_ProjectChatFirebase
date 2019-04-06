package com.example.Chat365.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.Chat365.Adapter.OnlineAdapter;
import com.example.Chat365.Fragment.FragmentGroup;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;

import java.util.List;

public class OnlineActivity extends AppCompatActivity {
    private ListView lv;
    private List<User> listRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Ai đang Online");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_online);
        // Anh Xa
        lv = findViewById(R.id.listOnline);
        // get User online
        listRoom = FragmentGroup.listOnline;
        Log.d("ONLINE",listRoom.size()+"");
        OnlineAdapter onlineAdapter = new OnlineAdapter(getApplicationContext(),listRoom);
        lv.setAdapter(onlineAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User u = listRoom.get(position);
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User",u);
                intent.putExtra("UserBundle",bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id==android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
