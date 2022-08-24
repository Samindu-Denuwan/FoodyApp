package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.adapter.AdapterUserOderDetails;
import com.jiat.foodyapp.model.CartItem;
import com.jiat.foodyapp.model.ModelOrderDetailsUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderDetailsUserActivity extends AppCompatActivity {

    private String orderTo, orderId;
    private RecyclerView OrderD_recycler;
    private static final String TAG = "Noti";


    private ImageButton backBtn, review;
    private TextView ORDER_ID, date, status, shopName, amount, address, itemCount;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderDetailsUser>orderedArrayList;
    private AdapterUserOderDetails adapterUserOderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_user);

        Intent intent =getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        OrderD_recycler = findViewById(R.id.orderDetailsRecycler);
        backBtn = findViewById(R.id.imgBtnBack);
        ORDER_ID = findViewById(R.id.orderIdTv);
        date = findViewById(R.id.dateTv);
        status = findViewById(R.id.statusTv);
        shopName = findViewById(R.id.shopNameTv);
        amount = findViewById(R.id.AmountTv);
        address = findViewById(R.id.addressTv);
        itemCount = findViewById(R.id.itemCountTv);
        review = findViewById(R.id.reviewBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        loadShoppingInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewBottomSheet();
            }
        });


    }

    private ImageView shopImage;
    private TextView  ShopNameTv;
    private RatingBar ratingStar;
    private EditText writeReview;
    private FloatingActionButton SUBMIT;
    private void reviewBottomSheet() {
        if (this == null) {
            return;
        }
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.review_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        shopImage = view.findViewById(R.id.shopImg);
        ShopNameTv = view.findViewById(R.id.shopName);
        ratingStar = view.findViewById(R.id.ratingBar);
        writeReview = view.findViewById(R.id.typeReviewEt);
        SUBMIT = view.findViewById(R.id.submitBtn);

        //if user has written review, load
        loadReview();
        
        //load Shop info
        loadShopInfo();
        
        SUBMIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    inputData(bottomSheetDialog);
                }
        });




        bottomSheetDialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadShopInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String shop = ""+snapshot.child("name").getValue();
                String shopImg = ""+snapshot.child("profileImage").getValue();


                ShopNameTv.setText(shop);
                try{

                    Glide.with(getApplicationContext())
                            .load(shopImg)
                            .into(shopImage);
                }catch(Exception e){

                    Glide.with(getApplicationContext())
                            .load(R.drawable.default_img)
                            .into(shopImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadReview() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            String uid = ""+snapshot.child("uid").getValue();
                            String ratings = ""+snapshot.child("ratings").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timestamp = ""+snapshot.child("timestamp").getValue();

                            float myRating = Float.parseFloat(ratings);
                            ratingStar.setRating(myRating);
                            writeReview.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData(BottomSheetDialog bottomSheetDialog) {
        String ratings = ""+ratingStar.getRating();
        String  review = writeReview.getText().toString().trim();
        //for time
        String timestamp = ""+System.currentTimeMillis();

        //set up data
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+firebaseAuth.getUid());
        hashMap.put("ratings", ""+ratings);
        hashMap.put("review", ""+review);
        hashMap.put("timestamp", ""+timestamp);

        //put to db
        //Db> Users> ShopUID> Rating

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        bottomSheetDialog.dismiss();
                        //review Added
                        Toast.makeText(OrderDetailsUserActivity.this, "Review Published Successfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadOrderedItems() {
        orderedArrayList = new ArrayList<>();

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderDetailsUser modelOrderDetailsUser = ds.getValue(ModelOrderDetailsUser.class);
                            orderedArrayList.add(modelOrderDetailsUser);
                        }
                        //setup Adapter
                        adapterUserOderDetails = new AdapterUserOderDetails(OrderDetailsUserActivity.this, orderedArrayList);
                        OrderD_recycler.setAdapter(adapterUserOderDetails);

                        //set Count
                        itemCount.setText(""+snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadOrderDetails() {
        //load order details
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String OrderBy = ""+snapshot.child("orderBy").getValue();
                        String OrderCost = ""+snapshot.child("orderCost").getValue();
                        String OrderId = ""+snapshot.child("orderId").getValue();
                        String OrderStatus = ""+snapshot.child("orderStatus").getValue();
                        String OrderTime = ""+snapshot.child("orderTime").getValue();
                        String OrderTo = ""+snapshot.child("orderTo").getValue();
                        String latitude = ""+snapshot.child("latitude").getValue();
                        String longitude = ""+snapshot.child("longitude").getValue();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(OrderTime));
                        String formatedDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

                        //change order status text color
                        if(OrderStatus.equals("In Progress")){
                            status.setTextColor(getResources().getColor(R.color.Blue));
                        }else if(OrderStatus.equals("Completed")){
                            status.setTextColor(getResources().getColor(R.color.Green));
                        }else if(OrderStatus.equals("Cancelled")){
                            status.setTextColor(getResources().getColor(R.color.red));
                        }
                        
                        ORDER_ID.setText(OrderId);
                        status.setText(OrderStatus);
                        amount.setText("LKR: "+OrderCost);
                        date.setText(formatedDate);
                        
                        findAddress(latitude, longitude);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        //Find Address

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            String Address = addresses.get(0).getAddressLine(0); //complete address
            address.setText(Address);
        }catch (Exception e){

        }


    }

    private void loadShoppingInfo() {
        /*get shopping data*/

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ShopName = ""+snapshot.child("name").getValue();
                        shopName.setText(ShopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}