package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.jiat.foodyapp.databinding.ActivityTrackingOrderBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackingOrderActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 1312;
    private static final String TAG = "map";
    private GoogleMap mMap;
    private ActivityTrackingOrderBinding binding;
    private String riderUid;
    private Marker marker_rider, marker_Customer;
    private FirebaseAuth firebaseAuth;
    private String rider_Latitude, rider_Longitude, riderName, my_Latitude, my_Longitude;
    private Polyline polyline;
    public LatLng customerPosition = null, riderPosition = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        riderUid = getIntent().getStringExtra("rider");
        /*Log.i(TAG, riderUid);*/
        binding = ActivityTrackingOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
       // loadMyLocation();
       // loadRiderLocation();
    }

    private void loadRiderLocation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(riderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rider_Latitude = ""+snapshot.child("latitude").getValue();
                        rider_Longitude = ""+snapshot.child("longitude").getValue();
                        riderName = ""+snapshot.child("name").getValue();



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyLocation() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        my_Latitude = ""+snapshot.child("latitude").getValue();
                        my_Longitude = ""+snapshot.child("longitude").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style);
        mMap.setMapStyle(style);


        if (checkPermissions()) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, LOCATION_REQUEST_CODE
            );
        }


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        my_Latitude = ""+snapshot.child("latitude").getValue();
                        my_Longitude = ""+snapshot.child("longitude").getValue();


                        double Cus_lat = Double.parseDouble(my_Latitude);
                        double Cus_longi = Double.parseDouble(my_Longitude);
                        customerPosition = new LatLng(Cus_lat, Cus_longi);

                        if(marker_Customer == null){
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark));
                            markerOptions.title("You");
                            markerOptions.position(customerPosition);
                            marker_Customer = mMap.addMarker(markerOptions);
                            moveCamera(customerPosition);

                        }else{
                            marker_Customer.setPosition(customerPosition);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
        reference1.child(riderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        rider_Latitude = ""+snapshot.child("latitude").getValue();
                        rider_Longitude = ""+snapshot.child("longitude").getValue();
                        riderName = ""+snapshot.child("name").getValue();


                         double rider_lat = Double.parseDouble(rider_Latitude);
                         double rider_longi = Double.parseDouble(rider_Longitude);
                         riderPosition = new LatLng(rider_lat, rider_longi);

                        if(marker_rider == null){
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.m1));
                            markerOptions.title("Rider: "+riderName);
                            markerOptions.position(riderPosition);
                            marker_rider = mMap.addMarker(markerOptions);


                            }else{
                             marker_rider.setPosition(riderPosition);
                        }

                        getDirection(customerPosition, riderPosition);
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });














    }







    public void getDirection(LatLng start, LatLng end) {
        okhttp3.OkHttpClient client = new OkHttpClient();

        String URL = "https://maps.googleapis.com/maps/api/directions/json?origin="
                +start.latitude
                +","
                +start.longitude
                +"&destination="
                +end.latitude
                +","
                +end.longitude
                +"&alternatives=true&key="
                +getString(R.string.direction_api_key);

        Log.i(TAG, URL);


        okhttp3.Request request = new Request.Builder()
                .url(URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray routes = jsonObject.getJSONArray("routes");

                    Log.i(TAG, routes.length()+"");

                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");

                    Log.i(TAG, overviewPolyline.toString());

                    List<LatLng> points = PolyUtil.decode((overviewPolyline.getString("points")));



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(polyline == null){
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.width(10);
                                polylineOptions.color(getColor(R.color.orange));

                                polylineOptions.addAll(points);
                                polyline = mMap.addPolyline(polylineOptions);
                            }else{
                                polyline.setPoints(points);
                            }

                        }
                    });




                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        });

    }

    public void moveCamera(LatLng latLng){
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(15f)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }


    public boolean checkPermissions() {
        boolean permissions = false;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            permissions = true;
        }
        return permissions;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionsGranted = false;
        if (requestCode == LOCATION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                        &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsGranted = true;
                } else if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                        &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsGranted = true;

                }

            }
            if (permissionsGranted) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

}