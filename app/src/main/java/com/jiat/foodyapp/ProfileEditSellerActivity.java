package com.jiat.foodyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfileEditSellerActivity extends AppCompatActivity implements LocationListener {

    private ImageView imgProfileBtn;
    private EditText editTextAddressSeller, editTextFullNameSeller, editTextPhoneNumSeller, shopName;
    private SwitchCompat shopOpenSwitch;
    private static final String TAG = "owner";
    private static final int LOCATION_REQUEST_CODE = 100;

    private String[] locationPermissions;

    //image Choose Uri
    private Uri image_uri;

    private double latitude = 0.0, longitude = 0.0;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_seller);

        imgProfileBtn = findViewById(R.id.imageViewSeller);
        ImageButton imgBtnBack = findViewById(R.id.imgBtnBack);
        ImageButton imgBtnGps = findViewById(R.id.imgBtnGetGps);
        shopName = findViewById(R.id.editTextShopSR);
        Button btnUpdateProfileSeller = findViewById(R.id.buttonEditProfileSeller);
        editTextAddressSeller = findViewById(R.id.editTextAddressSR);
        editTextFullNameSeller = findViewById(R.id.editTextFullNameSR);
        editTextPhoneNumSeller = findViewById(R.id.editTextPhoneNumSR);
        shopOpenSwitch = findViewById(R.id.switchShopOpen);

        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();


        Glide.with(this)
                .load(R.drawable.profile)
                .circleCrop()
                .into(imgProfileBtn);

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                onBackPressed();
                Log.i(TAG, "Back button pressed");
            }
        });

        imgBtnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //detect Location
                if (checkLocationPermission()) {
                    //already allow
                    detectLocation();
                } else {
                    //not allow, request
                    requestLocationPermission();

                }

            }
        });
        imgProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //image choose
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                //startActivityForResult(Intent.createChooser(intent, "Select Image"),IMAGE_REQUEST );
                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));

            }
        });

        btnUpdateProfileSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });


    }
    private String sellerFullName, phoneNumber, addressSeller, shopNameSeller;
    private boolean shopOpenBoolean;
    private void inputData() {
        //input data
        sellerFullName = editTextFullNameSeller.getText().toString().trim();
        phoneNumber = editTextPhoneNumSeller.getText().toString().trim();
        addressSeller = editTextAddressSeller.getText().toString().trim();
        shopNameSeller = shopName.getText().toString().trim();
        shopOpenBoolean = shopOpenSwitch.isChecked(); //true/ false

        //Data Validate

        if(TextUtils.isEmpty(sellerFullName)){
            Toast.makeText(this, "Enter Name..", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Enter Phone Number..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(shopNameSeller)){
            Toast.makeText(this, "Enter Shop Name..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(addressSeller)){
            Toast.makeText(this, "Please click GPS Button to detect Location..", Toast.LENGTH_SHORT).show();
            return;
        }
        if(latitude == 0.0 ){
            Toast.makeText(this, "Please click GPS Button to detect Location..", Toast.LENGTH_SHORT).show();
            return;
        }



        updateProfile();
    }

    private void updateProfile() {
        progressDialog.setMessage("Updating Profile...");
        progressDialog.show();

        if(image_uri == null){
            //update without image

            //setup data to save

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", "" + sellerFullName);
            hashMap.put("phone", "" + phoneNumber);
            hashMap.put("shopName", "" + shopNameSeller);
            hashMap.put("shopOpen", "" + shopOpenBoolean);
            hashMap.put("address", "" + addressSeller);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);

            /*hashMap.put("profileImage", "");*/

            //update to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //updated
                            Log.i(TAG, "Profile updated..");
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditSellerActivity.this, "Profile Updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed to update
                            progressDialog.dismiss();
                            Toast.makeText(ProfileEditSellerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }else{
            //update with image

            //image first upload
            String filePathAndName = "profile_images/" +""+ firebaseAuth.getUid();

            //getStorage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if(uriTask.isSuccessful()){
                                //image url receive, now update

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("name", "" + sellerFullName);
                                hashMap.put("phone", "" + phoneNumber);
                                hashMap.put("shopName", "" + shopNameSeller);
                                hashMap.put("shopOpen", "" + shopOpenBoolean);
                                hashMap.put("address", "" + addressSeller);
                                hashMap.put("latitude", "" + latitude);
                                hashMap.put("longitude", "" + longitude);
                                hashMap.put("profileImage", "" + downloadImageUri);

                                //update to db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //updated
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileEditSellerActivity.this, "Profile Updated...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed to update
                                                progressDialog.dismiss();
                                                Toast.makeText(ProfileEditSellerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileEditSellerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //load user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            String name = ""+ds.child("name").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String shop = ""+ds.child("shopName").getValue();
                            String address = ""+ds.child("address").getValue();
                            String shopOpen = ""+ds.child("shopOpen").getValue();
                            String email = ""+ds.child("email").getValue();
                            latitude = Double.parseDouble(""+ds.child("latitude").getValue());
                            longitude = Double.parseDouble(""+ds.child("longitude").getValue());
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String online = ""+ds.child("online").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String uid = ""+ds.child("uid").getValue();

                            editTextAddressSeller.setText(address);
                            editTextFullNameSeller.setText(name);
                            editTextPhoneNumSeller.setText(phone);
                            shopName.setText(shop);

                            if(shopOpen.equals("true")){
                                shopOpenSwitch.setChecked(true);
                            }else{
                                shopOpenSwitch.setChecked(false);
                            }


                            try {
                                Glide.with(ProfileEditSellerActivity.this)
                                        .load(profileImage)
                                        .circleCrop()
                                        .into(imgProfileBtn);

                            }catch (Exception e){
                                Glide.with(ProfileEditSellerActivity.this)
                                        .load(R.drawable.profile)
                                        .circleCrop()
                                        .into(imgProfileBtn);

                            }



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "Please Wait", Toast.LENGTH_LONG).show();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0, this);
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {//location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();

    }

    private void findAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);

            //set Address
            editTextAddressSeller.setText(address);

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps location disable
        Toast.makeText(this, "Please Turn On Location", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permission Allow
                        detectLocation();
                    }else{
                        //permission Denied
                        Toast.makeText(this, "Location Permission is necessary..", Toast.LENGTH_SHORT).show();

                    }
                }
            }break;

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_uri = result.getData().getData();
                        Glide.with(ProfileEditSellerActivity.this).load(image_uri).circleCrop().into(imgProfileBtn);
                        Log.i(TAG, image_uri.getPath());
                    }
                }
            }
    );
}