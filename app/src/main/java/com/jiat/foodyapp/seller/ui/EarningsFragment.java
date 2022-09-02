package com.jiat.foodyapp.seller.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.jiat.foodyapp.seller.adapter.AdapterSellerEarning;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;

import java.util.ArrayList;


public class EarningsFragment extends Fragment {


    private RecyclerView recyclerView;
    public TextView totalTv;
    private AdapterSellerEarning adapterSellerEarning;
    private ArrayList<ModelOrderSeller>orderSellerArrayList;
    private FirebaseAuth firebaseAuth;
    public EarningsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Earnings");
        recyclerView = view.findViewById(R.id.EarnRecycler);
        totalTv = view.findViewById(R.id.TotalTv);
        firebaseAuth = FirebaseAuth.getInstance();
        loadOrders();
        return view;
    }

    private void loadOrders() {
        orderSellerArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderSellerArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderSeller modelOrderSeller = ds.getValue(ModelOrderSeller.class);
                            orderSellerArrayList.add(modelOrderSeller);
                        }
                        adapterSellerEarning = new AdapterSellerEarning(getActivity(), orderSellerArrayList, totalTv);
                        adapterSellerEarning.getFilter().filter("Delivered");
                        recyclerView.setAdapter(adapterSellerEarning);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}