package com.example.Chat365.Activity.User;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.Chat365.Model.GroupFriends;
import com.example.Chat365.Model.User;
import com.example.Chat365.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import static com.example.Chat365.Utils.MapUtils.createCustomMarker;

public class FriendNearActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int REQUEST_PERMISSIONS = 200 ;
    private GoogleMap mMap;
    boolean boolean_permission;
    Geocoder geocoder;
    List<String> listFriend;
    DatabaseReference mData;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(getBaseContext());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;

                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
        }
    }
    private void askPermission() {
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_location_friends);
        askPermission();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vị trí bạn bè trong nhóm");
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Get data
        Bundle bundle = getIntent().getBundleExtra("Bundle");
        GroupFriends friends = (GroupFriends) bundle.getSerializable("FriendsGroup");
        listFriend = friends.getListFriend();
        mData = FirebaseDatabase.getInstance().getReference();
        if(listFriend.size() > 0){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            geocoder = new Geocoder(this, Locale.getDefault());
        }
    }

    private void generateLocationUser(String idUser){
        mData.child("Users").child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                LatLng locationUser = new LatLng(user.getLocationUser().getLatitude(),
                        user.getLocationUser().getLongitude());
                mMap.addMarker(new MarkerOptions().position(locationUser).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(getApplicationContext(),user.getLinkAvatar(),user.getName()))));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(locationUser);
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
                mMap.moveCamera(cu);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        for(String id : listFriend){
                            generateLocationUser(id);
                        }
                    }
                });
            }
}
