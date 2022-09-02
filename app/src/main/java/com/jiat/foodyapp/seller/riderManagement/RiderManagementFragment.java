package com.jiat.foodyapp.seller.riderManagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.ViewItemSellerActivity;
import com.jiat.foodyapp.adapter.ItemAdapter;
import com.jiat.foodyapp.seller.adapter.AdapterOrderSeller;
import com.jiat.foodyapp.seller.adapter.RiderAdapter;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;
import com.jiat.foodyapp.seller.model.RiderModel;

import java.util.ArrayList;

public class RiderManagementFragment extends Fragment {

    private RecyclerView RiderRecyclerView;
    private String UID;

    private ArrayList<RiderModel> riderList;
    private RiderAdapter riderAdapter;




    public RiderManagementFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider_management, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Riders");
        RiderRecyclerView = view.findViewById(R.id.rider_recycler);


        loadRiders();

        return view;
    }



    private void loadRiders() {
        riderList = new ArrayList<>();
        riderAdapter = new RiderAdapter(getActivity(), riderList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2,RecyclerView.VERTICAL, false );


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("Rider").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                riderList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    RiderModel riderModel = ds.getValue(RiderModel.class);
                    riderList.add(riderModel);


                }
                RiderRecyclerView.setLayoutManager(gridLayoutManager);
                RiderRecyclerView.setAdapter(riderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}