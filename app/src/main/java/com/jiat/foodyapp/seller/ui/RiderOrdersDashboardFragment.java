package com.jiat.foodyapp.seller.ui;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.adapter.AdapterOrderSeller;
import com.jiat.foodyapp.seller.adapter.RiderAdapter;
import com.jiat.foodyapp.seller.adapter.RiderAdapterDash;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;
import com.jiat.foodyapp.seller.model.RiderModel;

import java.util.ArrayList;
import java.util.Random;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class RiderOrdersDashboardFragment extends Fragment {

    private RecyclerView recyclerViewRider, orderRecycler;
    private RiderAdapterDash riderAdapterDash;
    private ArrayList<RiderModel> riderList;

    private ArrayList<ModelOrderSeller>orderSellerArrayList;
    private AdapterOrderSeller adapterOrderSeller;
    private FirebaseAuth firebaseAuth;
    private String riderUid = null;
    private String riderName = null;
    private TextView nameRider;

    public RiderOrdersDashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider_orders_dashboard, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Rider Orders");
        recyclerViewRider = view.findViewById(R.id.riderRecycle);
        orderRecycler = view.findViewById(R.id.order_recycler);
        nameRider = view.findViewById(R.id.RiderName);
        firebaseAuth = FirebaseAuth.getInstance();
        loadRiders();
        //loadOrders();

        orderSellerArrayList = new ArrayList<>();
        if(orderSellerArrayList.size() == 0){
            nameRider.setText("No Rider Selected..");
        }



        return view;
    }

    public void loadOrders() {

        Random random = new Random();



        EasyDB easyDB = EasyDB.init(getActivity(), "DB_RIDER2")
                .setTableName("RIDER2_TABLE")
                .addColumn(new Column("rid", new String[]{"text", "unique"}))
                .addColumn(new Column("rider_id", new String[]{"text", "not null"}))
                .addColumn(new Column("rider_name", new String[]{"text", "not null"}))
                .doneTableColumn();




        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            riderUid  = res.getString(2);
            riderName =  res.getString(3);

            nameRider.setText(riderName);

        }
        /*orderSellerArrayList = new ArrayList<>();*/

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Orders");
        reference1.orderByChild("rider").equalTo(riderUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderSellerArrayList.clear();
                        if(snapshot.exists()){
                            for(DataSnapshot ds: snapshot.getChildren()) {
                                ModelOrderSeller modelOrderSeller = ds.getValue(ModelOrderSeller.class);
                                orderSellerArrayList.add(modelOrderSeller);

                            }



                        }
                        adapterOrderSeller = new AdapterOrderSeller(getActivity(), orderSellerArrayList);
                        orderRecycler.setAdapter(adapterOrderSeller);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void loadRiders() {
        riderList = new ArrayList<>();
        riderAdapterDash = new RiderAdapterDash(getActivity(), riderList, RiderOrdersDashboardFragment.this);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("Rider").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                riderList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    RiderModel riderModel = ds.getValue(RiderModel.class);
                    String status = ""+ds.child("registerStatus").getValue();
                    if(status.equals("true")) {
                        riderList.add(riderModel);
                    }


                }
                recyclerViewRider.setAdapter(riderAdapterDash);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}