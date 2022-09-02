package com.jiat.foodyapp.seller.orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jiat.foodyapp.R;
import com.jiat.foodyapp.seller.adapter.AdapterOrderSeller;
import com.jiat.foodyapp.seller.model.ModelOrderSeller;

import java.util.ArrayList;


public class SellerOrdersFragment extends Fragment {

    private TextView FilterTv;
    private ImageButton BtnFilter;
    private RecyclerView orderSellerRecycler;
    private ArrayList<ModelOrderSeller>orderSellerArrayList;
    private AdapterOrderSeller adapterOrderSeller;
    private FirebaseAuth  firebaseAuth;

    public SellerOrdersFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_orders, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Orders");
        FilterTv = view.findViewById(R.id.filterOrderTv);
        BtnFilter = view.findViewById(R.id.filterBtn);
        orderSellerRecycler = view.findViewById(R.id.order_recycler_seller);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllOrders();

        BtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {"All", "In Progress", "Completed", "Cancelled", "Ready to Deliver","On the way", "Delivered"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Filter Orders: ")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if (which ==0){
                                    //All Click
                                    FilterTv.setText("Showing All Orders");
                                    adapterOrderSeller.getFilter().filter("");
                                }else{
                                    String optionClicked = options[which];
                                    FilterTv.setText("Showing "+optionClicked+" Orders");
                                    adapterOrderSeller.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });
        return view;

    }

    private void loadAllOrders() {
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
                        adapterOrderSeller = new AdapterOrderSeller(getActivity(), orderSellerArrayList);
                        orderSellerRecycler.setAdapter(adapterOrderSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}