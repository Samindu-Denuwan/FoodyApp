package com.jiat.foodyapp.cart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.jiat.foodyapp.CartCounter;
import com.jiat.foodyapp.OrderDetailsUserActivity;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.adapter.CartAdapter;
import com.jiat.foodyapp.adapter.CheckoutAdapter;
import com.jiat.foodyapp.constants.Constants;
import com.jiat.foodyapp.model.CartItem;
import com.jiat.foodyapp.newCart.AppDatabase;
import com.jiat.foodyapp.newCart.CartProduct;
import com.jiat.foodyapp.newCart.ProductDao;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class CartFragment extends Fragment implements LocationListener {


    private Editable UserNote;

    public CartFragment() {
        // Required empty public constructor
    }



    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private ArrayList<CartItem> cartItems;
    private CartAdapter cartAdapter;
    /*private static final String TAG = "owner";*/
    public TextView countCart;
    private ProgressDialog progressDialog;
    private String[] locationPermissions;
    private LocationManager locationManager;
    private double latitude = 0.0, longitude =0.0;
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final String TAG = "Noti";

    private FirebaseDatabase firebaseDatabase;
    public TextView subTotalTv;
    Button checkoutBtn;
    public double SUBTOTAl;
    private String uid, phone, address, MyLatitude, MyLongitude;
    private ImageView noCartImg;
    private TextView noCartTv;
    private String shopUID;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        noCartImg = view.findViewById(R.id.noItemCartImg);
        noCartTv = view.findViewById(R.id.noItemCartTv);

        noCartTv.setVisibility(View.GONE);
        noCartImg.setVisibility(View.GONE);
        shopUID = "d6RHVgGQoNZMkciEMVl16lSSsIw2";



        /*if(subTotalTv.getText().equals("LKR "+0)){
            noCartTv.setVisibility(View.VISIBLE);
            noCartImg.setVisibility(View.VISIBLE);
        }else{
            noCartTv.setVisibility(View.GONE);
            noCartImg.setVisibility(View.GONE);
        }*/





        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.cartRecycler);
        //countCart = view.findViewById(R.id.cartCount);

        subTotalTv = view.findViewById(R.id.subTotal);


        checkoutBtn = view.findViewById(R.id.btnCheckout);

        firebaseAuth = FirebaseAuth.getInstance();


        if(subTotalTv.getText().equals("LKR 0")){
            noCartTv.setVisibility(View.VISIBLE);
            noCartImg.setVisibility(View.VISIBLE);
        }else{
            noCartTv.setVisibility(View.GONE);
            noCartImg.setVisibility(View.GONE);
        }


        getRoomData();


        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (subTotalTv.getText().equals("LKR 0")) {
                    Toast.makeText(getActivity(), "Please Add to Chart Items, You want to Check Out..", Toast.LENGTH_SHORT).show();

                } else {
                   // Toast.makeText(getActivity(), " Check Out..", Toast.LENGTH_SHORT).show();

                    checkout();

                }
            }
        });



    }


    private SwitchCompat editSwitch;
    private ImageButton gps;
    private TextView tvAddress, tvMobile, subTotal_check, delivery_check, tax_check, total_check, TotalCheckout;
    private EditText etAddress, etMobile, etNote ;
    private CheckBox noteCheckbox;
    private double DeliverFee;
    private RecyclerView checkRecycler;
    private CheckoutAdapter checkoutAdapter;
    private String UserAddress, UserMobile;



    private void checkout() {


        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.checkout_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        checkRecycler = view.findViewById(R.id.checkoutRecycler);

        editSwitch = view.findViewById(R.id.SwitchDefaultDetails);
        gps = view.findViewById(R.id.gpsBtn);

        tvAddress = view.findViewById(R.id.addressTv);
        etAddress = view.findViewById(R.id.addressEt);

        tvMobile = view.findViewById(R.id.mobileTv);
        etMobile = view.findViewById(R.id.mobileEt);

        noteCheckbox = view.findViewById(R.id.addNoteCheckbox);
        etNote = view.findViewById(R.id.note);

        subTotal_check = view.findViewById(R.id.subTotalCheckout);
        delivery_check = view.findViewById(R.id.DeliveryFeeCheckout);
        tax_check = view.findViewById(R.id.TaxCheckout);
        total_check = view.findViewById(R.id.TotalCheckout);

        TotalCheckout = view.findViewById(R.id.checkout);
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        editSwitch.setChecked(false);
        etAddress.setVisibility(View.GONE);
        etMobile.setVisibility(View.GONE);
        gps.setVisibility(View.GONE);
        noteCheckbox.setChecked(false);
        etNote.setEnabled(false);




        //load Personal Data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (getActivity() == null) {
                            return;
                        }
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String accountType = ""+ds.child("accountType").getValue();
                            String name = ""+ds.child("name").getValue();
                            phone = ""+ds.child("phone").getValue();
                            address = ""+ds.child("address").getValue();
                            String email = ""+ds.child("email").getValue();
                            MyLatitude = ""+ds.child("latitude").getValue();
                            MyLongitude = ""+ds.child("longitude").getValue();
                            String timestamp = ""+ds.child("timestamp").getValue();
                            String online = ""+ds.child("online").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            uid = ""+ds.child("uid").getValue();

                            tvAddress.setText(address);
                            tvMobile.setText(phone);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //switch true
        editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    etAddress.setVisibility(View.VISIBLE);
                    etMobile.setVisibility(View.VISIBLE);
                    gps.setVisibility(View.VISIBLE);
                    tvAddress.setVisibility(View.INVISIBLE);
                    tvMobile.setVisibility(View.INVISIBLE);

                }else{
                    etAddress.setVisibility(View.GONE);
                    etMobile.setVisibility(View.GONE);
                    gps.setVisibility(View.GONE);
                    tvAddress.setVisibility(View.VISIBLE);
                    tvMobile.setVisibility(View.VISIBLE);
                }
            }
        });


        noteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    etNote.setEnabled(true);
                    etNote.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    UserNote = etNote.getText();
                }else{
                    etNote.setEnabled(false);
                    etNote.setBackgroundColor(getActivity().getResources().getColor(R.color.gray));
                    etNote.setText(null);
                    UserNote = null;
                }
            }
        });


        AppDatabase db = Room.databaseBuilder(getActivity(), AppDatabase.class, "cart_db")
                .allowMainThreadQueries().fallbackToDestructiveMigration().build();

        ProductDao productDao = db.ProductDao();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        checkRecycler.setLayoutManager(linearLayoutManager);

        List<CartProduct> cartProducts = productDao.getAllProduct();
        CheckoutAdapter checkoutAdapter = new CheckoutAdapter(cartProducts, subTotalTv);
        checkRecycler.setAdapter(checkoutAdapter);

        //set data
        tax_check.setText("LKR "+0);
       // subTotal_check.setText("LKR "+SUBTOTAl);

        int sum = 0, i;
        for (i = 0; i <cartProducts.size() ; i++) {
            sum = sum + (cartProducts.get(i).getPrice() * cartProducts.get(i).getQnt());

            subTotal_check.setText("LKR " + sum);
            SUBTOTAl = Double.parseDouble((subTotalTv.getText().toString().trim().replace("LKR ", "")));
        }

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");
        ref1.orderByChild("uid").equalTo("d6RHVgGQoNZMkciEMVl16lSSsIw2")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String fee = ""+ds.child("deliveryFee").getValue();
                             DeliverFee = Double.parseDouble(fee);
                            delivery_check.setText("LKR "+fee);
                            total_check.setText("LKR "+ (DeliverFee+ SUBTOTAl));
                            TotalCheckout.setText("LKR "+ (DeliverFee+ SUBTOTAl));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //detect Location
                if(checkLocationPermission()){
                    //already allow
                    detectLocation();
                }else{
                    //not allow, request
                    requestLocationPermission();

                }
            }
        });

        if(editSwitch.isChecked()){
            UserAddress = String.valueOf(etAddress.getText());
            UserMobile = String.valueOf(etMobile.getText());
        }

       /* if(noteCheckbox.isChecked()){
           UserNote = String.valueOf(etNote.getText());
        }else{
           UserNote = "";
        }*/

        TotalCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           /*     if (editSwitch.isChecked()) {
                    if (etAddress.equals("") || etMobile.equals("")) {
                        Toast.makeText(getActivity(), "Please Fill all fields...", Toast.LENGTH_SHORT).show();
                    } else {
                           // submitOrderWith();
                    }
                }else{
                    //default details
                    submitOrderWithout(bottomSheetDialog);

                }*/

               // Toast.makeText(getActivity(), ""+UserNote, Toast.LENGTH_SHORT).show();


                submitOrderWithout(bottomSheetDialog);

            }
        });



        bottomSheetDialog.show();
    }

    private void submitOrderWith() {
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        //for order id and order time
        String timestamp = ""+System.currentTimeMillis();
        String cost = TotalCheckout.getText().toString().trim().replace("LKR ", "");

        //setup order data
        HashMap<String, String > hashMap = new HashMap<>();
        hashMap.put("orderId", ""+timestamp);
        hashMap.put("orderTime", ""+timestamp);
        hashMap.put("orderStatus", "In Progress");
        hashMap.put("orderCost", ""+cost);
        hashMap.put("orderBy", ""+firebaseAuth.getUid());
        hashMap.put("orderTo", "d6RHVgGQoNZMkciEMVl16lSSsIw2");
        hashMap.put("orderExtraNote", ""+UserNote);

        //add to db
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Orders");
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AppDatabase db = Room.databaseBuilder(getActivity(), AppDatabase.class, "cart_db")
                                .allowMainThreadQueries().fallbackToDestructiveMigration().build();

                        ProductDao productDao = db.ProductDao();

                        List<CartProduct> cartProducts = productDao.getAllProduct();
                        for (int i = 0; i <cartProducts.size() ; i++) {
                            String pId = cartProducts.get(i).getProductID();
                            String id = String.valueOf(cartProducts.get(i).getPrimaryId());
                            String price = String.valueOf(cartProducts.get(i).getPrice());
                            String name = cartProducts.get(i).getPname();
                            String qnt = String.valueOf(cartProducts.get(i).getQnt());

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", qnt);

                            reference.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Order Placed Successfully...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitOrderWithout(BottomSheetDialog bottomSheetDialog) {
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        //for order id and order time
        final String timestamp = ""+System.currentTimeMillis();
        String cost = TotalCheckout.getText().toString().trim().replace("LKR ", "");


        //setup order data
        HashMap<String, String > hashMap = new HashMap<>();
        hashMap.put("orderId", ""+timestamp);
        hashMap.put("orderTime", ""+timestamp);
        hashMap.put("orderStatus", "In Progress");
        hashMap.put("orderCost", ""+cost);
        hashMap.put("orderBy", ""+firebaseAuth.getUid());
        hashMap.put("orderTo", shopUID);
        hashMap.put("orderExtraNote", ""+UserNote);
        hashMap.put("latitude", MyLatitude);
        hashMap.put("longitude", ""+MyLongitude);

        //add to db
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(shopUID).child("Orders");
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        AppDatabase db = Room.databaseBuilder(getActivity(), AppDatabase.class, "cart_db")
                                .allowMainThreadQueries().fallbackToDestructiveMigration().build();

                        ProductDao productDao = db.ProductDao();

                        List<CartProduct> cartProducts = productDao.getAllProduct();
                        for (int i = 0; i <cartProducts.size() ; i++) {
                            String pId = cartProducts.get(i).getProductID();
                            String id = String.valueOf(cartProducts.get(i).getPrimaryId());
                            String price = String.valueOf(cartProducts.get(i).getPrice());
                            String name = cartProducts.get(i).getPname();
                            String qnt = String.valueOf(cartProducts.get(i).getQnt());
                            String image = String.valueOf(cartProducts.get(i).getPimage());


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", qnt);
                            hashMap1.put("image", image);

                            reference.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        productDao.deleteAll();
                        getRoomData();
                        subTotalTv.setText("LKR "+0);
                        bottomSheetDialog.dismiss();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Order Placed Successfully...", Toast.LENGTH_SHORT).show();
                        noCartTv.setVisibility(View.VISIBLE);
                        noCartImg.setVisibility(View.VISIBLE);
                        Log.i(TAG, "Order Placed Successfully...");
                        prepareNotificationMessage(timestamp);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



    }


    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), locationPermissions, LOCATION_REQUEST_CODE);
    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(getActivity(), "Please Wait", Toast.LENGTH_LONG).show();

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0, this);
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
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
        if (getActivity() == null) {
            return;
        }

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(getActivity() , Locale.getDefault());


        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);

            //set Address
            etAddress.setText(address);

        }catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getActivity(), "Please Turn On Location", Toast.LENGTH_SHORT).show();
    }


    @SuppressWarnings("deprecation")
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
                        Toast.makeText(getActivity(), "Location Permission is necessary..", Toast.LENGTH_SHORT).show();

                    }
                }
            }break;

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    private void getRoomData() {
        AppDatabase db = Room.databaseBuilder(getActivity(), AppDatabase.class, "cart_db")
                .allowMainThreadQueries().fallbackToDestructiveMigration().build();

        ProductDao productDao = db.ProductDao();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        List<CartProduct> cartProducts = productDao.getAllProduct();
        CartAdapter cartAdapter = new CartAdapter(cartProducts, subTotalTv);
        recyclerView.setAdapter(cartAdapter);

        int sum = 0, i;
        for (i = 0; i <cartProducts.size() ; i++) {
            sum = sum+(cartProducts.get(i).getPrice()*cartProducts.get(i).getQnt());

            subTotalTv.setText("LKR "+sum);
           // SUBTOTAl = Double.parseDouble((subTotalTv.getText().toString().trim().replace("LKR ", "")));


        }
        if(cartProducts.size()==0){
            noCartTv.setVisibility(View.VISIBLE);
            noCartImg.setVisibility(View.VISIBLE);
        }else{
            noCartTv.setVisibility(View.GONE);
            noCartImg.setVisibility(View.GONE);
        }

    }
    
    private void prepareNotificationMessage(String orderId){
        //when user place order, send notification to seller
        
        //data for notification
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order "+ orderId;
        String NOTIFICATION_MESSAGE = "Hurry Up...! You Have a New Order";
        String NOTIFICATION_TYPE = "NewOrder";
        /*String NOTIFICATION_TYPE = "OrderPlaced";*/
        
        //JSON(what to send & where to
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        
        try {
            //send details
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid", firebaseAuth.getUid());
            notificationBodyJo.put("sellerUid", shopUID);
            notificationBodyJo.put("orderId", orderId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            
            //where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);//to all subscribe
            notificationJo.put("data", notificationBodyJo);
            
        }catch (Exception  e){
            if (getActivity() == null) {
                return;
            }
            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            
        }
        sendFcmNotification(notificationJo, orderId);


    }

    private void sendFcmNotification(JSONObject notificationJo, String orderId) {
        //send volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after send fcm start order details

               // Toast.makeText(getActivity(), "Success "+response, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), OrderDetailsUserActivity.class);
                intent.putExtra("orderTo", shopUID);
                intent.putExtra("orderId", orderId);
                getActivity().startActivity(intent);
                Log.i(TAG, "Success FCM cus");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getActivity(), "Error "+error, Toast.LENGTH_SHORT).show();
                //if failed send fcm, still start order details
                Intent intent = new Intent(getActivity(), OrderDetailsUserActivity.class);
                intent.putExtra("orderTo", shopUID);
                intent.putExtra("orderId", orderId);
                getActivity().startActivity(intent);
                Log.i(TAG, "Error FCM cus");

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required header
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key= "+ Constants.FCM_KEY);
                return headers;
            }
        };

        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);


    }


   /* private void loadCart() {


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        cartItems = new ArrayList<>();
        EasyDB easyDB = EasyDB.init(getActivity(), "ITEM_DB")
                .setTableName("ITEM_TABLE")
                .addColumn(new Column("item_id", new String[]{"text", "unique"}))
                .addColumn(new Column("item_pid", new String[]{"text", "not null"}))
                .addColumn(new Column("item_name", new String[]{"text", "not null"}))
                .addColumn(new Column("item_price", new String[]{"text", "not null"}))
                .addColumn(new Column("item_qty", new String[]{"text", "not null"}))
                .addColumn(new Column("item_image", new String[]{"text", "not null"}))
                .doneTableColumn();

        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String qty = res.getString(5);
            String image = res.getString(6);

            CartItem cartItem = new CartItem(""+id,
            ""+pId,""+name,""+price,""+qty,""+image);

            cartItems.add(cartItem);


        }
        //set up Adapter
        cartAdapter = new CartAdapter(getActivity(), cartItems);

        //set Recycler
        recyclerView.setAdapter(cartAdapter);

        loadDeliveryFee();
        *//*double itemTotal = Math.round()
        subTotalTv.setText("LKR "+);*//*

       *//* double total = Double.parseDouble((String) deliveryFeeTv.getText()) + Double.parseDouble((String) subTotalTv.getText());
        totalPriceTv.setText("LKR "+total);*//*





    }*/

/*    private void loadDeliveryFee() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo("d6RHVgGQoNZMkciEMVl16lSSsIw2")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String fee = ""+ds.child("deliveryFee").getValue();
                            DeliverFee = Double.parseDouble(fee);
                            deliveryFeeTv.setText(" "+fee);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }*/


}