package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.adapter.AdapterUserOderDetails;
import com.jiat.foodyapp.constants.Constants;
import com.jiat.foodyapp.model.ModelOrderDetailsUser;
import com.jiat.foodyapp.seller.adapter.RiderAdapter;
import com.jiat.foodyapp.seller.adapter.RiderAdapterOrder;
import com.jiat.foodyapp.seller.model.RiderModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    String sourceLatitude, sourceLongitude, desti_Latitude, desti_Longitude;
    private FirebaseAuth firebaseAuth;
    private String orderId, orderBy;
    private RecyclerView OrderD_recycler;
    private ImageButton backBtn, deliveryStatusBtn, btnEdit, btnRiderInfo;
    private TextView ORDER_ID, date, status, cusEmail, cuMobile, amount, address, itemCount, RiderName;
    private ArrayList<ModelOrderDetailsUser>orderedArrayList;
    private AdapterUserOderDetails adapterUserOderDetails;
    private static final String TAG = "Noti";

    public BottomSheetDialog bottomSheetDialog;

    private ArrayList<RiderModel> riderList;
    private RiderAdapterOrder riderAdapterOrder;
    private String RIDER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);


        orderId = getIntent().getStringExtra("orderId");
        orderBy = getIntent().getStringExtra("orderBy");


        OrderD_recycler = findViewById(R.id.orderDetailsRecycler);
        backBtn = findViewById(R.id.imgBtnBack);
        ORDER_ID = findViewById(R.id.orderIdTv);
        date = findViewById(R.id.dateTv);
        status = findViewById(R.id.statusTv);
        cusEmail = findViewById(R.id.cusEmailTv);
        amount = findViewById(R.id.AmountTv);
        address = findViewById(R.id.addressTv);
        itemCount = findViewById(R.id.itemCountTv);
        cuMobile = findViewById(R.id.mobileTv);
        deliveryStatusBtn = findViewById(R.id.deliveryStatBtn);
        btnEdit = findViewById(R.id.btnEditBack);
        btnRiderInfo = findViewById(R.id.riderDetailsBtn);




        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadCustomerInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editOrderStatusDialog();
            }
        });

        btnRiderInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 if(status.getText().equals("In Progress")||status.getText().equals("Completed")||status.getText().equals("Cancelled")) {

                     Toast.makeText(OrderDetailsSellerActivity.this, "Rider Info Not Available yet...", Toast.LENGTH_SHORT).show();
                }else {
                     riderInfoBottomSheet();
                }
            }
        });




            deliveryStatusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(status.getText().equals("Completed")) {
                        //deliverStatus();
                        onTheWayBottomSheet();

                    }else{
                        Toast.makeText(OrderDetailsSellerActivity.this, "Select Rider Not Available this time..,", Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }

    private ImageView imgProfileBtn;
    private TextView Name, Mobile, Email;

    private String userFullName, phoneNumber, addressUser;
    private void riderInfoBottomSheet() {

        EasyDB easyDB = EasyDB.init(this, "DELIVER_DB")
                .setTableName("DELIVER_TABLE")
                .addColumn(new Column("deliver_id", new String[]{"text", "unique"}))
                .addColumn(new Column("rider_id", new String[]{"text", "not null"}))
                .addColumn(new Column("rider_name", new String[]{"text", "not null"}))
                .addColumn(new Column("rider_mobile", new String[]{"text", "not null"}))
                .addColumn(new Column("rider_img", new String[]{"text", "not null"}))
                .doneTableColumn();

        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String deliverID = res.getString(1);
            String riderId = res.getString(2);
            String rider = res.getString(3);
            String riderMobile = res.getString(4);
            String riderImg = res.getString(5);
            RIDER_ID = riderId;

        }

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.rider_profile_bottomsheet, null);
            bottomSheetDialog.setContentView(view);

            imgProfileBtn = view.findViewById(R.id.imageViewUser);
            Name =view.findViewById(R.id.FullName);
            Email = view.findViewById(R.id.Email);
            Mobile= view.findViewById(R.id.PhoneNum);

            //checkUser();

            //load user info
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.orderByChild("uid").equalTo(RIDER_ID)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot ds: snapshot.getChildren()){
                                String accountType = ""+ds.child("accountType").getValue();
                                String name = ""+ds.child("name").getValue();
                                String phone = ""+ds.child("phone").getValue();
                                String address = ""+ds.child("address").getValue();
                                String email = ""+ds.child("email").getValue();
                                String timestamp = ""+ds.child("timestamp").getValue();
                                String online = ""+ds.child("online").getValue();
                                String profileImage = ""+ds.child("profileImage").getValue();
                                String uid = ""+ds.child("uid").getValue();

                                Email.setText(email);
                                Name.setText(name);
                                Mobile.setText(phone);


                                Glide.with(OrderDetailsSellerActivity.this)
                                        .load(profileImage)
                                        .circleCrop()
                                        .into(imgProfileBtn);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        bottomSheetDialog.show();

    }


    private RecyclerView riderRecycleView;
    private void onTheWayBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delivery_status_bottomsheet, null);
        bottomSheetDialog.setContentView(view);

        riderRecycleView = view.findViewById(R.id.rider_recycler);
        loadRiders();

        bottomSheetDialog.show();




    }

    public void dismissBottomSheet(){
        bottomSheetDialog.dismiss();
    }

    private void loadRiders() {
        riderList = new ArrayList<>();
        riderAdapterOrder = new RiderAdapterOrder(this, riderList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(OrderDetailsSellerActivity.this, 2,RecyclerView.VERTICAL, false );


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("Rider").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                riderList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String status = ""+ds.child("online").getValue();
                    if(status.equals("true")) {
                        RiderModel riderModel = ds.getValue(RiderModel.class);
                        riderList.add(riderModel);
                    }

                }
                riderRecycleView.setLayoutManager(gridLayoutManager);
                riderRecycleView.setAdapter(riderAdapterOrder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deliverStatus() {
        EasyDB easyDB = EasyDB.init(this, "DELIVER_DB")
                .setTableName("DELIVER_TABLE")
                .addColumn(new Column("deliver_id", new String[]{"text", "unique"}))
                .addColumn(new Column("rider_id", new String[]{"text", "not null"}))
                .addColumn(new Column("rider_name", new String[]{"text", "not null"}))
                .addColumn(new Column("rider_mobile", new String[]{"text", "not null"}))
                .addColumn(new Column("rider_img", new String[]{"text", "not null"}))
                .doneTableColumn();

        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String deliverID = res.getString(1);
            String riderId = res.getString(2);
            String rider = res.getString(3);
            String riderMobile = res.getString(4);
            String riderImg = res.getString(5);
            RIDER_ID = riderId;

        }

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", ""+"On the way");
        hashMap.put("rider", ""+RIDER_ID);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String message = "Order is now On the way";
                        Toast.makeText(OrderDetailsSellerActivity.this, message, Toast.LENGTH_SHORT).show();

                        prepareNotificationMessage(orderId, message);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsSellerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void editOrderStatusDialog() {

        final String[] options = {"In Progress", "Completed", "Cancelled"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status: ")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      String selectionOption = options[i];
                      editOrderStatus(selectionOption);
                    }
                })
                .show();
    }

    private void editOrderStatus(final String selectionOption) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", ""+selectionOption);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String message = "Order is now "+selectionOption;
                        Toast.makeText(OrderDetailsSellerActivity.this, message, Toast.LENGTH_SHORT).show();

                        prepareNotificationMessage(orderId, message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderDetailsSellerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadOrderedItems() {
        orderedArrayList = new ArrayList<>();

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderDetailsUser modelOrderDetailsUser = ds.getValue(ModelOrderDetailsUser.class);
                            orderedArrayList.add(modelOrderDetailsUser);
                        }
                        //setup Adapter
                        adapterUserOderDetails = new AdapterUserOderDetails(OrderDetailsSellerActivity.this, orderedArrayList);
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
        reference.child(firebaseAuth.getUid()).child("Orders").child(orderId)
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

    private void loadCustomerInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(orderBy)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       desti_Latitude = ""+snapshot.child("latitude").getValue();
                        desti_Longitude = ""+snapshot.child("longitude").getValue();
                        String email = ""+snapshot.child("email").getValue();
                        String phone  = ""+snapshot.child("phone").getValue();

                        cusEmail.setText(email);
                        cuMobile.setText(phone);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMyInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sourceLatitude = ""+snapshot.child("latitude").getValue();
                        sourceLongitude = ""+snapshot.child("longitude").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void prepareNotificationMessage(String orderId, String message){
        //when Seller change order status, in progress, completed, cancelled, send notification to user

        //data for notification
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order "+ orderId;
        String NOTIFICATION_MESSAGE = ""+ message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        //JSON(what to send & where to
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            //send details
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", orderBy);
            notificationBodyJo.put("sellerUid", firebaseAuth.getUid());
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);//to all subscribe
            notificationJo.put("data", notificationBodyJo);

        }catch (Exception  e){

            Toast.makeText(OrderDetailsSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }
        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //send notification
                Log.i(TAG, "Success send FCM Seller");
               // Toast.makeText(OrderDetailsSellerActivity.this, "Success "+response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // failed
              //  Toast.makeText(OrderDetailsSellerActivity.this, "Error "+error, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Failed send FCM Seller");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required header
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key="+ Constants.FCM_KEY);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }

}