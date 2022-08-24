package com.jiat.foodyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.adapter.AdapterUserOderDetails;
import com.jiat.foodyapp.constants.Constants;
import com.jiat.foodyapp.model.ModelOrderDetailsUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailsSellerActivity extends AppCompatActivity {

    String sourceLatitude, sourceLongitude, desti_Latitude, desti_Longitude;
    private FirebaseAuth firebaseAuth;
    private String orderId, orderBy;
    private RecyclerView OrderD_recycler;
    private ImageButton backBtn, deliveryStatusBtn, btnEdit;
    private TextView ORDER_ID, date, status, cusEmail, cuMobile, amount, address, itemCount;
    private ArrayList<ModelOrderDetailsUser>orderedArrayList;
    private AdapterUserOderDetails adapterUserOderDetails;
    private static final String TAG = "Noti";

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